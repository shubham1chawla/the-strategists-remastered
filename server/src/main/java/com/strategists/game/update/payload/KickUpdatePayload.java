package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KickUpdatePayload implements UpdatePayload<Long> {

    private Activity activity;
    private Long payload;

    @Override
    public UpdateType getType() {
        return UpdateType.KICK;
    }

}
