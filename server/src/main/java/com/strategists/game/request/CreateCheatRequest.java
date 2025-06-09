package com.strategists.game.request;

import com.strategists.game.entity.Cheat.Type;
import lombok.Data;

@Data
public class CreateCheatRequest {

    private String code;
    private Type type;
    private Double amount;
    private Integer life;

}
