package com.strategists.game.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

@Data
@Entity
@NoArgsConstructor
@Table(name = "trends")
public class Trend implements Serializable {

	private static final long serialVersionUID = 2688947717625456147L;

	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true)
	private Long playerId;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true)
	private Long landId;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true, precision = MathUtil.PRECISION)
	private Double cash;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true, precision = MathUtil.PRECISION)
	private Double netWorth;

	@JsonInclude(Include.NON_NULL)
	@Column(nullable = true, precision = MathUtil.PRECISION)
	private Double marketValue;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
	private Game game;

	public static Trend fromPlayer(Player player) {
		val trend = new Trend();
		trend.setGame(player.getGame());
		trend.setPlayerId(player.getId());
		trend.setCash(player.getCash());
		trend.setNetWorth(player.getNetWorth());
		return trend;
	}

	public static Trend fromLand(Land land) {
		val trend = new Trend();
		trend.setGame(land.getGame());
		trend.setLandId(land.getId());
		trend.setMarketValue(land.getMarketValue());
		return trend;
	}

}
