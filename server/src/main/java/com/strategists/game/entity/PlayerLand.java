package com.strategists.game.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.Assert;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players_lands")
@AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "playerId"))
@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
public class PlayerLand implements Serializable {

	private static final long serialVersionUID = -7469278040684677456L;

	private static final int PRECISION = 2;

	@EmbeddedId
	private PlayerLandId pk = new PlayerLandId();

	@Column(nullable = false, precision = PRECISION)
	private Double ownership;

	@Column(nullable = false, precision = PRECISION)
	private Double buyAmount;

	public PlayerLand(Player player, Land land, double ownership, double buyAmount) {
		this.pk = new PlayerLandId(player, land);

		Assert.isTrue(ownership > 0 && ownership <= 100, "Ownership should be between 0 and 100!");
		Assert.isTrue(buyAmount > 0, "Buy amount can't be negative!");

		this.ownership = BigDecimal.valueOf(ownership).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
		this.buyAmount = BigDecimal.valueOf(buyAmount).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
	}

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
