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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.val;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player implements Serializable {

	private static final long serialVersionUID = -7588097421340659821L;

	public enum State {
		INVITED, ACTIVE, BANKRUPT;
	}

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@JsonIgnore
	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * Player's cash will be dynamically calculated based on their investments.
	 * Check {@link Player#getCash()} for details.
	 */
	@JsonIgnore
	@Column(nullable = false, precision = MathUtil.PRECISION)
	private Double baseCash;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer index = 0;

	@Column(nullable = false, columnDefinition = "VARCHAR(8) DEFAULT 'INVITED'")
	@Enumerated(EnumType.STRING)
	private State state = State.INVITED;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean turn = false;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true, columnDefinition = "INTEGER DEFAULT NULL")
	private Integer remainingSkipsCount;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
	private Game game;

	@ToString.Exclude
	@JsonProperty("lands")
	@JsonIgnoreProperties({ "pk", "player", "land", "playerId" })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.player", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlayerLand> playerLands;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "targetPlayer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Rent> receivedRents;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcePlayer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Rent> paidRents;

	public Player(Game game, String email, double baseCash) {
		Assert.isTrue(EmailValidator.getInstance().isValid(email), "Email is not valid!");
		Assert.isTrue(baseCash > 0, "Cash can't be negative!");

		this.game = game;
		this.username = email.split("@")[0];
		this.email = email;
		this.baseCash = MathUtil.round(baseCash);
		this.remainingSkipsCount = game.getAllowedSkipsCount();
	}

	/**
	 * Transient field that calculate player's current cash based on their
	 * investments and rents.
	 * 
	 * @return
	 */
	@Transient
	@JsonProperty("cash")
	public double getCash() {
		val credits = baseCash + MathUtil.sum(receivedRents, Rent::getRentAmount);
		val debits = MathUtil.sum(paidRents, Rent::getRentAmount) + MathUtil.sum(playerLands, PlayerLand::getBuyAmount);
		return MathUtil.round(credits - debits);
	}

	/**
	 * Aggregation of player's cash and investments' worth. Note that investments
	 * are reverted post bankruptcy and don't count towards net worth.
	 * 
	 * @return
	 */
	@Transient
	public double getNetWorth() {
		val investments = isBankrupt() ? 0d
				: MathUtil.sum(playerLands, pl -> pl.getLand().getMarketValue() * (pl.getOwnership() / 100));
		return MathUtil.round(investments + getCash());
	}

	@Transient
	@JsonIgnore
	public boolean isInvited() {
		return State.INVITED.equals(state);
	}

	@Transient
	@JsonIgnore
	public boolean isBankrupt() {
		return State.BANKRUPT.equals(state);
	}

	@Transient
	@JsonIgnore
	public long getGameId() {
		return game.getId();
	}

	@Transient
	@JsonIgnore
	public String getGamePlayerKey() {
		return String.format("game-%s-player-%s", getGameId(), getUsername());
	}

	@Transient
	@JsonInclude(Include.NON_NULL)
	public Integer getAllowedSkipsCount() {
		return game.getAllowedSkipsCount();
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

}
