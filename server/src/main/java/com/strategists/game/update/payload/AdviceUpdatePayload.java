package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.update.UpdateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdviceUpdatePayload implements UpdatePayload<List<Advice>> {

    private List<Advice> payload;

    @Override
    public UpdateType getType() {
        return UpdateType.ADVICE;
    }

    @Override
    public Activity getActivity() {
        return null;
    }

}
