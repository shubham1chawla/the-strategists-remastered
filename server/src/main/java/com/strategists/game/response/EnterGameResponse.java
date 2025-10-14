package com.strategists.game.response;

import com.strategists.game.entity.Player;
import lombok.Data;

@Data
public class EnterGameResponse {

    private String gameCode;
    private long playerId;

    public static EnterGameResponse fromPlayer(Player player) {
        final var response = new EnterGameResponse();
        response.setGameCode(player.getGame().getCode());
        response.setPlayerId(player.getId());
        return response;
    }

}
