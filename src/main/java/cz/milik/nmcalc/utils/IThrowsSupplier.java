/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

/**
 *
 * @author jan
 */
@FunctionalInterface
public interface IThrowsSupplier<T, E extends Throwable> {
   
    public T supply() throws E;
    
}
