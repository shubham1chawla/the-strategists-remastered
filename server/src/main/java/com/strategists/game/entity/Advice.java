package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.advice.AdviceType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "advices")
public class Advice implements Serializable {

	private static final long serialVersionUID = -5037590930687765298L;

	public enum State {
		NEW, FOLLOWED;
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Boolean viewed;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private State state;

	@JsonIgnore
	@Column(nullable = false)
	private int priority;

	@JsonIgnore
	@Column(nullable = false)
	private int newCount;

	@JsonIgnore
	@Column(nullable = false)
	private int followedCount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AdviceType type;

	@Column(nullable = true)
	private String val1;

	@Column(nullable = true)
	private String val2;

	@Column(nullable = true)
	private String val3;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Player player;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Game game;

	public Advice(AdviceType type, int priority, Player player, String... values) {
		this.type = type;
		this.priority = priority;
		this.player = player;
		this.game = player.getGame();
		this.viewed = false;
		this.state = State.NEW;
		this.newCount = 1;
		this.followedCount = 0;
		this.val1 = values.length > 0 ? values[0] : null;
		this.val2 = values.length > 1 ? values[1] : null;
		this.val3 = values.length > 2 ? values[2] : null;
		if (values.length > 3) {
			throw new IllegalArgumentException("More than 3 values are not supported!");
		}
	}

	@Transient
	@JsonProperty("playerId")
	public long getPlayerId() {
		return player.getId();
	}

	public static Advice ofFrequentlyInvest(int priority, Player player, int turnsElapsed) {
		return new Advice(AdviceType.FREQUENTLY_INVEST, priority, player, String.valueOf(turnsElapsed));
	}

	public static Advice ofAvoidTimeout(int priority, Player player) {
		return new Advice(AdviceType.AVOID_TIMEOUT, priority, player);
	}

	public static Advice ofSignificantInvestments(int priority, Player player, double minAverageOwnership) {
		return new Advice(AdviceType.SIGNIFICANT_INVESTMENTS, priority, player, String.valueOf(minAverageOwnership));
	}

	public static Advice ofConcentrateInvestments(int priority, Player player, int minInvestmentsCount) {
		return new Advice(AdviceType.CONCENTRATE_INVESTMENTS, priority, player, String.valueOf(minInvestmentsCount));
	}

	public static Advice ofPotentialBankruptcy(int priority, Player player, double maxRentAmount, Land maxRentLand) {
		return new Advice(AdviceType.POTENTIAL_BANKRUPTCY, priority, player, String.valueOf(maxRentAmount),
				maxRentLand.getName());
	}

}
