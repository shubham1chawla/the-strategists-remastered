package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "activities")
public class Activity implements Serializable {

	private static final long serialVersionUID = -6960667863521865520L;

	@AllArgsConstructor
	public enum Type {

		// Lord gave Player 1 a bonus of $100 after completing one turn.
		BONUS("%s gave %s a bonus of $%s after completing one turn."),

		// Player 1 bought 10% of Land 1 for $200.
		BUY("%s bought %s%% of %s for $%s."),

		// Player 1 applied a cheat!
		CHEAT("%s applied a cheat!"),

		// Admin caused Event 1 at Land 1 for 10 turns!
		EVENT("%s caused %s at %s for %s turns!"),

		// Player 1 just got arrested!
		JAIL("%s just got arrested!"),

		// Player 1 joined the game with 100 cash.
		JOIN("%s joined the game with %s cash."),

		// Admin kicked Player 1 out!
		KICK("%s kicked %s out!"),

		// Player 1 paid $100 rent to Player 2 for Land 1.
		RENT("%s paid $%s rent to %s for %s."),

		// Player 1 traded 50% of Land 1 with Player 2 for $100.
		TRADE("%s traded %s% of %s with %s for $%s.");

		private String format;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

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

	public Activity(Type type, String... values) {
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

	@JsonValue
	public String toString() {
		switch (type) {
		case BONUS:
			return String.format(type.format, val1, val2, val3);
		case BUY:
			return String.format(type.format, val1, val2, val3, val4);
		case CHEAT:
			return String.format(type.format, val1);
		case EVENT:
			return String.format(type.format, val1, val2, val3, val4);
		case JAIL:
			return String.format(type.format, val1);
		case JOIN:
			return String.format(type.format, val1, val2);
		case KICK:
			return String.format(type.format, val1, val2);
		case RENT:
			return String.format(type.format, val1, val2, val3, val4);
		case TRADE:
			return String.format(type.format, val1, val2, val3, val4, val5);
		default:
			throw new IllegalStateException(type + " is not a valid Log Type!");
		}
	}

	public static Activity ofBonus(String admin, String player, double amount) {
		return new Activity(Type.BONUS, admin, player, Double.toString(amount));
	}

	public static Activity ofBuy(String buyer, double ownership, String land, double amount) {
		return new Activity(Type.BUY, buyer, Double.toString(ownership), land, Double.toString(amount));
	}

	public static Activity ofCheat(String player) {
		return new Activity(Type.CHEAT, player);
	}

	public static Activity ofEvent(String admin, String event, String land, int turns) {
		return new Activity(Type.EVENT, admin, event, land, Integer.toString(turns));
	}

	public static Activity ofJail(String player) {
		return new Activity(Type.JAIL, player);
	}

	public static Activity ofJoin(String player, double cash) {
		return new Activity(Type.JOIN, player, Double.toString(cash));
	}

	public static Activity ofKick(String admin, String player) {
		return new Activity(Type.KICK, admin, player);
	}

	public static Activity ofRent(String payer, double amount, String payee, String land) {
		return new Activity(Type.RENT, payer, Double.toString(amount), payee, land);
	}

	public static Activity ofTrade(String releaser, double percent, String land, String receiver, double amount) {
		return new Activity(Type.TRADE, releaser, Double.toString(percent), land, receiver, Double.toString(amount));
	}

}
