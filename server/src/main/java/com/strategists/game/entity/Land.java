package com.strategists.game.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

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

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;
import lombok.val;

@Data
@Entity
@Table(name = "lands")
public class Land implements Serializable {

	private static final long serialVersionUID = -5330429528909555218L;

	private static final double DAMPENER = 0.01;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer x;

	@Column(nullable = false)
	private Integer y;

	/**
	 * Land's sensitivity shouldn't be revealed and therefore marked as ignore. We
	 * use this information to calculate the current market value of the land, which
	 * is relevant.
	 */
	@JsonIgnore
	@Column(nullable = false)
	private Integer sensitivity;

	/**
	 * Similar to land's sensitivity, the base value is again a metric to calculate
	 * the market value. Client should not be allowed to view this as it could
	 * reveal the market value calculation formula.
	 */
	@JsonIgnore
	@Column(nullable = false, precision = 2)
	private Double baseValue;

	@ToString.Exclude
	@JsonProperty("players")
	@JsonIgnoreProperties({ "pk", "player", "land", "landId" })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL)
	private List<PlayerLand> playerLands;

	@ToString.Exclude
	@JsonProperty("events")
	@JsonIgnoreProperties({ "pk", "land", "event", "landId" })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL)
	private List<LandEvent> landEvents;

	@Transient
	public double getMarketValue() {
		return baseValue + (sensitivity + getDelta()) * getTotalOwnership();
	}

	/**
	 * Aggregation of all players' ownerships on this land. Note that the
	 * aggregation avoids bankrupt players. This step will ensure that bankrupt
	 * players' investments are available to other players and adjusts lands' market
	 * value accordingly.
	 * 
	 * @return
	 */
	@Transient
	public double getTotalOwnership() {
		return sum(playerLands, pl -> Player.State.BANKRUPT.equals(pl.getPlayer().getState()) ? 0d : pl.getOwnership());
	}

	/**
	 * Event's delta is an intermediary information used to calculate lands' market
	 * values therefore shouldn't be revealed to the client.
	 * 
	 * @return
	 */
	@JsonIgnore
	@Transient
	public double getDelta() {
		return sum(landEvents, le -> DAMPENER * le.getEvent().getFactor() * le.getLevel() * le.getLife());
	}

	public void addEvent(Event event, int life, int level) {
		landEvents = Objects.isNull(landEvents) ? new ArrayList<>() : landEvents;
		val opt = landEvents.stream().filter(le -> Objects.equals(le.getEventId(), event.getId())).findFirst();
		if (opt.isEmpty()) {
			landEvents.add(new LandEvent(this, event, life, level));
			return;
		}
		opt.get().setLife(opt.get().getLife() + life);
		opt.get().setLevel(level);
	}

	private static <T> double sum(List<T> list, ToDoubleFunction<T> mapper) {
		return CollectionUtils.isEmpty(list) ? 0d : list.stream().mapToDouble(mapper).sum();
	}

}
