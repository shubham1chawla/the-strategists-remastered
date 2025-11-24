package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.CleanUpConfigurationProperties;
import com.strategists.game.configuration.properties.GameConfigurationProperties;
import com.strategists.game.configuration.properties.SkipPlayerConfigurationProperties;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Game.State;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.GameRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.request.GoogleOAuthCredential;
import com.strategists.game.response.GameResponse;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.PredictionsService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Log4j2
@Service
@Transactional
public class GameServiceImpl implements GameService {

    private static final Random RANDOM = new Random();

    @Autowired
    private GameConfigurationProperties gameConfigurationProperties;

    @Autowired
    private SkipPlayerConfigurationProperties skipPlayerConfigurationProperties;

    @Autowired
    private CleanUpConfigurationProperties cleanUpConfigurationProperties;

    @Autowired
    private Map<String, File> gameMapFiles;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LandService landService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TrendRepository trendRepository;

    @Autowired(required = false)
    private PredictionsService predictionsService;

    @Autowired(required = false)
    private AdvicesService advicesService;

    @PostConstruct
    public void setup() {
        log.info(gameConfigurationProperties);
        log.info(skipPlayerConfigurationProperties);
        log.info(cleanUpConfigurationProperties);
    }

    @Override
    @UpdateMapping(UpdateType.CREATE)
    public GameResponse createGame(GoogleOAuthCredential credential) {
        final var email = credential.getEmail();

        // Checking if requesting user is not part of any other game
        Assert.hasText(email, "No email found in the request!");
        Assert.state(!playerService.existsByEmail(email), email + " already part of a game!");

        // Preparing game map's instance
        final var gameMap = GameMap.from(gameMapFiles.get(gameConfigurationProperties.defaultMap()));
        Assert.notNull(gameMap, gameConfigurationProperties.defaultMap() + " game map doesn't exist!");

        // Creating game instance
        var game = new Game();
        game.setCreatedAt(System.currentTimeMillis());
        game.setCurrentStep(0);
        game.setState(State.LOBBY);
        game.setGameMapId(gameMap.getId());

        // Setting up game's instance with required configurations
        game.setPlayerBaseCash(gameMap.getPlayerBaseCash());
        game.setMinPlayersCount(gameConfigurationProperties.minPlayersCount());
        game.setMaxPlayersCount(gameConfigurationProperties.maxPlayersCount());
        game.setDiceSize(gameConfigurationProperties.diceSize());
        game.setRentFactor(gameConfigurationProperties.rentFactor());

        // Setting skip-player optional configurations
        if (skipPlayerConfigurationProperties.enabled()) {
            log.info("Skip-player for game enabled!");
            game.setAllowedSkipsCount(skipPlayerConfigurationProperties.allowedCount());
            game.setSkipPlayerTimeout(skipPlayerConfigurationProperties.timeout());
        }

        // Setting clean-up optional configurations
        if (cleanUpConfigurationProperties.enabled()) {
            log.info("Clean-up for game enabled!");
            game.setCleanUpDelay(cleanUpConfigurationProperties.delay());
        }

        // Setting up share-able code for game
        do {
            game.setCode(generateCode());
        } while (gameRepository.existsById(game.getCode()));
        game = gameRepository.save(game);
        log.info("Created game: {}", game);

        // Saving lands associated with the game
        landService.updateLands(game, gameMap);

        // Creating player for requesting user
        playerService.addPlayer(game, email, credential.getName(), true);

        // Returning GameResponse for update
        return getGameResponseByGame(game);
    }

    @Override
    public Game getGameByCode(String code) {
        final var opt = gameRepository.findById(code);
        Assert.isTrue(opt.isPresent(), "No game found for code: " + code);
        return opt.get();
    }

