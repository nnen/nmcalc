/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author jan
 */
public abstract class Monad {

    ////////////////////////////////////////////////////////////////////////////
    // Static methods
    
    public static <T> IMonad<T> nothing()
    {
        return new Nothing<>();
    }
    
    public static <T> IMonad<T> just(T value)
    {
        return new Just<>(value);
    }
    
    public static <T> IMonad<T> maybe(T value)
    {
        if (value == null)
        {
            return nothing();
        }
        return just(value);
    }
    
    public static <T> IMonad<T> nonNull(IMonad<T> monad) {
        if (monad == null) {
            return nothing();
        }
        return monad;
    }
    
    public static <T, R> IMonad<R> maybeBind(T value, Function<T, IMonad<R>> function) {
        return maybe(value).bind(function);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance Methods
    
    ////////////////////////////////////////////////////////////////////////////
    // Monad Implementations
    
    public static class Just<T> implements IMonad<T>
    {
        private final T value;
        
        public Just(T value)
        {
            this.value = value;
        }
        
        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public T unwrap(Supplier<? extends T> function) {
            return unwrap();
        }

        @Override
        public <U> U unwrap(Function<T, U> mapFn, Supplier<U> elseFn) {
            return mapFn.apply(value);
        }

        @Override
        public <U> U unwrap(Function<T, U> mapFn, U elseValue) {
            return mapFn.apply(value);
        }
        
        @Override
        public void bind(Consumer<T> function) {
            function.accept(value);
        }   

        @Override
        public <U> IMonad<U> bind(Function<T, IMonad<U>> function) {
            return function.apply(value);
        }
        
        @Override
        public <U, V> IMonad<V> bind(IMonad<U> other, BiFunction<T, U, IMonad<V>> function) {
            return other.bind(otherValue -> {
                return function.apply(value, otherValue);
            });
        }
        
        @Override
        public <U> IMonad<U> map(Function<? super T, ? extends U> mapFn) {
            return maybe(mapFn.apply(value));
        }
        
        @Override
        public IMonad<T> or(Supplier<IMonad<? extends T>> function) {
            return this;
        }

        @Override
        public IMonad<? extends T> orValue(Supplier<? extends T> function) {
            return this;
        }
        
        @Override
        public String toString() {
            return String.format("Just{%s}", ObjectUtils.toString(unwrap()));
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.value);
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Just<?> other = (Just<?>) obj;
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }
        
    }
    
    public static class Nothing<T> implements IMonad<T>
    {
        @Override
        public T unwrap() {
            return null;
        }
        
        @Override
        public T unwrap(Supplier<? extends T> function) {
            return function.get();
        }

        @Override
        public <U> U unwrap(Function<T, U> mapFn, Supplier<U> elseFn) {
            return elseFn.get();
        }

        @Override
        public <U> U unwrap(Function<T, U> mapFn, U elseValue) {
            return elseValue;
        }
        
        @Override
        public void bind(Consumer<T> function) {
            // Do nothing.
        }
        
        @Override
        public <U> IMonad<U> bind(Function<T, IMonad<U>> function) {
            return nothing();
        }
        
        @Override
        public <U, V> IMonad<V> bind(IMonad<U> other, BiFunction<T, U, IMonad<V>> function) {
            return Monad.nothing();
        }

        @Override
        public <U> IMonad<U> map(Function<? super T, ? extends U> mapFn) {
            return nothing();
        }
        
        @Override
        public IMonad<? extends T> or(Supplier<IMonad<? extends T>> function) {
            return function.get();
        }
        
        @Override
        public IMonad<? extends T> orValue(Supplier<? extends T> function) {
            return maybe(function.get());
        }
        
        @Override
        public String toString() {
            return "Nothing";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            return (this == obj) || (obj instanceof Nothing<?>);
        }
        
        @Override
        public int hashCode() {
            return 131071; // All nothing monads are equal and have the same hash.
        }
        
    }
    
}
