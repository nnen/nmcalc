/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public abstract class LinkedList<TValue> {
    
    public LinkedList<TValue> add(TValue value) {
        return new List(value, this);
    }
    
    public abstract void forEach(Consumer<TValue> consumer);
    
    public abstract <T> Optional<T> mapFirst(Function<TValue, Optional<T>> fn);
    
    public abstract boolean isEmpty();
    
    
    public static <TValue> LinkedList<TValue> of(TValue first, TValue... rest) {
        LinkedList<TValue> result = new Empty();
        
        for (int i = rest.length - 1; i >= 0; i--) {
            result = result.add(rest[i]);
        }
        
        result = result.add(first);
        
        return result;
    }
    
    public static <TValue> LinkedList<TValue> empty() {
        return new Empty();
    }
    
    
    public static class List<TValue> extends LinkedList<TValue> {
        
        private final TValue head;
        private final LinkedList<TValue> tail;

        public List(TValue head, LinkedList<TValue> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public void forEach(Consumer<TValue> consumer) {
            consumer.accept(head);
            tail.forEach(consumer);
        }

        @Override
        public <T> Optional<T> mapFirst(Function<TValue, Optional<T>> fn) {
            Optional<T> result = fn.apply(head);
            if (result.isPresent()) {
                return result;
            }
            return tail.mapFirst(fn);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
    }
    
    public static class Empty<TValue> extends LinkedList<TValue> {

        @Override
        public void forEach(Consumer<TValue> consumer) {
            // Do nothing.
        }

        @Override
        public <T> Optional<T> mapFirst(Function<TValue, Optional<T>> fn) {
            return Optional.empty();
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
    }
    
}
