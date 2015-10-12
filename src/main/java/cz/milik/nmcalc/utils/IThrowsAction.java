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
 * @param <E>
 */
@FunctionalInterface
public interface IThrowsAction<T, E extends Throwable> {
    
    public void execute(T argument) throws E;
    
}
