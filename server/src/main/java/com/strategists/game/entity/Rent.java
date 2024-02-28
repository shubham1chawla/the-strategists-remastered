package com.strategists.game.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import com.strategists.game.util.MathUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rents")
public class Rent implements Serializable {

	private static final long serialVersionUID = -2636338193178571280L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "source_id", referencedColumnName = "id", nullable = false)
	private Player sourcePlayer;

	@ManyToOne
	@JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
	private Player targetPlayer;

	@ManyToOne
	@JoinColumn(name = "land_id", referencedColumnName = "id", nullable = false)
	private Land land;

	@Column(nullable = false, precision = MathUtil.PRECISION)
	private Double rentAmount;

	public Rent(Player sourcePlayer, Player targetPlayer, Land land, double rentAmount) {
		Assert.isTrue(Objects.equals(sourcePlayer.getGame(), targetPlayer.getGame()), "Players' games must match!");
		Assert.isTrue(Objects.equals(sourcePlayer.getGame(), land.getGame()), "Land's game must match!");

		this.sourcePlayer = sourcePlayer;
		this.targetPlayer = targetPlayer;
		this.land = land;
		this.rentAmount = MathUtil.round(rentAmount);
	}

}
