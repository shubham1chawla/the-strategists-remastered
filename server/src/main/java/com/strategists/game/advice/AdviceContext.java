package com.strategists.game.advice;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AdviceContext extends ContextBase implements Context {

    @Serial
    private static final long serialVersionUID = 8243870294194758570L;

    @Getter
    @EqualsAndHashCode.Include
    private final Game game;

    private final List<Player> players;
    private final List<Land> lands;
    private final List<Advice> advices;

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, Player> playerUsernameMap;

    public AdviceContext(Game game, List<Player> players, List<Land> lands) {
        this.game = game;
        this.players = players;
        this.lands = lands;
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
