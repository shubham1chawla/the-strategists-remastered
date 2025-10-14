package com.strategists.game.service.impl;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;
import com.strategists.game.repository.PlayerRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Log4j2
@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private static final Random RANDOM = new Random();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LandService landService;

    @Autowired
    private TrendRepository trendRepository;

    @Override
    public List<Player> getPlayersByGame(Game game) {
        return playerRepository.findByGame(game);
    }

    @Override
    public List<Player> getActivePlayersByGame(Game game) {
        return playerRepository.findByGameAndState(game, State.ACTIVE);
    }

    @Override
    public List<Player> getPlayersByGameOrderByBankruptcy(Game game) {
        return playerRepository.findByGameOrderByBankruptcyOrder(game);
    }

    @Override
    public Player getPlayerById(long id) {
        final var opt = playerRepository.findById(id);
        Assert.isTrue(opt.isPresent(), "No player found with ID: " + id);
        return opt.get();
    }

    @Override
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    @Override
    public Player getPlayerByEmail(String email) {
        final var opt = playerRepository.findByEmail(email);
        Assert.isTrue(opt.isPresent(), "No player found with requested email!");
        return opt.get();
    }

    @Override
    @UpdateMapping(UpdateType.JOIN)
    public Player addPlayer(Game game, String email, String name) {
        return addPlayer(game, email, name, false);
    }

    @Override
    public Player addPlayer(Game game, String email, String name, boolean host) {
        Assert.state(!existsByEmail(email), email + " already in a game!");
        Assert.state(playerRepository.countByGame(game) < game.getMaxPlayersCount(),
                "Max count reached! Can't add more players to game: " + game.getCode());
        if (host) {
            Assert.state(!playerRepository.existsByGameAndHost(game, host),
                    "Host already assigned to the game: " + game.getCode());
        }

        // Creating player's instance
        final var player = new Player(game, email);
        player.setState(State.ACTIVE);
        player.setHost(host);

        // Generating valid username for the player
        final var split = name.split("\\s+");
        int count = 0;
        player.setUsername(split[0]);
        while (playerRepository.existsByGameAndUsername(game, player.getUsername())) {
            player.setUsername(String.format("%s-%s", split[0], ++count));
        }

        log.info("{} (host? {}) joined game: {}", player.getUsername(), player.isHost(), game.getCode());
        return playerRepository.save(player);
    }

    @Override
    @UpdateMapping(UpdateType.KICK)
    public Player kickPlayer(long playerId) {
        try {
            final var player = getPlayerById(playerId);
            playerRepository.delete(player);
            log.info("Kicked {}", player.getUsername());
            return player;
        } catch (EmptyResultDataAccessException ex) {
            // suppress exception
        }
        return null;
    }

    @Override
    public boolean isTurnAssigned(Game game) {
        return playerRepository.existsByGameAndTurn(game, true);
    }

    @Override
    @UpdateMapping(UpdateType.START)
    public Player assignTurn(Game game) {
        Assert.state(!isTurnAssigned(game), "Turn already assigned!");

        log.info("Randomly assigning turn to a player for game: {}", game.getCode());
        final var players = getPlayersByGame(game);
        players.forEach(player -> player.setBankruptcyOrder(players.size()));
        final var player = players.get(RANDOM.nextInt(players.size()));
        player.setTurn(true);
        playerRepository.saveAll(players);

        log.info("Assigned first turn to {} for game: {}", player.getUsername(), game.getCode());
        return player;
    }

    @Override
    public Player getCurrentPlayer(Game game) {
        final var opt = playerRepository.findByGameAndTurn(game, true);
        Assert.state(opt.isPresent(), "No player has the turn for game: " + game.getCode());

        return opt.get();
    }

    @Override
    @UpdateMapping(UpdateType.MOVE)
    public Land movePlayer(Player player, int move) {
        final var game = player.getGame();
        player.setIndex((player.getIndex() + move) % landService.getCount(game));

        /**
         * Had to use flush because rent method refreshes the source player and revert's
         * player's index back to previous land. This ensures that it doesn't do that.
         */
        playerRepository.saveAndFlush(player);

        final var index = player.getIndex();
        final var land = landService.getLandByIndex(game, index);
        log.info("{} moved to {} ({}) for game: {}", player.getUsername(), land.getName(), index, game.getCode());
        return land;
    }

    @Override
    @UpdateMapping(UpdateType.TURN)
    public Player nextPlayer(Player currentPlayer) {
        Assert.state(currentPlayer.isTurn(), currentPlayer.getUsername() + " doesn't have current turn!");

        final var game = currentPlayer.getGame();
        log.info("Finding next player of {} for game: {}", currentPlayer.getUsername(), game.getCode());

        final var players = getPlayersByGame(game);
        int i = players.indexOf(currentPlayer);

        // Finding suitable player
        do {
            i = (i + 1) % players.size();
            final var player = players.get(i);

            // Checking if next player is not bankrupt and not current player
            if (player.isBankrupt() || Objects.equals(currentPlayer, player)) {
                continue;
            }

            currentPlayer.setTurn(false);
            player.setTurn(true);
            playerRepository.saveAll(List.of(currentPlayer, player));

            log.info("Assigned turn to {} for game: {}", player.getUsername(), game.getCode());
            return player;

        } while (!Objects.equals(currentPlayer, players.get(i)));

        log.warn("No suitable next player found for game: {}", game.getCode());
        return null;
    }

    @Override
    @UpdateMapping(UpdateType.SKIP)
    public void skipPlayer(Player player) {
        player.setRemainingSkipsCount(player.getRemainingSkipsCount() - 1);
        player.setLastSkippedStep(player.getGame().getCurrentStep());
        playerRepository.save(player);

        final var game = player.getGame();
        log.info("{}/{} skips remain for {} in game: {}", player.getRemainingSkipsCount(), game.getAllowedSkipsCount(), player.getUsername(), game.getCode());
    }

    @Override
    @UpdateMapping(UpdateType.INVEST)
    public void invest(Player player, Land land, double ownership) {
        final var buyAmount = land.getMarketValue() * (ownership / 100);
        Assert.isTrue(land.getTotalOwnership() + ownership <= 100, "Can't buy more than 100% of a land!");
        Assert.isTrue(player.getCash() > buyAmount,
                String.format("%s's %s cash < %s buying amount!", player.getUsername(), player.getCash(), buyAmount));

        player.addLand(land, ownership, buyAmount);
        player.setLastInvestStep(player.getGame().getCurrentStep());

        /*
         * Somehow standard "save" method was not inserting the record right away. My
         * guess, since PlayerLand has complex Id situation, JPA is unable to predict
         * that it needs to insert/update the record. This behavior breaks refreshing of
         * land's entity. The "saveAndFlush" method forces JPA to insert/update the
         * record right away.
         */
        playerRepository.saveAndFlush(player);

        // Refreshing land's entity to reflect this investment
        em.refresh(land);

        final var game = player.getGame();
        log.info("{} invested {}% in {} for {} in game: {}", player.getUsername(), ownership, land.getName(), buyAmount, game.getCode());
    }

    @Override
    @UpdateMapping(UpdateType.RENT)
    public void payRent(Rent rent) {
        final var source = rent.getSourcePlayer();
        final var target = rent.getTargetPlayer();
        final var land = rent.getLand();
        final var amount = rent.getRentAmount();

        // Adding rent instance to target player only
        target.addRent(rent);
        playerRepository.save(target);

        /**
         * After marking foreign key columns with @ManyToOne annotation, target player
         * received twice the rent. This was fixed by refreshing the target player's
         * entity too with source player.
         */
        em.refresh(source);
        em.refresh(target);

        final var game = source.getGame();
        log.info("{} paid {} rent to {} for {} in game: {}", source.getUsername(), amount, target.getUsername(), land.getName(), game.getCode());
    }

    @Override
    @UpdateMapping(UpdateType.BANKRUPTCY)
    public void bankruptPlayer(Player player) {
        final var order = playerRepository.countByGameAndState(player.getGame(), State.BANKRUPT) + 1;

        player.setState(State.BANKRUPT);
        player.setBankruptcyOrder((int) order);
        playerRepository.saveAndFlush(player);

        final var game = player.getGame();
        log.info("{} state updated to {} in game: {}", player.getUsername(), player.getState(), game.getCode());
    }

    @Override
    public void resetPlayers(Game game) {
        log.info("Resetting players for game: {}", game.getCode());
        final var players = getPlayersByGame(game);
        for (Player player : players) {

            // Removing all the investments
            for (PlayerLand pl : player.getPlayerLands()) {
                pl.getLand().getPlayerLands().clear();
            }
            player.getPlayerLands().clear();

            // Removing all the rents
            player.getReceivedRents().clear();
            player.getPaidRents().clear();

            // Resetting other information
            player.setIndex(0);
            player.setTurn(false);
            player.setState(State.ACTIVE);
            player.setRemainingSkipsCount(game.getAllowedSkipsCount());
        }

        playerRepository.saveAll(players);
    }

    @Override
    @UpdateMapping(UpdateType.TREND)
    public List<Trend> updatePlayerTrends(Game game) {
        return trendRepository.saveAll(getActivePlayersByGame(game).stream().map(Trend::fromPlayer).toList());
    }

}
