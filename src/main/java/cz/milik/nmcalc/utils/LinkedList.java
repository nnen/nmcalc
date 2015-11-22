/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public abstract class LinkedList<TValue> implements Serializable, Iterable<TValue> {
    
    public abstract TValue getHead();
    public abstract LinkedList<TValue> getTail();
    
    public LinkedList<TValue> prepend(TValue value) {
        return new List(value, this);
    }
    
    public LinkedList<TValue> add(TValue value) {
        return prepend(value);
    }
    
    //public abstract void forEach(Consumer<TValue> consumer);
    
    public abstract <TResult> TResult reduceLeft(BiFunction<TResult, TValue, TResult> fn, TResult first);
    
    public <TResult> TResult reduceLeft(BiFunction<TResult, TValue, TResult> fn) {
        return reduceLeft(fn, null);
    }
    
    public abstract <TResult> TResult reduceRight(BiFunction<TValue, TResult, TResult> fn, TResult last);
    
    public abstract <T> Optional<T> mapFirst(Function<TValue, Optional<T>> fn);
    
    public abstract <TOut> LinkedList<TOut> map(Function<TValue, TOut> fn);
    
    public abstract boolean isEmpty();

    
    @Override
    public Iterator<TValue> iterator() {
        return new Iterator<TValue>() {
            private LinkedList<TValue> current = LinkedList.this;
            
            @Override
            public boolean hasNext() {
                return !current.isEmpty();
            }
            
            @Override
            public TValue next() {
                TValue result = current.getHead();
                current = current.getTail();
                return result;
            }
        };
    }
    
    
    public static <TValue> LinkedList<TValue> of(java.util.List<TValue> list) {
        LinkedList<TValue> result = empty();
        for (int i = list.size() - 1; i >= 0; i--) {
            result = result.prepend(list.get(i));
        }
        return result;
    }
    
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

        @Override
        public TValue getHead() { return head; }

        @Override
        public LinkedList<TValue> getTail() { return tail; }
        

        public List(TValue head, LinkedList<TValue> tail) {
            this.head = head;
            this.tail = tail;
        }

        /*
        @Override
        public void forEach(Consumer<TValue> consumer) {
            consumer.accept(head);
            tail.forEach(consumer);
        }
        */

        @Override
        public <TResult> TResult reduceLeft(BiFunction<TResult, TValue, TResult> fn, TResult first) {
            return tail.reduceLeft(fn, fn.apply(first, head));
        }

        @Override
        public <TResult> TResult reduceRight(BiFunction<TValue, TResult, TResult> fn, TResult last) {
            return fn.apply(head, tail.reduceRight(fn, last));
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
        public <TOut> LinkedList<TOut> map(Function<TValue, TOut> fn) {
            return new List(fn.apply(head), tail.map(fn));
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
    }
    
    public static class Empty<TValue> extends LinkedList<TValue> {

        @Override
        public  TValue getHead() { return null; }

        @Override
        public LinkedList<TValue> getTail() { return this; }
        
        /*
        @Override
        public void forEach(Consumer<TValue> consumer) {
        // Do nothing.
        }
         */

        @Override
        public <TResult> TResult reduceLeft(BiFunction<TResult, TValue, TResult> fn, TResult first) {
            return first;
        }
        
        @Override
        public <TResult> TResult reduceRight(BiFunction<TValue, TResult, TResult> fn, TResult last) {
            return last;
        }
        
        @Override
        public <T> Optional<T> mapFirst(Function<TValue, Optional<T>> fn) {
            return Optional.empty();
        }
        
        @Override
        public <TOut> LinkedList<TOut> map(Function<TValue, TOut> fn) {
            return new Empty();
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
    }
    
}
