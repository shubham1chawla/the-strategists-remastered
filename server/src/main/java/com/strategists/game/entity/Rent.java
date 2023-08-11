package com.strategists.game.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rents")
public class Rent implements Serializable {

	private static final long serialVersionUID = -2636338193178571280L;

	private static final int PRECISION = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "source_id", referencedColumnName = "id")
	private Player sourcePlayer;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "target_id", referencedColumnName = "id")
	private Player targetPlayer;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "land_id", referencedColumnName = "id")
	private Land land;

	@Column(nullable = false, precision = PRECISION)
	private Double rentAmount;

	public Rent(Player sourcePlayer, Player targetPlayer, Land land, double rentAmount) {
		this.sourcePlayer = sourcePlayer;
		this.targetPlayer = targetPlayer;
		this.land = land;
		this.rentAmount = BigDecimal.valueOf(rentAmount).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
	}

}
