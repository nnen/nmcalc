/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {
    
    private final T first;
    private final U second;
    
    public T getFirst() { return first; }
    public U getSecond() { return second; }
    
    public Pair(T aFirst, U aSecond) {
        first = aFirst;
        second = aSecond;
    }
    
    public static <V, W> Pair<V, W> of(V first, W second) {
        return new Pair(first, second);
    }
    
    public <V, W> Pair<V, W> map(BiFunction<T, U, Pair<V, W>> fn) {
        return fn.apply(first, second);
    }
    
    public <V> Pair<V, U> mapFirst(Function<T, V> fn) {
        return new Pair(fn.apply(first), second);
    }
    
    public <V> Pair<T, V> mapSecond(Function<U, V> fn) {
        return new Pair(first, fn.apply(second));
    }
    
}
