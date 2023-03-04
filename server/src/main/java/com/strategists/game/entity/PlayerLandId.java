package com.strategists.game.entity;

import java.io.Serializable;

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

		this.player = player;
		this.land = land;
	}

}
