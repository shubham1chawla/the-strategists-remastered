package com.strategists.game.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity
@Table(name = "players")
public class Player implements Serializable {

	private static final long serialVersionUID = -7588097421340659821L;

	public enum State {
		ACTIVE, DEAD, JAIL;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private Integer index;

	@Column(nullable = false, precision = 2)
	private Double cash;

	@Column(nullable = false, columnDefinition = "VARCHAR(6) DEFAULT 'ACTIVE'")
	@Enumerated(EnumType.STRING)
	private State state;

	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean turn;

	@Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
	private Integer remainingJailLife;

	@JsonProperty("investments")
	@JsonIgnoreProperties({ "pk", "player", "land", "playerId" })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.player", cascade = CascadeType.ALL)
	private List<PlayerLand> playerLands;

	/**
	 * This field is not accessible by default. It's made public when an API call
	 * explicitly ask for a player's investments.
	 * 
	 * @return
	 */
	@JsonIgnore
	@Transient
	public List<Land> getInvestments() {
		return Objects.isNull(playerLands) ? Collections.emptyList()
				: playerLands.stream().map(PlayerLand::getLand).collect(Collectors.toList());
	}

	@Transient
	public double getNetWorth() {
		return cash + (Objects.isNull(playerLands) ? 0d
				: playerLands.stream().mapToDouble(pl -> pl.getLand().getMarketValue() + (pl.getOwnership() / 100))
						.sum());
	}

}
