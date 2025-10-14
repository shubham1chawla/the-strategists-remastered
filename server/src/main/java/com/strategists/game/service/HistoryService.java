package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.update.payload.UpdatePayload;

import java.util.List;
import java.util.Map;

public interface HistoryService {

    List<Map<String, Object>> getHistory(Game game);

    void appendUpdatePayload(Game game, UpdatePayload<?> payload);

    void exportHistory(Game game);

    void resetHistory(Game game);

}
