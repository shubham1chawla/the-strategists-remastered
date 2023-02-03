package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Embeddable
public class LandEventId implements Serializable {

	private static final long serialVersionUID = 271923618412311775L;

	@ManyToOne
	private Land land;

	@ManyToOne
	private Event event;

}
