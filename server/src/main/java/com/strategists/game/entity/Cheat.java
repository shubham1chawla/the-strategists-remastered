package com.strategists.game.entity;

import com.strategists.game.request.CreateCheatRequest;
import com.strategists.game.util.MathUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.val;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cheats")
public class Cheat implements Serializable {

    @Serial
    private static final long serialVersionUID = -4543480457374693950L;

    public enum Type {
        CASH, MOVE, LIFE;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false, precision = MathUtil.PRECISION)
    private Double amount;

    @Column(nullable = false)
    private Integer life;

    public static Cheat fromRequest(CreateCheatRequest request) {
        val cheat = new Cheat();
        cheat.setCode(request.getCode());
        cheat.setType(request.getType());
        cheat.setAmount(request.getAmount());
        cheat.setLife(request.getLife());
        return cheat;
    }

}
