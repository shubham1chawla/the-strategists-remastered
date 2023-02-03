package com.strategists.game.entity;

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

import lombok.Data;

@Data
@Entity
@Table(name = "players")
public class Player {

	enum State {
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

	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private State state = State.ACTIVE;

	@Column(nullable = false)
	private boolean turn = false;

	@Column(nullable = true)
	private Integer remainingJailLife = 0;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.player", cascade = CascadeType.ALL)
	private List<PlayerLand> playerLands;

	@Transient
	public double getNetWorth() {
		return cash + (Objects.isNull(playerLands) ? 0d
				: playerLands.stream().map(pl -> pl.getLand().getMarketValue() * (pl.getOwnership() / 100))
						.collect(Collectors.summingDouble(Double::doubleValue)));
	}

}
