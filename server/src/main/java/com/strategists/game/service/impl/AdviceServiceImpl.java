package com.strategists.game.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.handler.AbstractAdviceHandler;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.service.AdviceService;
import com.strategists.game.service.DataSyncService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@ConditionalOnProperty(name = "strategists.advice.enabled", havingValue = "true")
public class AdviceServiceImpl implements AdviceService {

	@Value("${strategists.advice.export.directory}")
	private File exportDirectory;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private DataSyncService dataSyncService;

	@Autowired
	private AdviceRepository adviceRepository;

	@Autowired
	private List<AbstractAdviceHandler> handlers;

	@PostConstruct
	public void setup() {
		// Validating export directory
		if (!exportDirectory.exists()) {
			Assert.state(exportDirectory.mkdir(), "Unable to create directory: " + exportDirectory);
			log.info("Created directory: {}", exportDirectory);
		}
		log.info("Checked advice-related directories!");
	}

	@Override
	@UpdateMapping(UpdateType.ADVICE)
	public List<Advice> generateAdvices(Game game) {
		log.info("Generating advices for game: {}", game.getCode());

		// Checking if any handler is available
		if (CollectionUtils.isEmpty(handlers)) {
			log.warn("No advice handlers enabled. Skipping generating advices...");
			return List.of();
		}

		// Creating the chain from available handlers
		val chain = new ChainBase();
		for (AbstractAdviceHandler handler : handlers) {
			chain.addCommand(handler);
		}

		// Adding information to advice context
		val players = playerService.getPlayersByGame(game);
		val lands = landService.getLandsByGame(game);
		val activities = activityRepository.findByGameOrderByIdDesc(game);
		val context = new AdviceContext(game, players, lands, activities);

		// Executing advice chain
		try {
			chain.execute(context);
		} catch (Exception ex) {
			log.error("Unable to complete advice chain! Message: " + ex.getMessage(), ex);
			return List.of();
		}

		// Saving new and updated records
		return adviceRepository.saveAll(context.getAdvices());
	}

	@Override
	public List<Advice> getAdvicesByGame(Game game) {
		return adviceRepository.findByGameOrderByPriority(game);
	}

	@Override
	@UpdateMapping(UpdateType.ADVICE)
	public List<Advice> markPlayerAdvicesViewed(Player player) {
		log.info("Marking {}'s advices as viewed for game: {}", player.getUsername(), player.getGameCode());
		val advices = adviceRepository.findByPlayerAndViewed(player, false);
		if (CollectionUtils.isEmpty(advices)) {
			return List.of();
		}
		return adviceRepository.saveAll(advices.stream().map(advice -> {
			advice.setViewed(true);
			return advice;
		}).toList());
	}

	@Override
	public void clearAdvices(Game game) {
		adviceRepository.deleteByGame(game);
	}

	@Override
	public void exportAdvices(Game game) {
		val filename = String.format("advice-%s-%s.csv", game.getCode(), System.currentTimeMillis());
		val csv = new File(exportDirectory, filename);
		val headers = Stream.of(CSVColumn.values()).map(CSVColumn::getHeader).toArray(String[]::new);
		val format = CSVFormat.DEFAULT.builder().setHeader(headers).build();

		try (final CSVPrinter printer = new CSVPrinter(new FileWriter(csv), format)) {

			// Adding rows to the CSV file
			for (Advice advice : getAdvicesByGame(game)) {
				val values = Stream.of(CSVColumn.values()).map(column -> column.getExtractor().apply(advice)).toArray();
				printer.printRecord(values);
			}

			log.debug("Exported: {}", csv.getAbsolutePath());
		} catch (IOException ex) {
			log.warn("Unable to export Advice's CSV file! Message: {}", ex.getMessage());
			return;
		}

		// Uploading Advice CSV file
		dataSyncService.uploadAdviceCSVFiles(exportDirectory);
	}

	@Getter
	@AllArgsConstructor
	private enum CSVColumn {
		GAME("game.code", advice -> advice.getGame().getCode()), PLAYER("player.id", Advice::getPlayerId),
		TYPE("advice.type", Advice::getType), STATE("advice.state", Advice::getState),
		PRIORITY("advice.priority", Advice::getPriority), VIEWED("advice.viewed", Advice::getViewed),
		VAL1("advice.val1", Advice::getVal1), VAL2("advice.val2", Advice::getVal2),
		VAL3("advice.val3", Advice::getVal3), NEW_COUNT("advice.newCount", Advice::getNewCount),
		FOLLOWED_COUNT("advice.followedCount", Advice::getFollowedCount);

		private String header;
		private Function<Advice, Object> extractor;
	}

}
