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

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "activities")
public class Activity implements Serializable {

	private static final long serialVersionUID = -6960667863521865520L;

	public enum Type {
		BANKRUPT, BONUS, CHEAT, EVENT, INVEST, JAIL, JOIN, KICK, MOVE, RENT, START, TRADE, TURN;
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

	public static Activity ofBankrupt(String username) {
		return new Activity(Type.BANKRUPT, username);
	}

	public static Activity ofBonus(String admin, String player, double amount) {
		return new Activity(Type.BONUS, admin, player, Double.toString(amount));
	}

	public static Activity ofCheat(String player) {
		return new Activity(Type.CHEAT, player);
	}

	public static Activity ofEvent(String admin, String event, String land, int turns) {
		return new Activity(Type.EVENT, admin, event, land, Integer.toString(turns));
	}

	public static Activity ofInvest(String buyer, double ownership, String land) {
		return new Activity(Type.INVEST, buyer, Double.toString(ownership), land);
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

	public static Activity ofMove(String player, String land) {
		return new Activity(Type.MOVE, player, land);
	}

	public static Activity ofRent(String payer, double amount, String payee, String land) {
		return new Activity(Type.RENT, payer, Double.toString(amount), payee, land);
	}

	public static Activity ofStart(String admin) {
		return new Activity(Type.START, admin);
	}

	public static Activity ofTrade(String releaser, double percent, String land, String receiver, double amount) {
		return new Activity(Type.TRADE, releaser, Double.toString(percent), land, receiver, Double.toString(amount));
	}

	public static Activity ofTurn(String from, String to) {
		return new Activity(Type.TURN, from, to);
	}

}
