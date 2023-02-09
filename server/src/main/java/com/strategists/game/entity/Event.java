package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "events")
public class Event implements Serializable {

	private static final long serialVersionUID = 6172827914156198508L;

	public enum Type {
		POSITIVE, NEGATIVE;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * Event's factor shouldn't be revealed as it is sensitive information used to
	 * calculate land's market value.
	 */
	@JsonIgnore
	@Column(nullable = false)
	private Integer factor;

	/**
	 * Since event's factor can't be revealed, event's type makes up for the nature
	 * of the event. UI can use this information to correctly designate the event's
	 * nature on the map.
	 * 
	 * @return
	 */
	@Transient
	public Type getType() {
		return factor > 0 ? Type.POSITIVE : Type.NEGATIVE;
	}

}
