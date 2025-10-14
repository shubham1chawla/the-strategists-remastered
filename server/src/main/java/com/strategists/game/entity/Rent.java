package com.strategists.game.entity;

import com.strategists.game.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rents")
public class Rent implements Serializable {

    @Serial
    private static final long serialVersionUID = -2636338193178571280L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer step;

    @ManyToOne
    @JoinColumn(name = "source_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player sourcePlayer;

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player targetPlayer;

    @ManyToOne
    @JoinColumn(name = "land_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Land land;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double rentAmount;

    public Rent(Player sourcePlayer, Player targetPlayer, Land land, double rentAmount) {
        Assert.isTrue(Objects.equals(sourcePlayer.getGame(), targetPlayer.getGame()), "Players' games must match!");
        Assert.isTrue(Objects.equals(sourcePlayer.getGame(), land.getGame()), "Land's game must match!");

        this.step = sourcePlayer.getGame().getCurrentStep();
        this.sourcePlayer = sourcePlayer;
        this.targetPlayer = targetPlayer;
        this.land = land;
        this.rentAmount = MathUtil.round(rentAmount);
    }

    @Transient
    public Long getLandId() {
        return getLand().getId();
    }

    @Transient
    public Long getSourcePlayerId() {
        return getSourcePlayer().getId();
    }

    @Transient
    public Long getTargetPlayerId() {
        return getTargetPlayer().getId();
    }

}
