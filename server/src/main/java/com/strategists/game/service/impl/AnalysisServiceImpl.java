package com.strategists.game.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.service.AnalysisService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AnalysisServiceImpl implements AnalysisService {

	private static final String PLAYER_COLUMN = "Player";
	private static final String RANK_COLUMN = "Rank";

	@Value("${strategists.analysis.export-directory}")
	private File exportDirectory;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@PostConstruct
	public void setup() {
		if (!exportDirectory.exists()) {
			Assert.state(exportDirectory.mkdirs(), "Unable to create export directory!");
		}
		log.info("Export Directory: {}", exportDirectory.getAbsolutePath());
	}

	@Override
	public void export() {
		log.info("Exporting game data as CSV...");

		// Creating CSV formatter
		val headers = getCSVHeaders();
		val format = CSVFormat.DEFAULT.builder().setHeader(headers.toArray(String[]::new)).build();

		// Sorting players as per their rank
		val players = playerService.getPlayers();
		players.sort((Player a, Player b) -> Double.compare(b.getNetWorth(), a.getNetWorth()));

		// Preparing CSV file
		try (final CSVPrinter printer = new CSVPrinter(new FileWriter(getCSVFilename()), format)) {

			// Adding rows to the CSV file
			for (int rank = 1; rank <= players.size(); rank++) {

				// Printing the row to the CSV
				printer.printRecord(getCSVRow(headers, players.get(rank - 1), rank));

			}

			log.info("Export completed!");
		} catch (IOException ex) {
			log.error("Unable to export data! Message: {}", ex.getMessage(), ex);
		}
	}

	private String getCSVFilename() {
		return String.format("%sexport-%s.csv", exportDirectory, System.currentTimeMillis());
	}

	private List<String> getCSVHeaders() {
		// Preparing a list to store all headers
		val headers = new ArrayList<String>(2 + landService.getCount());

		// Adding constant columns
		headers.add(PLAYER_COLUMN);
		headers.add(RANK_COLUMN);

		// Adding land names as columns
		landService.getLands().stream().map(Land::getName).forEach(headers::add);
		return headers;
	}

	private List<Object> getCSVRow(List<String> headers, Player player, int rank) {
		// Mapping player's investment to a map
		val lands = player.getPlayerLands().stream()
				.collect(Collectors.toMap(pl -> pl.getLand().getName(), PlayerLand::getOwnership));

		// Preparing a row to be added in the CSV
		val row = new ArrayList<>(headers.size());
		for (String header : headers) {
			if (PLAYER_COLUMN.equals(header)) {
				row.add(player.getUsername());
			} else if (RANK_COLUMN.equals(header)) {
				row.add(rank);
			} else {
				row.add(lands.containsKey(header) ? lands.get(header) : 0);
			}
		}
		return row;
	}

}
