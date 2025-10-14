package com.strategists.game.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.entity.Trend;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;

@Getter
@Builder
public class GameResponse {

    private Game game;

    private List<Player> players;

    private List<Land> lands;

    private List<Activity> activities;

    private List<Trend> trends;

    private List<PlayerPrediction> playerPredictions;

    private List<Advice> advices;

    @JsonIgnore
    public Player getHostPlayer() {
        final var opt = players.stream().filter(Player::isHost).findFirst();
        Assert.isTrue(opt.isPresent(), "Unable to find host player!");
        return opt.get();
    }

}
