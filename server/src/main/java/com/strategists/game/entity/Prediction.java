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
import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "predictions")
public class Prediction implements Serializable {

	private static final long serialVersionUID = 2668335964192831780L;

	public enum Type {
		WINNER, BANKRUPT
	}

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = MathUtil.PRECISION)
	private Double winnerProbability;

	@Column(nullable = false, precision = MathUtil.PRECISION)
	private Double bankruptProbability;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

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

	public Prediction(Player player, double bankruptProbability, double winnerProbability, Type type) {
		this.game = player.getGame();
		this.player = player;
		this.bankruptProbability = MathUtil.round(bankruptProbability);
		this.winnerProbability = MathUtil.round(winnerProbability);
		this.type = type;
	}

	@Transient
	@JsonProperty("playerId")
	public long getPlayerId() {
		return player.getId();
	}

	@Transient
	@JsonIgnore
	public boolean isWinner() {
		return Type.WINNER.equals(type);
	}

	@Transient
	@JsonIgnore
	public boolean isBankrupt() {
		return Type.BANKRUPT.equals(type);
	}

}
