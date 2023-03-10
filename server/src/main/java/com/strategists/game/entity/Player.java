package com.strategists.game.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
public class Player implements Serializable {

	private static final long serialVersionUID = -7588097421340659821L;

	public enum State {
		ACTIVE, DEAD, JAIL;
	}

	public Player(String username, double cash) {
		Assert.hasText(username, "Username can't be empty!");
		Assert.isTrue(cash > 0, "Cash can't be negative!");

		this.username = username;
		this.cash = cash;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	/**
	 * Player's cash will be dynamically calculated based on their investments.
	 * Check {@link Player#getCash()} for details.
	 */
	@JsonIgnore
	@Column(nullable = false, precision = 2)
	private Double cash;

	@Column(nullable = true, columnDefinition = "INTEGER DEFAULT 0")
	private Integer index = 0;

	@Column(nullable = true, columnDefinition = "VARCHAR(6) DEFAULT 'ACTIVE'")
	@Enumerated(EnumType.STRING)
	private State state = State.ACTIVE;

	@Column(nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean turn = false;

	@Column(nullable = true, columnDefinition = "INTEGER DEFAULT 0")
	private Integer remainingJailLife = 0;

	@ToString.Exclude
	@JsonProperty("lands")
	@JsonIgnoreProperties({ "pk", "player", "land", "playerId" })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.player", cascade = CascadeType.ALL)
	private List<PlayerLand> playerLands;

	/**
	 * Transient field that calculate player's current cash based on their
	 * investments.
	 * 
	 * @return
	 */
	@JsonProperty("cash")
	@Transient
	public double getCash() {
		return cash - (CollectionUtils.isEmpty(playerLands) ? 0d
				: playerLands.stream().mapToDouble(PlayerLand::getBuyAmount).sum());
	}

	@Transient
	public double getNetWorth() {
		return getCash() + (CollectionUtils.isEmpty(playerLands) ? 0d
				: playerLands.stream().mapToDouble(pl -> pl.getLand().getMarketValue() * (pl.getOwnership() / 100))
						.sum());
	}

	public void addLand(Land land, double ownership, double buyAmount) {
		playerLands = Objects.isNull(playerLands) ? new ArrayList<>() : playerLands;
		val opt = playerLands.stream().filter(pl -> Objects.equals(pl.getLandId(), land.getId())).findFirst();
		if (opt.isEmpty()) {
			playerLands.add(new PlayerLand(this, land, ownership, buyAmount));
			return;
		}
		opt.get().setOwnership(opt.get().getOwnership() + ownership);
		opt.get().setBuyAmount(opt.get().getBuyAmount() + buyAmount);
	}

}
