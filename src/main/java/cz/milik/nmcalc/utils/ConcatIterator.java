/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author jan
 */
public class ConcatIterator<T> implements Iterator<T> {

    private final Iterator<Iterator<T>> iterators;
    private Iterator<T> currentIterator;
    
    public ConcatIterator(Iterator<Iterator<T>> iterators)
    {
        this.iterators = iterators;
    }
    
    public ConcatIterator(Iterable<Iterator<T>> iterators)
    {
        this.iterators = iterators.iterator();
    }
    
    public ConcatIterator(Iterator<T>... iterators)
    {
        this.iterators = Arrays.asList(iterators).iterator();
    }
    
    private Iterator<T> getCurrent()
    {
        if (currentIterator == null)
        {
            if (!iterators.hasNext())
            {
                return null;
            }
            currentIterator = iterators.next();
        }
        return currentIterator;
    }
    
    private Iterator<T> getNext()
    {
        if (iterators.hasNext())
        {
            currentIterator = iterators.next();
        }
        else
        {
            currentIterator = null;
        }
        return currentIterator;
    }
    
    @Override
    public boolean hasNext() {
        while (true)
        {
            Iterator<T> current = getCurrent();
            if (current == null)
            {
                break;
            }
            if (current.hasNext())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() {
        while (true)
        {
            Iterator<T> current = getCurrent();
            if (current == null)
            {
                break;
            }
            if (current.hasNext())
            {
                return current.next();
            }
            getNext();
        }
        return null;
    }
    
}
