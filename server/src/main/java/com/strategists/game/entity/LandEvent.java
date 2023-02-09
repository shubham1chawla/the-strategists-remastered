package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "lands_events")
@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
@AssociationOverride(name = "pk.event", joinColumns = @JoinColumn(name = "eventId"))
public class LandEvent implements Serializable {

	private static final long serialVersionUID = -458918820184295751L;

	@EmbeddedId
	private LandEventId pk = new LandEventId();

	@Column(nullable = false)
	private Integer life;

	@Column(nullable = false)
	private Integer level;

	@Transient
	public Land getLand() {
		return pk.getLand();
	}

	@Transient
	public Event getEvent() {
		return pk.getEvent();
	}

	@Transient
	public Long getLandId() {
		return getLand().getId();
	}

	@Transient
	public Long getEventId() {
		return getEvent().getId();
	}

}
