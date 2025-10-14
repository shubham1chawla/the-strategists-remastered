package com.strategists.game.service.impl;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;
import com.strategists.game.repository.LandRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.EventService;
import com.strategists.game.service.LandService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class LandServiceImpl implements LandService {

    @Autowired
    private LandRepository landRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private TrendRepository trendRepository;

    @Override
    public void updateLands(Game game, GameMap gameMap) {
        gameMap.getLands().forEach(land -> land.setGame(game));
        landRepository.saveAll(gameMap.getLands());
    }

    @Override
    public List<Land> getLandsByGame(Game game) {
        return landRepository.findByGameOrderById(game);
    }

    @Override
    public int getCount(Game game) {
        return (int) landRepository.countByGame(game);
    }

    @Override
    public Land getLandByIndex(Game game, int index) {
        return getLandsByGame(game).get(index);
    }

    @Override
    public List<Rent> getPlayerRentsByLand(Player sourcePlayer, Land land) {
        Assert.isTrue(Objects.equals(sourcePlayer.getGame(), land.getGame()), "Player's and Land's game must match!");
        final var game = sourcePlayer.getGame();

        final var rents = new ArrayList<Rent>();
        for (PlayerLand pl : land.getPlayerLands()) {
            final var targetPlayer = pl.getPlayer();

            // Avoiding self rent payment or bankrupt players
            if (Objects.equals(targetPlayer, sourcePlayer) || targetPlayer.isBankrupt()) {
                continue;
            }

            // Calculating rent for the target player
            final var rentAmount = game.getRentFactor() * (pl.getOwnership() / 100) * land.getMarketValue();
            rents.add(new Rent(sourcePlayer, targetPlayer, land, rentAmount));
        }

        return rents;
    }

    @Override
    public void hostEvent(long landId, long eventId, int life, int level) {
        final var opt = landRepository.findById(landId);
        Assert.isTrue(opt.isPresent(), "No land associated with ID: " + landId);

        final var land = opt.get();
        final var event = eventService.getEventById(eventId);

        land.addEvent(event, life, level);
        landRepository.save(land);

        log.info("Event {} hosted on {}.", event.getName(), land.getName());
    }

    @Override
    public void resetLands(Game game) {
        log.info("Resetting lands for game: {}", game.getCode());
        final var lands = getLandsByGame(game);
        for (Land land : lands) {
            land.getLandEvents().clear();
        }
        landRepository.saveAll(lands);
    }

    @Override
    @UpdateMapping(UpdateType.TREND)
    public List<Trend> updateLandTrends(Game game) {
        return trendRepository.saveAll(getLandsByGame(game).stream().map(Trend::fromLand).toList());
    }

}
