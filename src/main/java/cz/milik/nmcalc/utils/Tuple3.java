/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.utils;

import java.util.Objects;

/**
 *
 * @author jan
 */
public class Tuple3<T, U, V> {
    private final T first;
    private final U second;
    private final V third;

    public Tuple3(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    
    @Override
    public String toString() {
        return "Tuple3{" + first + ", " + second + ", " + third + '}';
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.first);
        hash = 47 * hash + Objects.hashCode(this.second);
        hash = 47 * hash + Objects.hashCode(this.third);
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
        final Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        if (!Objects.equals(this.third, other.third)) {
            return false;
        }
        return true;
    }
    
    
    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
    
    public V getThird() {
        return third;
    }
    
    
    public static <T, U, V> Tuple3<T, U, V> of(T first, U second, V third) {
        return new Tuple3(first, second, third);
    }
}
