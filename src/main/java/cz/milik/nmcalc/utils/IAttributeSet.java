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
public interface IAttributeSet {
    
    //public Object getAttribute(Object key);
    
    public <T> T getAttribute(Class<T> cls);
    public <T> void setAttribute(Class<T> cls, T value);
    public void setAttribute(Object value);
    
}
