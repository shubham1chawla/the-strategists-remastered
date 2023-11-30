package com.strategists.game.update.payload;

import java.util.List;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Trend;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrendUpdatePayload implements UpdatePayload<List<Trend>> {

	private List<Trend> payload;

	@Override
	public UpdateType getType() {
		return UpdateType.TREND;
	}

	@Override
	public Activity getActivity() {
		return null;
	}

}
