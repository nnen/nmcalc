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
    
    public Object getAttribute(Object key);
    
    public IAttributeSet addAttribute(Object key, Object value);
    public IAttributeSet addAttributes(IAttributeSet attributes);
    
    public Iterable<Pair<Object, Object>> getAttributes();
    
}
