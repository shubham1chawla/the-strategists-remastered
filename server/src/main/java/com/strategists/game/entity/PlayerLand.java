package com.strategists.game.entity;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "players_lands")
@AssociationOverrides({
		//
		@AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "playerId")),
		//
		@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
		//
})
public class PlayerLand {

	@EmbeddedId
	private PlayerLandId pk = new PlayerLandId();

	@Column(nullable = false, precision = 2)
	private Double ownership;

	@Column(nullable = false, precision = 2)
	private Double buyAmount;

	@Transient
	public Player getPlayer() {
		return pk.getPlayer();
	}

	@Transient
	public Land getLand() {
		return pk.getLand();
	}

}
