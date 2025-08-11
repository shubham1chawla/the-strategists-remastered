package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
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
    private Integer turn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UpdateType type;

    @Column(nullable = true)
    private String val1;

    @Column(nullable = true)
    private String val2;

    @Column(nullable = true)
    private String val3;

    @Column(nullable = true)
    private String val4;

    @Column(nullable = true)
    private String val5;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    public Activity(Game game, UpdateType type, String... values) {
        this.game = game;
        this.turn = game.getTurn();
        this.type = type;
        this.val1 = values.length > 0 ? values[0] : null;
        this.val2 = values.length > 1 ? values[1] : null;
        this.val3 = values.length > 2 ? values[2] : null;
        this.val4 = values.length > 3 ? values[3] : null;
        this.val5 = values.length > 4 ? values[4] : null;
        if (values.length > 5) {
            throw new IllegalArgumentException("More than 5 values are not supported!");
        }
    }

    public static Activity ofBankruptcy(Player player) {
        return new Activity(player.getGame(), UpdateType.BANKRUPTCY, player.getUsername());
    }

    public static Activity ofCreate(Player player) {
        return new Activity(player.getGame(), UpdateType.CREATE, player.getUsername(), player.getGameCode());
    }

    public static Activity ofInvest(Player player, Land land, double ownership) {
        return new Activity(player.getGame(), UpdateType.INVEST, player.getUsername(), Double.toString(ownership),
                land.getName());
    }

    public static Activity ofJoin(Player player) {
        return new Activity(player.getGame(), UpdateType.JOIN, player.getUsername());
    }

    public static Activity ofKick(Player player) {
        return new Activity(player.getGame(), UpdateType.KICK, player.getUsername());
    }

    public static Activity ofMove(Player player, int move, Land land) {
        val game = player.getGame();
        return new Activity(game, UpdateType.MOVE, player.getUsername(), Integer.toString(move), land.getName());
    }

    public static Activity ofPrediction(PlayerPrediction playerPrediction) {
        val player = playerPrediction.getPlayer();
        return new Activity(player.getGame(), UpdateType.PREDICTION, player.getUsername(), playerPrediction.getPrediction().name());
    }

    public static Activity ofRent(Rent rent) {
        val payer = rent.getSourcePlayer();
        val payee = rent.getTargetPlayer();
        val land = rent.getLand();
        val amount = rent.getRentAmount();
        return new Activity(payer.getGame(), UpdateType.RENT, payer.getUsername(), Double.toString(amount),
                payee.getUsername(), land.getName());
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
        val game = previousPlayer.getGame();
        return new Activity(game, UpdateType.TURN, previousPlayer.getUsername(), currentPlayer.getUsername());
    }

    public static Activity ofWin(Player player) {
        return new Activity(player.getGame(), UpdateType.WIN, player.getUsername());
    }

}
