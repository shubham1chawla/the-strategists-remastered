package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.strategists.game.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "trends")
public class Trend implements Serializable {

    @Serial
    private static final long serialVersionUID = 2688947717625456147L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer step;

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
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    public static Trend fromPlayer(Player player) {
        final var trend = new Trend();
        trend.setGame(player.getGame());
        trend.setStep(player.getGame().getCurrentStep());
        trend.setPlayerId(player.getId());
        trend.setCash(player.getCash());
        trend.setNetWorth(player.getNetWorth());
        return trend;
    }

    public static Trend fromLand(Land land) {
        final var trend = new Trend();
        trend.setGame(land.getGame());
        trend.setStep(land.getGame().getCurrentStep());
        trend.setLandId(land.getId());
        trend.setMarketValue(land.getMarketValue());
        return trend;
    }

}