    @Override
    public GameResponse getGameResponseByGame(Game game) {
        // Creating response for the game
        final var builder = GameResponse.builder()
                .game(game)
                .players(playerService.getPlayersByGame(game))
                .lands(landService.getLandsByGame(game))
                .activities(activityRepository.findByGameOrderByIdDesc(game))
                .trends(trendRepository.findByGameOrderByIdAsc(game));

        // Adding predictions, if enabled
        if (Objects.nonNull(predictionsService)) {
            builder.playerPredictions(predictionsService.getPlayerPredictionsByGame(game));
        }

        // Adding advice, if enabled
        if (Objects.nonNull(advicesService)) {
            builder.advices(advicesService.getAdvicesByGame(game));
        }

        // Building final GameResponse
        return builder.build();
    }

    @Override
    public void startGame(Game game) {

        // Changing game's state and other information
        game.setCurrentStep(1);
        game.setState(State.ACTIVE);
        game = gameRepository.save(game);

        // Assigning turn
        playerService.assignTurn(game);

        // Updating initial trends
        updateTrends(game);
    }

    @Override
    @UpdateMapping(UpdateType.WIN)
    public Player playTurn(Game game) {

        // Updating game's current step
        game.setCurrentStep(game.getCurrentStep() + 1);
        game = gameRepository.save(game);

        // Checking if game has ended
        final var winner = getWinnerPlayer(game);
        if (winner.isPresent()) {
            return winner.get();
        }

        // Assigning turn to next player
        final var player = playerService.nextPlayer(playerService.getCurrentPlayer(game));

        // Moving the current player to a new position
        final var land = playerService.movePlayer(player, RANDOM.nextInt(game.getDiceSize()) + 1);

        // Calculating rents on the moved land
        final var rents = landService.getPlayerRentsByLand(player, land);

        // Paying rent to players on current land
        for (Rent rent : rents) {
            playerService.payRent(rent);
        }

        // Updating trends
        updateTrends(game);

        // Checking if player is bankrupt
        if (player.getCash() <= 0) {
            playerService.bankruptPlayer(player);
            return playTurn(game);
        }

        // No winner declared
        return null;
    }

    @Override
    @UpdateMapping(UpdateType.RESET)
    public GameResponse resetGame(Game game) {
        log.info("Resetting game: {}", game.getCode());

        // Changing game's state and other information
        game.setCurrentStep(0);
        game.setState(State.LOBBY);
        game.setCreatedAt(System.currentTimeMillis());
        game.setEndedAt(null);
        game = gameRepository.save(game);

        // Resetting players
        playerService.resetPlayers(game);

        // Resetting lands
        landService.resetLands(game);

        // Resetting activities
        activityRepository.deleteByGame(game);

        // Resetting trends
        trendRepository.deleteByGame(game);

        // Resetting predictions, if enabled
        if (Objects.nonNull(predictionsService)) {
            predictionsService.clearPlayerPredictions(game);
        }

        // Resetting advices, if enabled
        if (Objects.nonNull(advicesService)) {
            advicesService.clearAdvices(game);
        }

        // Returning GameResponse for update
        return getGameResponseByGame(game);
    }

    @Override
    @UpdateMapping(UpdateType.CLEAN_UP)
    public void deleteGame(Game game) {
        log.info("Cleaning up data for game: {}", game.getCode());

        // Deleting game record to cascade it to players and lands
        try {
            gameRepository.delete(game);
        } catch (EmptyResultDataAccessException ex) {
            // suppress exception
        }
    }

    private Optional<Player> getWinnerPlayer(Game game) {
        final var activePlayers = playerService.getActivePlayersByGame(game);
        if (activePlayers.size() > 1) {
            return Optional.empty();
        }

        // Setting end at time for the game if not set before
        if (Objects.isNull(game.getEndedAt())) {
            game.setEndedAt(System.currentTimeMillis());
            game = gameRepository.save(game);
        }

        final var winner = activePlayers.getFirst();
        log.info("Found winner {} for game: {}", winner.getUsername(), game.getCode());
        return Optional.of(winner);
    }

    private void updateTrends(Game game) {
        playerService.updatePlayerTrends(game);
        landService.updateLandTrends(game);
    }

    private String generateCode() {
        final var builder = new StringBuilder();
        for (int i = 0; i < gameConfigurationProperties.codeLength(); i++) {
            final var c = (char) ('A' + RANDOM.nextInt(26));
            builder.append(c);
        }
        return builder.toString();
    }

}
