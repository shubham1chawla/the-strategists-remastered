package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class LandEventId implements Serializable {

	private static final long serialVersionUID = 271923618412311775L;

	@ManyToOne
	private Land land;

	@ManyToOne
	private Event event;

	public LandEventId(Land land, Event event) {
		Assert.notNull(land, "Land shouldn't be null!");
		Assert.notNull(event, "Event shouldn't be null!");

		this.land = land;
		this.event = event;
	}

}
