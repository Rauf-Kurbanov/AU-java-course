package ru.spbau.mit.kurbanov.functional;

public interface Predicate<T> extends Function<T, Boolean> {

    Predicate<Object> ALWAYS_TRUE = (arg -> true);
    Predicate<Object> ALWAYS_FALSE = (arg -> false);

    default Predicate<T> or(Predicate<? super T> other) {
        return arg -> apply(arg) || other.apply(arg);
    }

    default Predicate<T> and(Predicate<? super T> other) {
        return arg -> apply(arg) && other.apply(arg);
    }

    default Predicate<T> not() {
        return arg -> !apply(arg);
    }
}