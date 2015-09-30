/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.Objects;

/**
 *
 * @author jan
 */
public abstract class PrimitiveValueBase<T> extends CalcValue {
    
    private final T value;
    
    public T getValue() { return value; }

    
    public PrimitiveValueBase(T value) {
        this.value = value;
    }
    
    
    @Override
    public String toString() {
        return String.format(
                "%s{value=%s}",
                getClass().getSimpleName(),
                Objects.toString(value)
        );
    }

    
    @Override
    public String getRepr() {
        return Objects.toString(value);
    }
    
}
