package com.strategists.game.update;

import com.strategists.game.update.handler.UpdateHandler;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Aspect
@Component
public class UpdateAspect {

    private final Map<UpdateType, UpdateHandler> handlers;

    @Autowired
    public UpdateAspect(List<UpdateHandler> handlers) {
        this.handlers = handlers.stream().collect(Collectors.toMap(UpdateHandler::getType, Function.identity()));
    }

    @Around("@annotation(mapping)")
    public Object advice(ProceedingJoinPoint joinPoint, UpdateMapping mapping) throws Throwable {
        // Executing method and extracting return value
        Object returnValue = null;
        try {
            returnValue = joinPoint.proceed();
        } catch (Throwable ex) {
            log.warn("Exception occurred processing update type: {}", mapping.value());
            log.debug(ex);
            throw ex;
        }

        // Checking if handler is present for the activity
        if (!handlers.containsKey(mapping.value())) {
            log.warn("No handler registered for update type: {}", mapping.value());
            return returnValue;
        }

        // Handling update
        val handler = handlers.get(mapping.value());
        handler.handle(returnValue, joinPoint.getArgs());

        log.debug("Handled update type: {}", mapping.value());
        return returnValue;
    }

}
