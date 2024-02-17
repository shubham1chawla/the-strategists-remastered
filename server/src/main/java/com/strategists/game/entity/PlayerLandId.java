package com.strategists.game.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class PlayerLandId implements Serializable {

	private static final long serialVersionUID = 2411177966930860531L;

	@ManyToOne
	private Player player;

	@ManyToOne
	private Land land;

	public PlayerLandId(Player player, Land land) {
		Assert.notNull(player, "Player can't be null!");
		Assert.notNull(land, "Land can't be null!");
		Assert.notNull(player.getGame(), "Player must be associated with a game!");
		Assert.notNull(land.getGame(), "Land must be associated with a game!");
		Assert.isTrue(Objects.equals(player.getGameId(), land.getGameId()), "Game IDs must match!");

		this.player = player;
		this.land = land;
	}

}
