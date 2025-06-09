package com.strategists.game.controller;

import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.UpdateService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

@RestController
@RequestMapping("/api/games/{code}/sse")
public class UpdateController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UpdateService updateService;

    @GetMapping
    public SseEmitter getSseEmitter(@PathVariable String code, @RequestParam(required = true) long playerId) {
        val game = gameService.getGameByCode(code);
        val player = playerService.getPlayerById(playerId);
        Assert.isTrue(Objects.equals(game, player.getGame()), "Player doesn't belong in requested game!");

        return updateService.registerEmitter(player);
    }

}
