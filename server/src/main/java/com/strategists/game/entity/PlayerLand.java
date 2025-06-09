package com.strategists.game.entity;

import com.strategists.game.util.MathUtil;
import jakarta.persistence.AssociationOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players_lands")
@AssociationOverride(name = "pk.player", joinColumns = @JoinColumn(name = "playerId"))
@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
public class PlayerLand implements Serializable {

    @Serial
    private static final long serialVersionUID = -7469278040684677456L;

    @EmbeddedId
    private PlayerLandId pk = new PlayerLandId();

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double ownership;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double buyAmount;

    public PlayerLand(Player player, Land land, double ownership, double buyAmount) {
        this.pk = new PlayerLandId(player, land);

        Assert.isTrue(ownership > 0 && ownership <= 100, "Ownership should be between 0 and 100!");
        Assert.isTrue(buyAmount > 0, "Buy amount can't be negative!");

        this.ownership = MathUtil.round(ownership);
        this.buyAmount = MathUtil.round(buyAmount);
    }

    @Transient
    public Player getPlayer() {
        return pk.getPlayer();
    }

    @Transient
    public Land getLand() {
        return pk.getLand();
    }

    @Transient
    public Long getPlayerId() {
        return getPlayer().getId();
    }

    @Transient
    public Long getLandId() {
        return getLand().getId();
    }

}
