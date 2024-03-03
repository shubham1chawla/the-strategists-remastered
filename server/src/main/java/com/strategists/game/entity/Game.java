package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "games")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game implements Serializable {

	private static final long serialVersionUID = -8431465657152445136L;

	public enum State {
		LOBBY, ACTIVE;
	}

	@Id
	@EqualsAndHashCode.Include
	private String code;

	@JsonIgnore
	@Column(nullable = false, unique = false, precision = MathUtil.PRECISION)
	private Double playerBaseCash;

	@JsonIgnore
	@Column(nullable = false, unique = false)
	private Integer diceSize;

	@JsonIgnore
	@Column(nullable = false, unique = false)
	private Double rentFactor;

	@JsonIgnore
	@Column(nullable = true, unique = false)
	private Integer allowedSkipsCount;

	@Column(nullable = false, columnDefinition = "VARCHAR(6) DEFAULT 'LOBBY'")
	@Enumerated(EnumType.STRING)
	private State state = State.LOBBY;

	@Transient
	@JsonIgnore
	public boolean isLobby() {
		return State.LOBBY.equals(state);
	}

	@Transient
	@JsonIgnore
	public boolean isActive() {
		return State.ACTIVE.equals(state);
	}

}
