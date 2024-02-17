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
import javax.persistence.Transient;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@Table(name = "games")
public class Game implements Serializable {

	private static final long serialVersionUID = -8431465657152445136L;

	public enum State {
		LOBBY, ACTIVE;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@Column(nullable = false, unique = false)
	private String adminUsername;

	@JsonIgnore
	@ToString.Exclude
	@Column(nullable = false, unique = true)
	private String adminEmail;

	@Column(nullable = true, columnDefinition = "VARCHAR(6) DEFAULT 'LOBBY'")
	@Enumerated(EnumType.STRING)
	private State state = State.LOBBY;

	public Game(String adminUsername, String adminEmail) {
		Assert.hasText(adminUsername, "Admin username is required!");
		Assert.isTrue(EmailValidator.getInstance().isValid(adminEmail), "Admin email is not valid!");

		this.adminUsername = adminUsername;
		this.adminEmail = adminEmail;
	}

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
