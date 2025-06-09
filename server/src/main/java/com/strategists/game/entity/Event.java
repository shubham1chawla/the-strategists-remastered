package com.strategists.game.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "events")
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 6172827914156198508L;

    public enum Type {
        POSITIVE, NEGATIVE;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Event's factor shouldn't be revealed as it is sensitive information used to
     * calculate land's market value.
     */
    @JsonIgnore
    @Column(nullable = false)
    private Integer factor;

    /**
     * Since event's factor can't be revealed, event's type makes up for the nature
     * of the event. UI can use this information to correctly designate the event's
     * nature on the map.
     *
     * @return Event Type
     */
    @Transient
    public Type getType() {
        return factor > 0 ? Type.POSITIVE : Type.NEGATIVE;
    }

}
