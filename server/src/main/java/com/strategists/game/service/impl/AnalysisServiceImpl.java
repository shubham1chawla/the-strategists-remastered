package com.strategists.game.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.AnalysisService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.util.MathUtil;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AnalysisServiceImpl implements AnalysisService {

	private class Column {
		private static final String TIMESTAMP = "timestamp";
		private static final String PLAYER = "player";
		private static final String BASE_CASH = "base-cash";
		private static final String STATE = "state";
		private static final String BANKRUPTCY_ORDER = "bankruptcy-order";
		private static final String INVESTMENT_DEBITS = "investment-debits";
		private static final String RENT_DEBITS = "rent-debits";
		private static final String RENT_CREDITS = "rent-credits";
		private static final String AVERAGE_OWNERSHIP = "average-ownership";
	}

	@Value("${strategists.analysis.export-data-directory}")
	private File exportDataDirectory;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@PostConstruct
	public void setup() {
		if (!exportDataDirectory.exists()) {
			Assert.state(exportDataDirectory.mkdirs(), "Unable to create export data directory!");
		}
		log.info("Export Data Directory: {}", exportDataDirectory.getAbsolutePath());
	}

	@Override
	public void exportGameData() {
		log.info("Exporting game data as CSV...");

		// Creating CSV formatter
		val headers = getCSVHeaders();
		val format = CSVFormat.DEFAULT.builder().setHeader(headers.toArray(String[]::new)).build();

		// Sorting players as per their rank
		val orderedPlayers = getPlayersOrderByBankruptcy();

		// Preparing CSV file
		val timestamp = System.currentTimeMillis();
		val csv = new File(exportDataDirectory, String.format("export-%s.csv", timestamp));
		try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csv), format)) {

			// Adding rows to the CSV file
			for (int order = 1; order <= orderedPlayers.size(); order++) {
				printer.printRecord(getCSVRow(headers, orderedPlayers.get(order - 1), order, timestamp));
			}

			log.info("Export completed!");
		} catch (IOException ex) {
			log.error("Unable to export data! Message: {}", ex.getMessage(), ex);
		}
	}

	private List<String> getCSVHeaders() {
		// Preparing a list to store all headers
		val headers = new ArrayList<String>(2 + landService.getCount());

		// Adding constant columns
		headers.add(Column.TIMESTAMP);
		headers.add(Column.PLAYER);
		headers.add(Column.BASE_CASH);
		headers.add(Column.STATE);
		headers.add(Column.BANKRUPTCY_ORDER);
		headers.add(Column.INVESTMENT_DEBITS);
		headers.add(Column.RENT_DEBITS);
		headers.add(Column.RENT_CREDITS);
		headers.add(Column.AVERAGE_OWNERSHIP);

		// Adding land names as columns
		landService.getLands().stream().map(Land::getName).map(String::toLowerCase).forEach(headers::add);
		return headers;
	}

	private List<Player> getPlayersOrderByBankruptcy() {
		// Mapping players with their user name
		val players = playerService.getPlayers().stream()
				.collect(Collectors.toMap(Player::getUsername, Function.identity()));

		// Adding players in order of bankruptcy
		val orderedPlayers = new ArrayList<Player>(players.size());
		for (Activity activity : activityRepository.findByType(Type.BANKRUPTCY)) {
			orderedPlayers.add(players.get(activity.getVal1()));
		}

		// Adding winner to the ordered players
		for (Player player : players.values()) {
			if (!player.isBankrupt()) {
				orderedPlayers.add(player);
			}
		}
		return orderedPlayers;
	}

	private List<Object> getCSVRow(List<String> headers, Player player, int order, long timestamp) {
		val lands = player.getPlayerLands().stream()
				.collect(Collectors.toMap(pl -> pl.getLand().getName().toLowerCase(), PlayerLand::getOwnership));

		// Preparing a row to be added in the CSV
		val row = new ArrayList<>(headers.size());
		for (String header : headers) {
			switch (header) {
			case Column.TIMESTAMP:
				row.add(timestamp);
				break;
			case Column.PLAYER:
				row.add(player.getUsername());
				break;
			case Column.BASE_CASH:
				row.add(player.getBaseCash());
				break;
			case Column.STATE:
				row.add(player.getState());
				break;
			case Column.BANKRUPTCY_ORDER:
				row.add(order);
				break;
			case Column.INVESTMENT_DEBITS:
				row.add(MathUtil.sum(player.getPlayerLands(), PlayerLand::getBuyAmount));
				break;
			case Column.RENT_DEBITS:
				row.add(MathUtil.sum(player.getPaidRents(), Rent::getRentAmount));
				break;
			case Column.RENT_CREDITS:
				row.add(MathUtil.sum(player.getReceivedRents(), Rent::getRentAmount));
				break;
			case Column.AVERAGE_OWNERSHIP:
				val average = MathUtil.sum(player.getPlayerLands(), PlayerLand::getOwnership) / landService.getCount();
				row.add(MathUtil.round(average));
				break;
			default:
				row.add(lands.containsKey(header) ? lands.get(header) : 0);
			}
		}
		return row;
	}

}
