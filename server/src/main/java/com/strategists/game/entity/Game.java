package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

	@Column(nullable = false, unique = false)
	private Integer turn;

	@Column(nullable = false, unique = false)
	private Integer minPlayersCount;

	@Column(nullable = false, unique = false)
	private Integer maxPlayersCount;

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

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true, unique = false)
	private Integer skipPlayerTimeout;

	@JsonIgnore
	@Column(nullable = true, unique = false)
	private Integer cleanUpDelay;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private State state;

	@JsonIgnore
	@Column(nullable = true, unique = false)
	private Long endedAt;

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

	public void setAllowedSkipsCount(int allowedSkipsCount) {
		Assert.isTrue(allowedSkipsCount > 0, "Allowed skips should be greater than 0!");
		this.allowedSkipsCount = allowedSkipsCount;
	}

	public void setSkipPlayerTimeout(int skipPlayerTimeout) {
		Assert.isTrue(skipPlayerTimeout > 10000, "Skip player timeout should be more than 10 seconds!");
		this.skipPlayerTimeout = skipPlayerTimeout;
	}

}
