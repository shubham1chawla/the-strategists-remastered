package com.strategists.game.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MathUtil {

    public static final int PRECISION = 2;

    public static double round(double value) {
        return BigDecimal.valueOf(value).setScale(PRECISION, RoundingMode.HALF_UP).doubleValue();
    }

    public static <T> double sum(List<T> list, ToDoubleFunction<T> mapper) {
        return Objects.isNull(list) ? 0d : round(list.stream().mapToDouble(mapper).sum());
    }

}
