package com.strategists.game.response;

import com.strategists.game.entity.Player;

import lombok.Data;
import lombok.val;

@Data
public class EnterGameResponse {

	private String gameCode;
	private long playerId;

	public static EnterGameResponse fromPlayer(Player player) {
		val response = new EnterGameResponse();
		response.setGameCode(player.getGameCode());
		response.setPlayerId(player.getId());
		return response;
	}

}
