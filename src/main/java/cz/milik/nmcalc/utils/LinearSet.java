/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jan
 */
public class LinearSet<T> extends AbstractSet<T> {
    
    private List<T> list;
    
    @Override
    public Iterator<T> iterator() {
        if (list == null) {
            return Collections.emptyIterator();
        }
        return list.iterator();
    }
    
    @Override
    public int size() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    
    @Override
    public boolean add(T e) {
        if (list == null) {
            list = new ArrayList();
        }
        if (list.contains(e)) {
            return false;
        }
        return list.add(e);
    }
    
    public LinearSet() {
    }
    
    public LinearSet(Collection<T> items) {
        list = new ArrayList(items);
    }
    
}
