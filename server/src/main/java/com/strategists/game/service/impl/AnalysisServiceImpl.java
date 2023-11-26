package com.strategists.game.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.strategists.game.entity.Trend;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.AnalysisService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.util.MathUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AnalysisServiceImpl implements AnalysisService {

	@Value("${strategists.analysis.export-data-directory}")
	private File exportDataDirectory;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

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
				val row = new CSVRow(timestamp, order, orderedPlayers.get(order - 1));
				printer.printRecord(row.getValues(headers));
			}

			log.info("Export completed!");
		} catch (IOException ex) {
			log.error("Unable to export data! Message: {}", ex.getMessage(), ex);
		}
	}

	private List<String> getCSVHeaders() {
		val headers = new ArrayList<String>();
		val landNames = landService.getLands().stream().map(Land::getName).toList();

		for (Field field : CSVColumn.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && String.class.equals(field.getType())) {
				try {
					val header = (String) field.get(null);
					if (CSVColumn.FORMATS.contains(header)) {
						landNames.forEach(name -> headers.add(String.format(header, name)));
					} else {
						headers.add(header);
					}
				} catch (Exception ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		}

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

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private class CSVColumn {
		// Game-related columns
		private static final String GAME_EXPORT_TIMESTAMP = "game.export.timestamp";
		private static final String GAME_BANKRUPTCY_ORDER = "game.bankruptcy-order";

		// Player-related columns
		private static final String PLAYER_USERNAME = "player.username";
		private static final String PLAYER_BASE_CASH = "player.base-cash";
		private static final String PLAYER_STATE = "player.state";

		// Ownership-related columns
		private static final String OWNERSHIP_TOTAL = "ownership.total";
		private static final String OWNERSHIP_COUNT = "ownership.count";
		private static final String OWNERSHIP_LAND_FMT = "ownership.%s";

		// Debits-related columns
		private static final String DEBIT_TOTAL = "debit.total";
		private static final String DEBIT_COUNT = "debit.count";
		private static final String DEBIT_INVEST_TOTAL = "debit.invest.total";
		private static final String DEBIT_INVEST_COUNT = "debit.invest.count";
		private static final String DEBIT_INVEST_LAND_FMT = "debit.invest.%s";
		private static final String DEBIT_RENT_TOTAL = "debit.rent.total";
		private static final String DEBIT_RENT_COUNT = "debit.rent.count";
		private static final String DEBIT_RENT_LAND_FMT = "debit.rent.%s";

		// Credits-related columns
		private static final String CREDIT_TOTAL = "credit.total";
		private static final String CREDIT_COUNT = "credit.count";
		private static final String CREDIT_RENT_TOTAL = "credit.rent.total";
		private static final String CREDIT_RENT_COUNT = "credit.rent.count";
		private static final String CREDIT_RENT_LAND_FMT = "credit.rent.%s";

		// Formats
		private static final Set<String> FORMATS = Set.of(OWNERSHIP_LAND_FMT, DEBIT_INVEST_LAND_FMT,
				DEBIT_RENT_LAND_FMT, CREDIT_RENT_LAND_FMT);
	}

	private class CSVRow {

		private long timestamp;
		private int order;
		private Player player;

		private Map<String, PlayerLand> investmentPerLand;
		private Map<String, List<Rent>> rentsPaidPerLand;
		private Map<String, List<Rent>> rentsReceivedPerLand;

		private double totalOwnership;
		private double totalInvestment;
		private double totalRentPaid;
		private double totalRentReceived;

		CSVRow(long timestamp, int order, Player player) {
			this.timestamp = timestamp;
			this.order = order;
			this.player = player;

			// Preparing statistics per land
			investmentPerLand = player.getPlayerLands().stream()
					.collect(Collectors.toMap(pl -> pl.getLand().getName(), Function.identity()));
			rentsPaidPerLand = player.getPaidRents().stream()
					.collect(Collectors.groupingBy(r -> r.getLand().getName()));
			rentsReceivedPerLand = player.getReceivedRents().stream()
					.collect(Collectors.groupingBy(r -> r.getLand().getName()));

			// Preparing statistics for the player
			totalOwnership = MathUtil.sum(player.getPlayerLands(), PlayerLand::getOwnership);
			totalInvestment = MathUtil.sum(player.getPlayerLands(), PlayerLand::getBuyAmount);
			totalRentPaid = MathUtil.sum(player.getPaidRents(), Rent::getRentAmount);
			totalRentReceived = MathUtil.sum(player.getReceivedRents(), Rent::getRentAmount);
		}

		List<Object> getValues(List<String> headers) {
			return headers.stream().map(this::getValue).toList();
		}

		Object getValue(String header) {
			switch (header) {
			case CSVColumn.GAME_EXPORT_TIMESTAMP:
				return timestamp;
			case CSVColumn.GAME_BANKRUPTCY_ORDER:
				return order;
			case CSVColumn.PLAYER_USERNAME:
				return player.getUsername();
			case CSVColumn.PLAYER_BASE_CASH:
				return player.getBaseCash();
			case CSVColumn.PLAYER_STATE:
				return player.getState();
			case CSVColumn.OWNERSHIP_COUNT, CSVColumn.DEBIT_INVEST_COUNT:
				return investmentPerLand.size();
			case CSVColumn.OWNERSHIP_TOTAL:
				return totalOwnership;
			case CSVColumn.DEBIT_TOTAL:
				return MathUtil.round(totalInvestment + totalRentPaid);
			case CSVColumn.DEBIT_COUNT:
				return investmentPerLand.size() + player.getPaidRents().size();
			case CSVColumn.DEBIT_INVEST_TOTAL:
				return totalInvestment;
			case CSVColumn.DEBIT_RENT_TOTAL:
				return totalRentPaid;
			case CSVColumn.DEBIT_RENT_COUNT:
				return player.getPaidRents().size();
			case CSVColumn.CREDIT_TOTAL, CSVColumn.CREDIT_RENT_TOTAL:
				return totalRentReceived;
			case CSVColumn.CREDIT_COUNT, CSVColumn.CREDIT_RENT_COUNT:
				return player.getReceivedRents().size();
			default:
				val split = header.split("\\.");
				val name = split[split.length - 1];
				if ("ownership".equals(split[0])) {
					return investmentPerLand.containsKey(name) ? investmentPerLand.get(name).getOwnership() : 0d;
				} else if ("credit".equals(split[0])) {
					return MathUtil.sum(rentsReceivedPerLand.get(name), Rent::getRentAmount);
				} else if ("invest".equals(split[1])) {
					return investmentPerLand.containsKey(name) ? investmentPerLand.get(name).getBuyAmount() : 0d;
				} else if ("rent".equals(split[1])) {
					return MathUtil.sum(rentsPaidPerLand.get(name), Rent::getRentAmount);
				} else {
					log.warn("Unknown header: {}", header);
				}
				return null;
			}
		}

	}

	@Override
	public void updateTrends() {
		trendRepository.saveAll(playerService.getActivePlayers().stream().map(Trend::fromPlayer).toList());
		trendRepository.saveAll(landService.getLands().stream().map(Trend::fromLand).toList());
	}
}
