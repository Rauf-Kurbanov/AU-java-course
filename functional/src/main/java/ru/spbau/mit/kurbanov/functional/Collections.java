package ru.spbau.mit.kurbanov.functional;

import java.util.ArrayList;
import java.util.List;

public class Collections {
    public static <T, R> List<R> map(Function1<? super T, R> f, Iterable<T> cont) {
        List<R> result = new ArrayList<>();
        for (T e : cont) {
            result.add(f.apply(e));
        }
        return result;
    }

    public static <T> List<T> filter(Predicate<? super T> pred, Iterable<T> cont){
        List<T> result = new ArrayList<>();
        for (T e : cont) {
            if (pred.apply(e)) {
                result.add(e);
            }
        }
        return result;
    }

    public static <T> List<T> takeWhile(Predicate<? super T> pred, Iterable<T> cont) {
        List<T> result = new ArrayList<>();
        for (T e : cont) {
            if (!pred.apply(e)){
                break;
            }
            result.add(e);
        }
        return result;
    }

    public static <T> List<T> takeUnless(Predicate<? super T> pred, Iterable<T> cont) {
        return takeWhile(pred.not(), cont);
    }

    public static <T, R> R foldl(Function2<R, ? super T, R> foldFunction, R startValue, Iterable<T> cont) {
        R result = startValue;
        for(T e : cont) {
            result = foldFunction.apply(result, e);
        }
        return result;
    }

    public static <T, R> R foldr(Function2<? super T, R, R> function, R ini, Iterable<T> cont) {
        ArrayList<T> tmp = new ArrayList<>();
        for (T t : cont) {
            tmp.add(t);
        }
        R result = ini;
        for (int i = tmp.size() - 1; i >= 0; --i) {
            result = function.apply(tmp.get(i), result);
        }
        return result;
    }
}
