package com.strategists.game.advice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AdviceContext extends ContextBase implements Context {

	private static final long serialVersionUID = 8243870294194758570L;

	@EqualsAndHashCode.Include
	private Game game;
	private List<Advice> advices;

	public AdviceContext(Game game) {
		this.game = game;
		this.advices = new ArrayList<>();
	}

	public void addAdvice(Advice advice) {
		advices.add(advice);
	}

}
