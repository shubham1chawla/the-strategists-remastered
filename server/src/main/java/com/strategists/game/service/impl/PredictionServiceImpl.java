package com.strategists.game.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Prediction;
import com.strategists.game.entity.Prediction.Type;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.PredictionRepository;
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

	@Value("${strategists.prediction.data-directory}")
	private File dataDirectory;

	@Value("${strategists.prediction.metadata-directory}")
	private File metadataDirectory;

	@Value("${strategists.prediction.classifier-directory}")
	private File classifierDirectory;

	@Value("${strategists.prediction.classifier-pickle-file-name}")
	private String classifierPickleFileName;

	@Value("${strategists.prediction.test-directory}")
	private File testDirectory;

	@Value("${strategists.prediction.train-subcommand}")
	private String trainSubcommand;

	@Value("${strategists.prediction.predict-subcommand}")
	private String predictSubcommand;

	@Value("${strategists.prediction.python-executable}")
	private String pythonExecutable;

	@Value("${strategists.prediction.python-script}")
	private String pythonScript;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PredictionRepository predictionRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	private ObjectMapper predictionObjectMapper;

	@PostConstruct
	public void setup() {
		validateDirectories();

		// Setting up prediction object mapper
		val module = new SimpleModule();
		module.addDeserializer(Prediction.class, new PredictionDeserializer());

		predictionObjectMapper = new ObjectMapper();
		predictionObjectMapper.registerModule(module);

		// Training the model if classifier is not present
		val classifierPickleFile = getClassifierPickleFile();
		if (!classifierPickleFile.exists()) {
			trainPredictionModel();
		} else {
			log.info("Found classifier: {}", classifierPickleFile.getAbsolutePath());
		}
	}

	@Override
	public void trainPredictionModel() {
		log.info("Training prediction model...");

		// Training the prediction model
		val output = executePredictionScript(new String[] {

				// Script execution command
				pythonExecutable, pythonScript, trainSubcommand,

				// Game data export directory
				"-D", dataDirectory.getAbsolutePath(),

				// Metadata export directory
				"-M", metadataDirectory.getAbsolutePath(),

				// Classifier pickle file path
				"-P", getClassifierPickleFile().getAbsolutePath()

		});
		log.info("Prediction Model's training output:\n{}", String.join("\n", output));
	}

	@Override
	@Transactional
	public void trainPredictionModel(Game game) {

		// Ensuring that inconsistency is not observed while generating the CSV file
		em.flush();
		em.getEntityManagerFactory().getCache().evictAll();

		// Exporting game data if requested
		val orderedPlayers = playerService.getPlayersByGameOrderByBankruptcy(game);
		orderedPlayers.forEach(em::refresh);

		// Checking if game data CSV should be exported
		try {
			validateGameIntegrity(game, orderedPlayers);
		} catch (Exception ex) {
			log.warn("Skipped CSV export for game: {} | Reason: {}", game.getCode(), ex.getMessage());
			return;
		}

		val filename = String.format("%s-%s", game.getCode(), System.currentTimeMillis());
		val csv = exportCSVFile(game, orderedPlayers, dataDirectory, filename);
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
	public List<Prediction> executePredictionModel(Game game) {

		// Fetching new reference
		val players = playerService.getActivePlayersByGame(game);
		log.info("Testing prediction model for game: {}", game.getCode());

		// Exporting player data
		val csv = exportCSVFile(game, players, testDirectory, game.getCode());
		if (Objects.isNull(csv)) {
			log.error("No CSV exported! Skipping executing the model!");
			return List.of();
		}

		// Executing prediction script
		val output = executePredictionScript(new String[] {

				// Script execution command
				pythonExecutable, pythonScript, predictSubcommand,

				// Prediction file
				"-P", getClassifierPickleFile().getAbsolutePath(),

				// Model out directory
				"-T", csv.getAbsolutePath()

		});

		// Deleting prediction file
		csv.delete();

		// Loading predictions
		return predictionRepository.saveAll(loadPredictions(game, output));
	}

	@Override
	public List<Prediction> getPredictionsByGame(Game game) {
		return predictionRepository.findByGameOrderById(game);
	}

	@Override
	public void clearPredictions(Game game) {
		predictionRepository.deleteByGame(game);
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

	private void validateDirectories() {
		for (File directory : List.of(dataDirectory, metadataDirectory, classifierDirectory, testDirectory)) {
			if (!directory.exists()) {
				Assert.state(directory.mkdirs(), "Unable to create directory: " + directory);
			}
		}
		log.info("Data Directory: {}", dataDirectory.getAbsolutePath());
		log.info("Metadata Directory: {}", metadataDirectory.getAbsolutePath());
		log.info("Classifier Directory: {}", classifierDirectory.getAbsolutePath());
		log.info("Test Directory: {}", testDirectory.getAbsolutePath());
	}

	private File getClassifierPickleFile() {
		return Paths.get(classifierDirectory.getAbsolutePath(), classifierPickleFileName).toFile();
	}

	private List<Prediction> loadPredictions(Game game, List<String> output) {

		// Getting prediction file's reference
		val predictionFileName = String.format("%s.json", game.getCode());
		val predictionFile = Paths.get(testDirectory.getAbsolutePath(), predictionFileName).toFile();

		try {

			// Checking output and ensuring prediction file exists
			Assert.notEmpty(output, "No output from prediction script!");
			Assert.isTrue(output.stream().anyMatch(line -> line.contains(predictionFileName)),
					"Prediction file name not in output!");
			Assert.isTrue(predictionFile.exists(), "Prediction file doesn't exist!");

			// Returning predictions
			val predictions = predictionObjectMapper.readValue(predictionFile, Prediction[].class);
			return Arrays.asList(predictions);

		} catch (Exception ex) {

			log.error("Unable to load predictions! Message: {}", ex.getMessage());
			log.debug(ex);
			return List.of();

		} finally {

			// Deleting the prediction file
			if (predictionFile.exists()) {
				predictionFile.delete();
			}

		}
	}

	private void validateGameIntegrity(Game game, List<Player> players) {

		val case1 = players.size() > 1;
		Assert.isTrue(case1, "More than 1 player required!");

		val case2 = Objects.isNull(game.getAllowedSkipsCount());
		if (!case2) {
			val case3 = players.stream().allMatch(player -> player.getRemainingSkipsCount() > 0);
			Assert.isTrue(case3, "All players must have more than 0 remaining skips!");
		}

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

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private class CSVColumn {
		// Game-related columns
		private static final String GAME_EXPORT_TIMESTAMP = "game.export.timestamp";
		private static final String GAME_CODE = "game.code";
		private static final String GAME_BANKRUPTCY_ORDER = "game.bankruptcy-order";

		// Player-related columns
		private static final String PLAYER_ID = "player.id";
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
			case CSVColumn.GAME_CODE:
				return player.getGameCode();
			case CSVColumn.GAME_BANKRUPTCY_ORDER:
				return order;
			case CSVColumn.PLAYER_ID:
				return player.getId();
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

	private class PredictionDeserializer extends StdDeserializer<Prediction> {

		private static final long serialVersionUID = -8567774160048687462L;

		private static final String PLAYER_ID_FIELD = "player_id";
		private static final String PROBA_FIELD = "proba";
		private static final String PREDICT_FIELD = "predict";

		private PredictionDeserializer() {
			super(Prediction.class);
		}

		@Override
		public Prediction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			val node = p.getCodec().readTree(p);
			Assert.isTrue(node.isObject(), "JSON request is not object!");

			// Validating JSON structure
			Assert.notNull(node.get(PLAYER_ID_FIELD), "Player ID field not present in JSON!");
			Assert.notNull(node.get(PROBA_FIELD), "Proba field not present in JSON!");
			Assert.notNull(node.get(PREDICT_FIELD), "Predict field not present in JSON!");

			// Extracting values from JSON
			val playerId = ((IntNode) node.get(PLAYER_ID_FIELD)).asLong();
			val proba = new ArrayList<Double>(2);
			for (JsonNode probaValue : ((ArrayNode) node.get(PROBA_FIELD))) {
				proba.add(((DoubleNode) probaValue).asDouble());
			}
			val predictionValue = ((IntNode) node.get(PREDICT_FIELD)).asInt();

			// Creating Prediction instance
			val player = playerService.getPlayerById(playerId);
			val type = predictionValue == 1 ? Type.WINNER : Type.BANKRUPT;
			return new Prediction(player, proba.get(0), proba.get(1), type);
		}

	}

}
