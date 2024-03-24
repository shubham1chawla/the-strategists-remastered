package com.strategists.game.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.PredictionService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import com.strategists.game.util.MathUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.prediction.enabled", havingValue = "true")
public class PredictionServiceImpl implements PredictionService {

	@Value("${strategists.prediction.export-data-directory}")
	private File exportDataDirectory;

	@Value("${strategists.prediction.model-out-directory}")
	private File modelOutDirectory;

	@Value("${strategists.prediction.predict-file-directory}")
	private File predictFileDirectory;

	@Value("${strategists.prediction.python-executable}")
	private String pythonExecutable;

	@Value("${strategists.prediction.python-script}")
	private String pythonScript;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@PostConstruct
	public void setup() {
		validateDirectories();
		trainPredictionModel();
	}

	@Override
	public void trainPredictionModel() {
		log.info("Training prediction model...");

		// Training the prediction model
		val output = executePredictionScript(new String[] {

				// Script execution command
				pythonExecutable, pythonScript, "train",

				// Prediction file
				"-D", exportDataDirectory.getAbsolutePath(),

				// Model out directory
				"-O", modelOutDirectory.getAbsolutePath()

		});
		log.info("Prediction Model's training output:\n{}", String.join("\n", output));
	}

	@Override
	@Transactional
	public void trainPredictionModel(Game game) {

		// Exporting game data if requested
		val orderedPlayers = playerService.getPlayersByGameOrderByBankruptcy(game);

		// Checking if game data CSV should be exported
		try {
			validateGameIntegrity(game, orderedPlayers);
		} catch (Exception ex) {
			log.warn("Skipped CSV export for game: {} | Reason: {}", game.getCode(), ex.getMessage());
			return;
		}

		val csv = exportCSVFile(game, orderedPlayers, exportDataDirectory, "export-" + System.currentTimeMillis());
		if (Objects.isNull(csv)) {
			log.error("No CSV exported! Skipping training the model!");
			return;
		}

		// Training the model
		log.info("Exported game data: {}", csv.getAbsolutePath());
		trainPredictionModel();
	}

	@Override
	@Transactional
	@UpdateMapping(UpdateType.PREDICTION)
	public Prediction executePredictionModel(Player player) {

		// Fetching new reference
		player = playerService.getPlayerById(player.getId());
		log.info("Testing prediction model on {} for game: {}", player.getUsername(), player.getGameCode());

		// Exporting player data
		val csv = exportCSVFile(player.getGame(), List.of(player), predictFileDirectory, player.getGamePlayerKey());
		if (Objects.isNull(csv)) {
			return Prediction.UNKNOWN;
		}

		// Executing prediction script
		val output = executePredictionScript(new String[] {

				// Script execution command
				pythonExecutable, pythonScript, "predict",

				// Prediction file
				"-P", csv.getAbsolutePath(),

				// Model out directory
				"-M", modelOutDirectory.getAbsolutePath()

		});

		// Deleting prediction file
		csv.delete();

		// Extracting prediction results
		if (output == null || output.size() != 3) {
			return Prediction.UNKNOWN;
		}
		val result = Integer.valueOf(output.get(2).split(" ")[1]);
		val prediction = result == 1 ? Prediction.WINNER : Prediction.BANKRUPT;

		log.info("{} predicted to be {} for game: {}", player.getUsername(), prediction, player.getGameCode());
		return result == 1 ? Prediction.WINNER : Prediction.BANKRUPT;
	}

	private File exportCSVFile(Game game, List<Player> players, File directory, String filename) {
		// Creating CSV formatter
		val headers = getCSVHeaders(game);
		val format = CSVFormat.DEFAULT.builder().setHeader(headers.toArray(String[]::new)).build();

		// Preparing CSV file
		val csv = new File(directory, String.format("%s.csv", filename));
		try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csv), format)) {

			// Adding rows to the CSV file
			for (int order = 1; order <= players.size(); order++) {
				val row = new CSVRow(System.currentTimeMillis(), order, players.get(order - 1));
				printer.printRecord(row.getValues(headers));
			}

			log.debug("Exported: {}", csv.getAbsolutePath());
			return csv;
		} catch (IOException ex) {
			log.warn("Unable to export CSV file! Message: {}", ex.getMessage(), ex);
			return null;
		}
	}

	private List<String> executePredictionScript(String[] commands) {
		List<String> output = null;
		val builder = new ProcessBuilder(commands);
		builder.redirectErrorStream(true);
		try {

			val process = builder.start();
			val code = process.waitFor();

			// Checking if process executed successfully
			if (code != 0) {
				log.warn("Prediction script exited with code: {}", code);
				return output;
			}

			// Extracting results from prediction
			val stream = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
			try (val reader = new BufferedReader(stream)) {
				output = reader.lines().toList();
			}

		} catch (Exception ex) {
			log.warn("Unable to execute prediction script! Message: {}", ex.getMessage(), ex);
		}
		return output;
	}

	private List<String> getCSVHeaders(Game game) {
		val headers = new ArrayList<String>();
		val landNames = landService.getLandsByGame(game).stream().map(Land::getName).toList();

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

	private void validateDirectories() {
		for (File directory : List.of(exportDataDirectory, modelOutDirectory, predictFileDirectory)) {
			if (!directory.exists()) {
				Assert.state(directory.mkdirs(), "Unable to create directory: " + directory);
			}
		}
		log.info("Export Data Directory: {}", exportDataDirectory.getAbsolutePath());
		log.info("Model Out Directory: {}", modelOutDirectory.getAbsolutePath());
		log.info("Predict File Directory: {}", predictFileDirectory.getAbsolutePath());
	}

	private void validateGameIntegrity(Game game, List<Player> players) {

		val case1 = players.size() > 1;
		Assert.isTrue(case1, "More than 1 player required!");

		val case2 = Objects.isNull(game.getAllowedSkipsCount());
		val case3 = players.stream().allMatch(player -> player.getRemainingSkipsCount() > 0);
		Assert.isTrue(case2 || case3, "All players must have more than 0 remaining skips!");

		val activePlayers = players.stream().filter(player -> !player.isBankrupt()).toList();
		val bankruptPlayers = players.stream().filter(Player::isBankrupt).toList();

		val case4 = activePlayers.size() == 1;
		val case5 = bankruptPlayers.size() == players.size() - 1;
		val case6 = activePlayers.size() + bankruptPlayers.size() == players.size();
		Assert.isTrue(case4, "Only 1 active player should remain!");
		Assert.isTrue(case5, "Apart from 1 active player, all other players should be bankrupt!");
		Assert.isTrue(case6, "Active & bankrupt players count should add up to total players count!");

		int order = 1;
		for (Player player : players) {
			Assert.isTrue(player.getBankruptcyOrder() == order++, "Inconsistent bankruptcy order!");
		}
	}

}
