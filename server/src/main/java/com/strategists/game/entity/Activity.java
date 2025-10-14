package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.update.UpdateType;
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
@Table(name = "activities")
public class Activity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6960667863521865520L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer step;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UpdateType type;

    @JsonIgnore
    @Column(nullable = true)
    private String val1;

    @JsonIgnore
    @Column(nullable = true)
    private String val2;

    @JsonIgnore
    @Column(nullable = true)
    private String val3;

    @JsonIgnore
    @Column(nullable = true)
    private String val4;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    public Activity(Game game, UpdateType type, String... values) {
        this.game = game;
        this.step = game.getCurrentStep();
        this.type = type;
        this.val1 = values.length > 0 ? values[0] : null;
        this.val2 = values.length > 1 ? values[1] : null;
        this.val3 = values.length > 2 ? values[2] : null;
        this.val4 = values.length > 3 ? values[3] : null;
        if (values.length > 4) {
            throw new IllegalArgumentException("More than 5 values are not supported!");
        }
    }

    @Transient
    @JsonProperty("text")
    public String getText() {
        switch (getType()) {
            case BANKRUPTCY -> {
                return String.format("%s declared bankruptcy!", getVal1());
            }
            case CREATE -> {
                return String.format("%s created game %s", getVal1(), getVal2());
            }
            case INVEST -> {
                return String.format("%s invested in %s of %s!", getVal1(), getVal2(), getVal3());
            }
            case JOIN -> {
                return String.format("%s joined The Strategists!", getVal1());
            }
            case KICK -> {
                return String.format("Host kicked %s out!", getVal1());
            }
            case MOVE -> {
                return String.format("%s travelled %s steps and reached %s.", getVal1(), getVal2(), getVal3());
            }
            case PREDICTION -> {
                return PlayerPrediction.Prediction.WINNER.name().equals(getVal2())
                        ? String.format("%s is likely to win based on the predictions!", getVal1())
                        : String.format("%s leads slightly based on the predictions.", getVal1());
            }
            case RENT -> {
                return String.format("%s paid %s cash rent to %s for %s.", getVal1(), getVal2(), getVal3(), getVal4());
            }
            case RESET -> {
                return "Host restarted The Strategists!";
            }
            case SKIP -> {
                return String.format("%s's turn skipped due to inactivity!", getVal1());
            }
            case START -> {
                return String.format("The Strategists started! %s's turn to invest.", getVal1());
            }
            case TURN -> {
                return String.format("%s passed turn to %s.", getVal1(), getVal2());
            }
            case WIN -> {
                return String.format("%s won The Strategists!", getVal1());
            }
            default -> {
                return String.format("Unknown activity type: '%s'", getType());
            }
        }
    }

    public static Activity ofBankruptcy(Player player) {
        return new Activity(player.getGame(), UpdateType.BANKRUPTCY, player.getUsername());
    }

    public static Activity ofCreate(Player player) {
        return new Activity(player.getGame(), UpdateType.CREATE, player.getUsername(), player.getGame().getCode());
    }

    public static Activity ofInvest(Player player, Land land, double ownership) {
        return new Activity(player.getGame(), UpdateType.INVEST, player.getUsername(), Double.toString(ownership), land.getName());
    }

    public static Activity ofJoin(Player player) {
        return new Activity(player.getGame(), UpdateType.JOIN, player.getUsername());
    }

    public static Activity ofKick(Player player) {
        return new Activity(player.getGame(), UpdateType.KICK, player.getUsername());
    }

    public static Activity ofMove(Player player, int move, Land land) {
        final var game = player.getGame();
        return new Activity(game, UpdateType.MOVE, player.getUsername(), Integer.toString(move), land.getName());
    }

    public static Activity ofPrediction(PlayerPrediction playerPrediction) {
        final var player = playerPrediction.getPlayer();
        return new Activity(player.getGame(), UpdateType.PREDICTION, player.getUsername(), playerPrediction.getPrediction().name());
    }

    public static Activity ofRent(Rent rent) {
        final var payer = rent.getSourcePlayer();
        final var payee = rent.getTargetPlayer();
        final var land = rent.getLand();
        final var amount = rent.getRentAmount();
        return new Activity(payer.getGame(), UpdateType.RENT, payer.getUsername(), Double.toString(amount), payee.getUsername(), land.getName());
    }

    public static Activity ofReset(Game game) {
        return new Activity(game, UpdateType.RESET);
    }

    public static Activity ofSkip(Player player) {
        return new Activity(player.getGame(), UpdateType.SKIP, player.getUsername());
    }

    public static Activity ofStart(Player player) {
        return new Activity(player.getGame(), UpdateType.START, player.getUsername());
    }

    public static Activity ofTurn(Player previousPlayer, Player currentPlayer) {
        final var game = previousPlayer.getGame();
        return new Activity(game, UpdateType.TURN, previousPlayer.getUsername(), currentPlayer.getUsername());
    }

    public static Activity ofWin(Player player) {
        return new Activity(player.getGame(), UpdateType.WIN, player.getUsername());
    }

}
