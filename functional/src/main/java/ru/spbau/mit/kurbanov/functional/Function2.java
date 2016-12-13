package ru.spbau.mit.kurbanov.functional;

public interface Function2<T1, T2, R> {

    R apply(T1 arg1, T2 arg2);

    default<R2> Function2<T1, T2, R2> compose(Function1<? super R, R2> g) {
        return (arg1, arg2) -> g.apply(apply(arg1, arg2));
    }

    default Function1<T2, R> bind1(T1 arg1) {
        return arg2 -> apply(arg1, arg2);
    }

    default Function1<T1, R> bind2(T2 arg2) {
        return arg1 -> apply(arg1, arg2);
    }

    default Function1<T1, Function1<T2, R>> curry() {
        return arg1 -> arg2 -> apply(arg1, arg2);
    }
}
