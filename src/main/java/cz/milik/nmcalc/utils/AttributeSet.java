/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author jan
 */
public abstract class AttributeSet implements IAttributeSet {

    private Map<Object, Object> values = new HashMap();
    
    @Override
    public Object getAttribute(Object key) {
        return values.get(key);
    }

    @Override
    public IAttributeSet addAttribute(Object key, Object value) {
        values.put(key, value);
        return this;
    }

    @Override
    public IAttributeSet addAttributes(IAttributeSet attributes) {
        for (Pair<Object, Object> attr : attributes.getAttributes()) {
            addAttribute(attr.getFirst(), attr.getSecond());
        }
        return this;
    }
    
    @Override
    public Iterable<Pair<Object, Object>> getAttributes() {
        return new Iterable<Pair<Object, Object>>() {
            @Override
            public Iterator<Pair<Object, Object>> iterator() {
                return values.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).iterator();
            }
        };
    }
    
}
