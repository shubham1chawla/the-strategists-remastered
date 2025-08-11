package com.strategists.game.csv.impl;

import com.strategists.game.csv.StrategistsCSV;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
public class AdvicesCSV implements StrategistsCSV {

    @Getter
    @AllArgsConstructor
    public enum Header {
        GAME("game.code", advice -> advice.getGame().getCode()), PLAYER("player.id", Advice::getPlayerId),
        TYPE("advice.type", Advice::getType), STATE("advice.state", Advice::getState),
        PRIORITY("advice.priority", Advice::getPriority),
        VIEWED("advice.viewed", Advice::getViewed),
        VAL1("advice.val1", Advice::getVal1),
        VAL2("advice.val2", Advice::getVal2),
        VAL3("advice.val3", Advice::getVal3),
        NEW_COUNT("advice.newCount", Advice::getNewCount),
        FOLLOWED_COUNT("advice.followedCount", Advice::getFollowedCount);

        private final String header;
        private final Function<Advice, Object> extractor;
    }

    private final String defaultFileName;
    private final List<String> headers;
    private final List<Map<String, Object>> rowMaps;

    public AdvicesCSV(Game game, List<Advice> advices) {
        defaultFileName = String.format("advice-%s-%s.csv", game.getCode(), System.currentTimeMillis());
        headers = Stream.of(Header.values()).map(Header::getHeader).toList();
        rowMaps = new ArrayList<>(advices.size());
        for (var advice : advices) {
            var rowMap = new LinkedHashMap<String, Object>();
            for (var header : Header.values()) {
                rowMap.put(header.getHeader(), header.extractor.apply(advice));
            }
            rowMaps.add(rowMap);
        }
    }

}
