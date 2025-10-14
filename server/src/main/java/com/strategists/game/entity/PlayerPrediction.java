package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "player_predictions")
public class PlayerPrediction implements Serializable {

    @Serial
    private static final long serialVersionUID = 2668335964192831780L;

    public enum Prediction {
        WINNER, BANKRUPT
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer step;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double winnerProbability;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double bankruptProbability;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Prediction prediction;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    public PlayerPrediction(Player player, double bankruptProbability, double winnerProbability, Prediction prediction) {
        this.game = player.getGame();
        this.step = player.getGame().getCurrentStep();
        this.player = player;
        this.bankruptProbability = MathUtil.round(bankruptProbability);
        this.winnerProbability = MathUtil.round(winnerProbability);
        this.prediction = prediction;
    }

    @Transient
    @JsonProperty("playerId")
    public long getPlayerId() {
        return player.getId();
    }

    @Transient
    @JsonIgnore
    public boolean isBankrupt() {
        return Prediction.BANKRUPT.equals(prediction);
    }

}
