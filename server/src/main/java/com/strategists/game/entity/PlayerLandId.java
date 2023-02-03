package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Embeddable
public class PlayerLandId implements Serializable {

	private static final long serialVersionUID = 2411177966930860531L;

	@ManyToOne
	private Player player;

	@ManyToOne
	private Land land;

}
