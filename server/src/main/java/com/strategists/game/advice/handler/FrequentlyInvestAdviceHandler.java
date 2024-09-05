package com.strategists.game.advice.handler;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.service.PlayerService;
import com.strategists.game.update.UpdateType;

import lombok.val;

@Component
@ConditionalOnExpression("${strategists.advice.enabled} && ${strategists.advice.frequently-invest.enabled}")
public class FrequentlyInvestAdviceHandler extends AbstractAdviceHandler {

	@Value("${strategists.advice.frequently-invest.turn-look-back}")
	private int turnLookBack = 3;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private AdviceRepository adviceRepository;

	@Override
	protected void generate(AdviceContext context) {
		val game = context.getGame();

		// Fetching information from database
		val activities = activityRepository.findByGameOrderByIdDesc(game);
		val players = playerService.getPlayersByGame(game);

		// Creating utility functions for lookup
		val getPlayerByUsername = getPlayerByUsernameFunction(players);
		val getLastInvestTurnByPlayer = getLastInvestTurnByPlayerFunction(activities, getPlayerByUsername);
		val getTurnOffsetByPlayer = getTurnOffsetByPlayerFunction(game, activities, players, getPlayerByUsername);

		// Generating or updating advice
		for (Player player : players) {

			// Ignoring bankrupt players
			if (player.isBankrupt()) {
				continue;
			}

			val lastInvestTurn = getLastInvestTurnByPlayer.apply(player);
			val turnOffset = getTurnOffsetByPlayer.apply(player);

			/**
			 * Since the turns are cycling between players, this calculation ensures we are
			 * adjusting the game's turn with respect to player's turns.
			 */
			val adjGameTurn = Math.max(0, game.getTurn() - (turnOffset + 1));
			val adjLastInvestTurn = Math.max(0, lastInvestTurn - (turnOffset + 1));

			/**
			 * Indicates whether advice is needed based on last invest turn.
			 * 
			 * Issue - for the first turn, this boolean will be "true" after turnLookBack+1
			 * turns later.
			 */
			val isAdviceNeeded = (adjGameTurn - adjLastInvestTurn) > (players.size() * turnLookBack);

			// Checking if we have already generated advice for this player
			val opt = adviceRepository.findByPlayerAndType(player, AdviceType.FREQUENTLY_INVEST);

			if (opt.isEmpty() && isAdviceNeeded) {
				/**
				 * This condition ensures we create a new advice for the player.
				 */
				context.addAdvice(Advice.ofFrequentlyInvest(player, turnLookBack));
			} else if (opt.isPresent()) {
				/**
				 * This condition ensures we update the old advice and mark it as "new" or
				 * "followed".
				 */
				val advice = opt.get();
				advice.setState(isAdviceNeeded ? Advice.State.NEW : Advice.State.FOLLOWED);
				context.addAdvice(advice);
			}
		}
	}

	private Function<String, Player> getPlayerByUsernameFunction(List<Player> players) {
		val usernames = players.stream().collect(Collectors.toMap(Player::getUsername, Function.identity()));
		return username -> usernames.get(username);
	}

	private Function<Player, Integer> getLastInvestTurnByPlayerFunction(List<Activity> activities,
			Function<String, Player> getPlayerByUsername) {
		val playerInvestTurnMap = new HashMap<Player, Integer>();
		for (Activity activity : activities) {
			if (!UpdateType.INVEST.equals(activity.getType())) {
				continue;
			}
			val player = getPlayerByUsername.apply(activity.getVal1());
			playerInvestTurnMap.computeIfAbsent(player, key -> activity.getTurn());
		}
		return p -> playerInvestTurnMap.getOrDefault(p, 0);
	}

	private Function<Player, Integer> getTurnOffsetByPlayerFunction(Game game, List<Activity> activities,
			List<Player> players, Function<String, Player> getPlayerByUsername) {
		Player firstPlayer = null;
		for (int i = activities.size() - 1; i > -1; i--) {
			val activity = activities.get(i);
			if (UpdateType.START.equals(activity.getType())) {
				firstPlayer = getPlayerByUsername.apply(activity.getVal1());
				break;
			}
		}

		// This assertion ensures this method is not called before game starts
		Assert.notNull(firstPlayer, "Couldn't find first turned player for game: " + game.getCode());

		// Figuring first player's index
		int firstPlayerIndex = 0;
		while (firstPlayer != players.get(firstPlayerIndex)) {
			firstPlayerIndex++;
		}

		// Preparing a map of players' turn offset from first player
		val playerTurnOffsetMap = new HashMap<Player, Integer>();
		int i = firstPlayerIndex;
		int offset = 0;
		while (playerTurnOffsetMap.size() != players.size()) {
			val player = players.get(i);
			playerTurnOffsetMap.put(player, offset++);
			i = (i + 1) % players.size();
		}
		return p -> playerTurnOffsetMap.get(p);
	}

}
