package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
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
@AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "playerId"))
@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
public class PlayerLand implements Serializable {

	private static final long serialVersionUID = -7469278040684677456L;

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

	@Transient
	public Long getPlayerId() {
		return getPlayer().getId();
	}

	@Transient
	public Long getLandId() {
		return getLand().getId();
	}

}