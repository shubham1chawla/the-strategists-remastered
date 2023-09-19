package com.strategists.game.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.strategists.game.request.CreateCheatRequest;
import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.val;

@Data
@Entity
@Table(name = "cheats")
public class Cheat {

	public enum Type {
		CASH, MOVE, LIFE;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String code;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	@Column(nullable = false, precision = MathUtil.PRECISION)
	private Double amount;

	@Column(nullable = false)
	private Integer life;

	public static Cheat fromRequest(CreateCheatRequest request) {
		val cheat = new Cheat();
		cheat.setCode(request.getCode());
		cheat.setType(request.getType());
		cheat.setAmount(request.getAmount());
		cheat.setLife(request.getLife());
		return cheat;
	}

}
