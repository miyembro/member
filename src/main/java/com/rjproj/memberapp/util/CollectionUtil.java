package com.rjproj.memberapp.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !CollectionUtil.isEmpty(c);
    }

    public static <T, R> List<R> map(Collection<T> c, Function<? super T, ? extends R> mapper) {
        return c.stream().map(mapper).collect(Collectors.toList());
    }
}
