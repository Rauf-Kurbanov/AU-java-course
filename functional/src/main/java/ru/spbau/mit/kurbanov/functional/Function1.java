package ru.spbau.mit.kurbanov.functional;

public interface Function1<T, RES> extends Function<T, RES> {

    default <R2> Function1<T, R2> compose(Function1<? super RES, R2> g) {
        return arg -> g.apply(apply(arg));
    }
}