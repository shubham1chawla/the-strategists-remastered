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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strategists.game.service.PredictionService.Prediction;
import com.strategists.game.update.UpdateType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@Entity
@NoArgsConstructor
@Table(name = "activities")
public class Activity implements Serializable {

	private static final long serialVersionUID = -6960667863521865520L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UpdateType type;

	@Column(nullable = false)
	private String val1;

	@Column(nullable = true)
	private String val2;

	@Column(nullable = true)
	private String val3;

	@Column(nullable = true)
	private String val4;

	@Column(nullable = true)
	private String val5;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
	private Game game;

	public Activity(Game game, UpdateType type, String... values) {
		this.game = game;
		this.type = type;
		this.val1 = values.length > 0 ? values[0] : null;
		this.val2 = values.length > 1 ? values[1] : null;
		this.val3 = values.length > 2 ? values[2] : null;
		this.val4 = values.length > 3 ? values[3] : null;
		this.val5 = values.length > 4 ? values[4] : null;
		if (values.length > 5) {
			throw new IllegalArgumentException("More than 5 values are not supported!");
		}
	}

	public static Activity ofBankruptcy(Player player) {
		return new Activity(player.getGame(), UpdateType.BANKRUPTCY, player.getUsername());
	}

	public static Activity ofEnd(Player player) {
		return new Activity(player.getGame(), UpdateType.END, player.getUsername());
	}

	public static Activity ofInvest(Player player, Land land, double ownership) {
		return new Activity(player.getGame(), UpdateType.INVEST, player.getUsername(), Double.toString(ownership),
				land.getName());
	}

	public static Activity ofInvite(Player player) {
		val game = player.getGame();
		return new Activity(game, UpdateType.INVITE, game.getAdminUsername(), player.getEmail());
	}

	public static Activity ofJoin(Player player) {
		return new Activity(player.getGame(), UpdateType.JOIN, player.getUsername());
	}

	public static Activity ofKick(Player player) {
		val game = player.getGame();
		return new Activity(game, UpdateType.KICK, game.getAdminUsername(), player.getUsername());
	}

	public static Activity ofMove(Player player, int move, Land land) {
		val game = player.getGame();
		return new Activity(game, UpdateType.MOVE, player.getUsername(), Integer.toString(move), land.getName());
	}

	public static Activity ofPrediction(Player player, Prediction prediction) {
		val game = player.getGame();
		return new Activity(game, UpdateType.PREDICTION, game.getAdminUsername(), player.getUsername(),
				prediction.name());
	}

	public static Activity ofRent(Rent rent) {
		val payer = rent.getSourcePlayer();
		val payee = rent.getTargetPlayer();
		val land = rent.getLand();
		val amount = rent.getRentAmount();
		return new Activity(payer.getGame(), UpdateType.RENT, payer.getUsername(), Double.toString(amount),
				payee.getUsername(), land.getName());
	}

	public static Activity ofReset(Game game) {
		return new Activity(game, UpdateType.RESET, game.getAdminUsername());
	}

	public static Activity ofStart(Player player) {
		val game = player.getGame();
		return new Activity(game, UpdateType.START, game.getAdminUsername(), player.getUsername());
	}

	public static Activity ofTurn(Player previousPlayer, Player currentPlayer) {
		val game = previousPlayer.getGame();
		return new Activity(game, UpdateType.TURN, previousPlayer.getUsername(), currentPlayer.getUsername());
	}

}
