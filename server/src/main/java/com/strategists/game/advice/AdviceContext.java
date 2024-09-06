package com.strategists.game.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AdviceContext extends ContextBase implements Context {

	private static final long serialVersionUID = 8243870294194758570L;

	@Getter
	@EqualsAndHashCode.Include
	private Game game;

	private List<Player> players;
	private List<Activity> activities;
	private List<Advice> advices;

	@Getter(AccessLevel.PRIVATE)
	private Map<String, Player> playerUsernameMap;

	public AdviceContext(Game game, List<Player> players, List<Activity> activities) {
		this.game = game;
		this.players = players;
		this.activities = activities;
		this.advices = new ArrayList<>();

		this.playerUsernameMap = players.stream().collect(Collectors.toMap(Player::getUsername, Function.identity()));
	}

	public Player getPlayerByUsername(String username) {
		return playerUsernameMap.get(username);
	}

	public void addAdvice(Advice advice) {
		advices.add(advice);
	}

}
