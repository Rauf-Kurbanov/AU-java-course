package ru.spbau.mit.kurbanov.functional;

public interface Function<T1, R> {
    
    R apply(T1 arg);
}