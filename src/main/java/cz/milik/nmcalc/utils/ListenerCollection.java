/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author jan
 */
public class ListenerCollection<T> extends AbstractCollection<T> {

    private final List<T> listeners = new ArrayList<>();
    private final T[] arrayDummy;

    public ListenerCollection(T[] arrayDummy) {
        this.arrayDummy = arrayDummy;
    }
    
    
    @Override
    public Iterator<T> iterator() {
        return listeners.iterator();
    }
    
    @Override
    public int size() {
        return listeners.size();
    }

    @Override
    public boolean add(T e) {
        if (listeners.contains(e)) {
            return false;
        }
        return listeners.add(e);
    }
    
    
    public void handleEvent(Consumer<T> fn) {
        T[] array = listeners.toArray(arrayDummy);
        for (T listener : array) {
            fn.accept(listener);
        }
    }
    
}
