package ru.spbau.mit.kurbanov.functional;

public interface Function1<T, R> {

    R apply(T arg);

    default <R2> Function1<T, R2> compose(Function1<? super R, R2> g) {
        return arg -> g.apply(apply(arg));
    }
}