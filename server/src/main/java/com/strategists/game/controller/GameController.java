package com.strategists.game.controller;

import com.strategists.game.request.GoogleOAuthCredential;
import com.strategists.game.response.EnterGameResponse;
import com.strategists.game.response.GameResponse;
import com.strategists.game.response.PermissionGroupResponse;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PermissionsService;
import com.strategists.game.service.PlayerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private PermissionsService permissionsService;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @GetMapping("/{code}")
    public ResponseEntity<GameResponse> getGameResponse(@PathVariable String code) {
        try {
            // Finding requested game
            final var game = gameService.getGameByCode(code);

            // Preparing game response
            final var gameResponse = gameService.getGameResponseByGame(game);

            // Responding with 200
            return ResponseEntity.ok(gameResponse);

        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<EnterGameResponse> findGame(@RequestParam(name = "credential") String jwt) {
        try {

            // Converting JWT string to credential instance
            final var credential = GoogleOAuthCredential.fromJWT(jwt);

            // Finding player and associated game information
            final var player = playerService.getPlayerByEmail(credential.getEmail());

            return ResponseEntity.ok(EnterGameResponse.fromPlayer(player));

        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EnterGameResponse> createGame(@RequestBody GoogleOAuthCredential credential) {

        // Checking if requesting user can create the game
        final var opt = permissionsService.getPermissionGroupByEmail(credential.getEmail());
        final var status = opt.isPresent() ? opt.get().getGameCreation() : PermissionGroupResponse.PermissionStatus.DISABLED;
        if (PermissionGroupResponse.PermissionStatus.DISABLED.equals(status)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Creating game for the requesting player
        final var gameResponse = gameService.createGame(credential);
        final var hostPlayer = gameResponse.getHostPlayer();
        return ResponseEntity.ok(EnterGameResponse.fromPlayer(hostPlayer));
    }

    @PutMapping("/{code}/start")
    public void startGame(@PathVariable String code) {
        final var game = gameService.getGameByCode(code);
        Assert.state(game.isLobby(), "Game already started!");
        gameService.startGame(game);
    }

    @PutMapping("/{code}/turn")
    public void playTurn(@PathVariable String code) {
        final var game = gameService.getGameByCode(code);
        Assert.state(game.isActive(), "Game not started yet!");
        gameService.playTurn(game);
    }

    @DeleteMapping("/{code}")
    public void resetGame(@PathVariable String code) {
        final var game = gameService.getGameByCode(code);
        gameService.resetGame(game);
    }

}
