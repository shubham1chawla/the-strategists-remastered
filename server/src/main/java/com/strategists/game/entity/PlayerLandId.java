package com.strategists.game.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
@NoArgsConstructor
public class PlayerLandId implements Serializable {

    @Serial
    private static final long serialVersionUID = 2411177966930860531L;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Land land;

    public PlayerLandId(Player player, Land land) {
        Assert.notNull(player, "Player can't be null!");
        Assert.notNull(land, "Land can't be null!");
        Assert.notNull(player.getGame(), "Player must be associated with a game!");
        Assert.notNull(land.getGame(), "Land must be associated with a game!");
        Assert.isTrue(Objects.equals(player.getGame(), land.getGame()), "Games must match!");

        this.player = player;
        this.land = land;
    }

}
