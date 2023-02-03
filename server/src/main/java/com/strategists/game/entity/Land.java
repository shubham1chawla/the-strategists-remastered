package com.strategists.game.entity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "lands")
public class Land {

	private static final double EVENT_DAMPENER = 0.01;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private Integer index;

	@Column(nullable = false)
	private Integer sensitivity;

	@Column(nullable = false, precision = 2)
	private Double baseValue;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL)
	private List<PlayerLand> playerLands;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL)
	private List<LandEvent> landEvents;

	@Transient
	public double getMarketValue() {
		return baseValue + (sensitivity + getEventDelta()) * getTotalOwnership();
	}

	@Transient
	public double getTotalOwnership() {
		return Objects.isNull(playerLands) ? 0d
				: playerLands.stream().map(PlayerLand::getOwnership)
						.collect(Collectors.summingDouble(Double::doubleValue));
	}

	@Transient
	public double getEventDelta() {
		return Objects.isNull(landEvents) ? 0d
				: landEvents.stream()
						.map(le -> EVENT_DAMPENER * le.getEvent().getFactor() * le.getLevel() * le.getLife())
						.collect(Collectors.summingDouble(Double::doubleValue));
	}

}
