package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.strategists.game.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "games")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = -8431465657152445136L;

    public enum State {
        LOBBY, ACTIVE;
    }

    @Id
    @EqualsAndHashCode.Include
    private String code;

    @Column(nullable = false, unique = false)
    private Integer currentStep;

    @Column(nullable = false, unique = false)
    private Integer minPlayersCount;

    @Column(nullable = false, unique = false)
    private Integer maxPlayersCount;

    @Column(nullable = false)
    private String gameMapId;

    /**
     * Sensitive information, the UI doesn't need to know the base cash for a given map.
     */
    @JsonIgnore
    @Column(nullable = false, unique = false, precision = MathUtil.PRECISION)
    private Double playerBaseCash;

    @Column(nullable = false, unique = false)
    private Integer diceSize;

    /**
     * Sensitive information, the UI doesn't need to know the rent factor of the game.
     */
    @JsonIgnore
    @Column(nullable = false, unique = false)
    private Double rentFactor;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, unique = false)
    private Integer allowedSkipsCount;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, unique = false)
    private Integer skipPlayerTimeout;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, unique = false)
    private Integer cleanUpDelay;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false, unique = false)
    private Long createdAt;

    @JsonInclude(Include.NON_NULL)
    @Column(nullable = true, unique = false)
    private Long endedAt;

    @Transient
    @JsonIgnore
    public boolean isLobby() {
        return State.LOBBY.equals(state);
    }

    @Transient
    @JsonIgnore
    public boolean isActive() {
        return State.ACTIVE.equals(state);
    }

}
