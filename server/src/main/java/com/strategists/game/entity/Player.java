package com.strategists.game.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

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

	private static final int PRECISION = 2;

	public enum State {
		ACTIVE, BANKRUPT, JAIL;
	}

	public Player(String username, double baseCash, String password) {
		Assert.hasText(username, "Username can't be empty!");
		Assert.isTrue(baseCash > 0, "Cash can't be negative!");
		Assert.hasText(password, "Password can't be empty!");

		this.username = username;
		this.baseCash = baseCash;
		this.password = password;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@JsonIgnore
	@ToString.Exclude
	@Column(nullable = false, unique = false)
	private String password;

	/**
	 * Player's cash will be dynamically calculated based on their investments.
	 * Check {@link Player#getCash()} for details.
	 */
	@JsonIgnore
	@Column(nullable = false, precision = PRECISION)
	private Double baseCash;

	@Column(nullable = true, columnDefinition = "INTEGER DEFAULT 0")
	private Integer index = 0;

	@Column(nullable = true, columnDefinition = "VARCHAR(8) DEFAULT 'ACTIVE'")
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

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "targetPlayer", cascade = CascadeType.ALL)
	private List<Rent> receivedRents;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcePlayer", cascade = CascadeType.ALL)
	private List<Rent> paidRents;

	/**
	 * Transient field that calculate player's current cash based on their
	 * investments and rents.
	 * 
	 * @return
	 */
	@Transient
	@JsonProperty("cash")
	public double getCash() {
		val credits = baseCash + sum(receivedRents, Rent::getRentAmount);
		val debits = sum(paidRents, Rent::getRentAmount) + sum(playerLands, PlayerLand::getBuyAmount);
		return BigDecimal.valueOf(credits - debits).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Aggregation of player's cash and investments' worth. Note that investments
	 * are reverted post bankruptcy and don't count towards net worth.
	 * 
	 * @return
	 */
	@Transient
	public double getNetWorth() {
		return (isBankrupt() ? 0d : sum(playerLands, pl -> pl.getLand().getMarketValue() * (pl.getOwnership() / 100)))
				+ getCash();
	}

	@Transient
	@JsonIgnore
	public boolean isBankrupt() {
		return State.BANKRUPT.equals(state);
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

	public void addRent(Rent rent) {
		if (Objects.isNull(receivedRents)) {
			receivedRents = new ArrayList<>();
		}
		receivedRents.add(rent);
	}

	private static <T> double sum(List<T> list, ToDoubleFunction<T> mapper) {
		return Objects.isNull(list) ? 0d : list.stream().mapToDouble(mapper).sum();
	}

}
