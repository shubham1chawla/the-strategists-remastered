package com.strategists.game.service.impl;

import java.util.List;

import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.handler.AbstractAdviceHandler;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.service.AdviceService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.advice.enabled", havingValue = "true")
public class AdviceServiceImpl implements AdviceService {

	@Autowired
	private AdviceRepository adviceRepository;

	@Autowired
	private List<AbstractAdviceHandler> handlers;

	@Override
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

		// Executing advice chain
		val context = new AdviceContext(game);
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
		return adviceRepository.findByGameOrderByIdDesc(game);
	}

	@Override
	public void clearAdvices(Game game) {
		adviceRepository.deleteByGame(game);
	}

}
