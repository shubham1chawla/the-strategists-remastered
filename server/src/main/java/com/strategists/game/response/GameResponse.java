package com.strategists.game.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Prediction;
import com.strategists.game.entity.Trend;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GameResponse {

    @JsonUnwrapped
    private Game game;

    private List<Player> players;

    private List<Land> lands;

    private List<Activity> activities;

    private List<Trend> trends;

    private List<Prediction> predictions;

    private List<Advice> advices;

}
