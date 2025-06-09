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
@Table(name = "predictions")
public class Prediction implements Serializable {

    @Serial
    private static final long serialVersionUID = 2668335964192831780L;

    public enum Type {
        WINNER, BANKRUPT
    }

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer turn;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double winnerProbability;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double bankruptProbability;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

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

    public Prediction(Player player, double bankruptProbability, double winnerProbability, Type type) {
        this.game = player.getGame();
        this.turn = player.getGame().getTurn();
        this.player = player;
        this.bankruptProbability = MathUtil.round(bankruptProbability);
        this.winnerProbability = MathUtil.round(winnerProbability);
        this.type = type;
    }

    @Transient
    @JsonProperty("playerId")
    public long getPlayerId() {
        return player.getId();
    }

    @Transient
    @JsonIgnore
    public boolean isWinner() {
        return Type.WINNER.equals(type);
    }

    @Transient
    @JsonIgnore
    public boolean isBankrupt() {
        return Type.BANKRUPT.equals(type);
    }

}
