package com.strategists.game.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class LandEventId implements Serializable {

    @Serial
    private static final long serialVersionUID = 271923618412311775L;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Land land;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    public LandEventId(Land land, Event event) {
        Assert.notNull(land, "Land shouldn't be null!");
        Assert.notNull(event, "Event shouldn't be null!");

        this.land = land;
        this.event = event;
    }

}
