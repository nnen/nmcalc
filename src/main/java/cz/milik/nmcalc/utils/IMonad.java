/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jan
 * @param <T>
 */
public interface IMonad<T> {
    
    public T unwrap();
    public T unwrap(Supplier<? extends T> function);
    public <U> U unwrap(Function<T, U> mapFn, Supplier<U> elseFn);
    public <U> U unwrap(Function<T, U> mapFn, U elseValue);
    
    public void bind(Consumer<T> function);
    public <U> IMonad<U> bind(Function<T, IMonad<U>> function);
    public <U, V> IMonad<V> bind(IMonad<U> other, BiFunction<T, U, IMonad<V>> function);
    
    public <U> IMonad<U> map(Function<? super T, ? extends U> mapFn);
    
    public IMonad<? extends T> or(Supplier<IMonad<? extends T>> function);
    public IMonad<? extends T> orValue(Supplier<? extends T> function);
    
}
