package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonValue;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "lands")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Land implements Serializable {

    @Serial
    private static final long serialVersionUID = -5330429528909555218L;

    private static final double DAMPENER = 0.01;

    @AllArgsConstructor
    public enum PlayerPosition {
        TOP_LEFT("top-left"),
        TOP_RIGHT("top-right"),
        BOTTOM_LEFT("bottom-left"),
        BOTTOM_RIGHT("bottom-right");

        private final String value;

        @JsonValue
        public String getValue() {
            return value;
        }
    }

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerPosition playerPosition;

    /**
     * Land's sensitivity shouldn't be revealed and therefore marked as ignore. We
     * use this information to calculate the current market value of the land, which
     * is relevant.
     */
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false)
    private Integer sensitivity;

    /**
     * Similar to land's sensitivity, the base value is again a metric to calculate
     * the market value. Client should not be allowed to view this as it could
     * reveal the market value calculation formula.
     */
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double baseValue;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "game_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game;

    @ToString.Exclude
    @JsonProperty("players")
    @JsonIgnoreProperties({"pk", "player", "land", "landId"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerLand> playerLands;

    @ToString.Exclude
    @JsonProperty("events")
    @JsonIgnoreProperties({"pk", "land", "event", "landId"})
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.land", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LandEvent> landEvents;

    @Transient
    public double getMarketValue() {
        return baseValue + (sensitivity + getDelta()) * getTotalOwnership();
    }

    /**
     * Aggregation of all players' ownerships on this land. Note that the
     * aggregation avoids bankrupt players. This step will ensure that bankrupt
     * players' investments are available to other players and adjusts lands' market
     * value accordingly.
     *
     * @return Total Ownership out of 100
     */
    @Transient
    public double getTotalOwnership() {
        return MathUtil.sum(playerLands, pl -> pl.getPlayer().isBankrupt() ? 0d : pl.getOwnership());
    }

    /**
     * Event's delta is intermediary information used to calculate lands' market
     * values therefore shouldn't be revealed to the client.
     *
     * @return Change in sensitivity
     */
    @JsonIgnore
    @Transient
    public double getDelta() {
        return MathUtil.sum(landEvents, le -> DAMPENER * le.getEvent().getFactor() * le.getLevel() * le.getLife());
    }

    public void addEvent(Event event, int life, int level) {
        landEvents = Objects.isNull(landEvents) ? new ArrayList<>() : landEvents;
        final var opt = landEvents.stream().filter(le -> Objects.equals(le.getEventId(), event.getId())).findFirst();
        if (opt.isEmpty()) {
            landEvents.add(new LandEvent(this, event, life, level));
            return;
        }
        opt.get().setLife(opt.get().getLife() + life);
        opt.get().setLevel(level);
    }

}
