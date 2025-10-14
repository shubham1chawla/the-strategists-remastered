package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strategists.game.util.MathUtil;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@Table(name = "players")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = -7588097421340659821L;

    public enum State {
        ACTIVE, BANKRUPT;
    }

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Sensitive information, UI doesn't need to know what's the user's email.
     */
    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer index = 0;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean turn = false;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean host = false;

    @Column(nullable = true)
    private Integer bankruptcyOrder;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, columnDefinition = "INTEGER DEFAULT NULL")
    private Integer lastInvestStep;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, columnDefinition = "INTEGER DEFAULT NULL")
    private Integer lastSkippedStep;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, columnDefinition = "INTEGER DEFAULT NULL")
    private Integer remainingSkipsCount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    @ToString.Exclude
    @JsonProperty("lands")
    @JsonIgnoreProperties({"pk", "player", "land", "playerId"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerLand> playerLands;

    @ToString.Exclude
    @JsonIgnoreProperties({"sourcePlayer", "targetPlayer", "land", "targetPlayerId"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "targetPlayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rent> receivedRents;

    @ToString.Exclude
    @JsonIgnoreProperties({"sourcePlayer", "targetPlayer", "land", "sourcePlayerId"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcePlayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rent> paidRents;

    public Player(Game game, String email) {
        Assert.isTrue(EmailValidator.getInstance().isValid(email), "Email is not valid!");
        Assert.notNull(game.getPlayerBaseCash(), "Cash can't be null!");
        Assert.isTrue(game.getPlayerBaseCash() > 0, "Cash can't be negative!");

        this.game = game;
        this.email = email;
        this.remainingSkipsCount = game.getAllowedSkipsCount();
    }

    /**
     * Transient field that calculate player's current cash based on their
     * investments and rents.
     *
     * @return Player's Cash
     */
    @Transient
    @JsonProperty("cash")
    public double getCash() {
        final var credits = game.getPlayerBaseCash() + MathUtil.sum(receivedRents, Rent::getRentAmount);
        final var debits = MathUtil.sum(paidRents, Rent::getRentAmount) + MathUtil.sum(playerLands, PlayerLand::getBuyAmount);
        return MathUtil.round(credits - debits);
    }

    /**
     * Aggregation of player's cash and investments' worth. Note that investments
     * are reverted post bankruptcy and don't count towards net worth.
     *
     * @return Player's Net Worth
     */
    @Transient
    public double getNetWorth() {
        final var investments = isBankrupt() ? 0d
                : MathUtil.sum(playerLands, pl -> pl.getLand().getMarketValue() * (pl.getOwnership() / 100));
        return MathUtil.round(investments + getCash());
    }

    @Transient
    @JsonIgnore
    public boolean isBankrupt() {
        return State.BANKRUPT.equals(state);
    }

    public void addLand(Land land, double ownership, double buyAmount) {
        playerLands = Objects.isNull(playerLands) ? new ArrayList<>() : playerLands;
        final var opt = playerLands.stream().filter(pl -> Objects.equals(pl.getLandId(), land.getId())).findFirst();
        if (opt.isEmpty()) {
            playerLands.add(new PlayerLand(this, land, ownership, buyAmount));
            return;
        }
        opt.get().setOwnership(opt.get().getOwnership() + ownership);
        opt.get().setBuyAmount(opt.get().getBuyAmount() + buyAmount);
    }

    public void addRent(Rent rent) {
        if (Objects.isNull(receivedRents)) {
            receivedRents = new ArrayList<>();
        }
        receivedRents.add(rent);
    }

}
