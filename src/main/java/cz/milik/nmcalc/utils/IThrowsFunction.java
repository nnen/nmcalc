/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

/**
 *
 * @author jan
 * @param <T>
 * @param <R>
 * @param <E>
 */
@FunctionalInterface
public interface IThrowsFunction<T, R, E extends Throwable> {
    
    public R apply(T argument) throws E;
    
}
