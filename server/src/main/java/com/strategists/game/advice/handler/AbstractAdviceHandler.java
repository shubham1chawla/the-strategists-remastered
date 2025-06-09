package com.strategists.game.advice.handler;

import com.strategists.game.advice.AdviceContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.util.Assert;

public abstract class AbstractAdviceHandler implements Command {

    @Override
    public boolean execute(Context context) throws Exception {
        Assert.isTrue(context.getClass().isAssignableFrom(AdviceContext.class),
                context + " is not assignable to " + AdviceContext.class);
        generate((AdviceContext) context);
        return false;
    }

    protected abstract void generate(AdviceContext context);

}
