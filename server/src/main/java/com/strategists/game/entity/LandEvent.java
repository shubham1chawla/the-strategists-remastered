package com.strategists.game.entity;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "lands_events")
@AssociationOverride(name = "pk.land", joinColumns = @JoinColumn(name = "landId"))
@AssociationOverride(name = "pk.event", joinColumns = @JoinColumn(name = "eventId"))
public class LandEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = -458918820184295751L;

    private static final int MAX_LIFE = 10;
    private static final int MAX_LEVEL = 10;

    @EmbeddedId
    private LandEventId pk = new LandEventId();

    @Column(nullable = false)
    private Integer life;

    @Column(nullable = false)
    private Integer level;

    public LandEvent(Land land, Event event, int life, int level) {
        this.pk = new LandEventId(land, event);
        setLife(life);
        setLevel(level);
    }

    @Transient
    public Land getLand() {
        return pk.getLand();
    }

    @Transient
    public Event getEvent() {
        return pk.getEvent();
    }

    @Transient
    public Long getLandId() {
        return getLand().getId();
    }

    @Transient
    public Long getEventId() {
        return getEvent().getId();
    }

    public void setLife(int life) {
        this.life = Math.max(0, Math.min(life, MAX_LIFE));
    }

    public void setLevel(int level) {
        this.level = Math.max(0, Math.min(level, MAX_LEVEL));
    }

}
