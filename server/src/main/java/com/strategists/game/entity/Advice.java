package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.advice.AdviceType;
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
@Table(name = "advices")
public class Advice implements Serializable {

    @Serial
    private static final long serialVersionUID = -5037590930687765298L;

    public enum State {
        NEW, FOLLOWED;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean viewed;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdviceType type;

    @JsonIgnore
    @Column(nullable = true)
    private String val1;

    @JsonIgnore
    @Column(nullable = true)
    private String val2;

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

    public Advice(AdviceType type, int priority, Player player, String... values) {
        this.type = type;
        this.priority = priority;
        this.player = player;
        this.game = player.getGame();
        this.viewed = false;
        this.state = State.NEW;
        this.val1 = values.length > 0 ? values[0] : null;
        this.val2 = values.length > 1 ? values[1] : null;
        if (values.length > 2) {
            throw new IllegalArgumentException("More than 3 values are not supported!");
        }
    }

    @Transient
    @JsonProperty("text")
    public String getText() {
        switch (getType()) {
            case AVOID_TIMEOUT -> {
                return "The game completed your turn because of inactivity. Please use the 'Skip' button!";
            }
            case CONCENTRATE_INVESTMENTS -> {
                return String.format("You have invested all over the map; try to have at least %s investments close by!", getVal1());
            }
            case FREQUENTLY_INVEST -> {
                return String.format("You have yet to invest in the last %s turns. Try investing more to get a competitive edge!", getVal1());
            }
            case POTENTIAL_BANKRUPTCY -> {
                return String.format("You will go bankrupt if you land on %s! Keep more than $%s to avoid bankruptcy.", getVal2(), getVal1());
            }
            case SIGNIFICANT_INVESTMENTS -> {
                return String.format("Try investing more than %s%% to get steep rent from others.", getVal1());
            }
            default -> {
                return String.format("Unknown advice type: '%s'", getType());
            }
        }
    }

    @Transient
    @JsonProperty("playerId")
    public long getPlayerId() {
        return player.getId();
    }

    public static Advice ofFrequentlyInvest(int priority, Player player, int turnsElapsed) {
        return new Advice(AdviceType.FREQUENTLY_INVEST, priority, player, String.valueOf(turnsElapsed));
    }

    public static Advice ofAvoidTimeout(int priority, Player player) {
        return new Advice(AdviceType.AVOID_TIMEOUT, priority, player);
    }

    public static Advice ofSignificantInvestments(int priority, Player player, double minAverageOwnership) {
        return new Advice(AdviceType.SIGNIFICANT_INVESTMENTS, priority, player, String.valueOf(minAverageOwnership));
    }

    public static Advice ofConcentrateInvestments(int priority, Player player, int minInvestmentsCount) {
        return new Advice(AdviceType.CONCENTRATE_INVESTMENTS, priority, player, String.valueOf(minInvestmentsCount));
    }

    public static Advice ofPotentialBankruptcy(int priority, Player player, double maxRentAmount, Land maxRentLand) {
        return new Advice(AdviceType.POTENTIAL_BANKRUPTCY, priority, player, String.valueOf(maxRentAmount), maxRentLand.getName());
    }

}
