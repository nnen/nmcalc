/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;

/**
 *
 * @author jan
 */
public class FloatValue extends CalcValue {
    
    private float value;
    
    public FloatValue(float value)
    {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return Float.toString(value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Float.floatToIntBits(this.value);
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
        final FloatValue other = (FloatValue) obj;
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String getRepr() {
        return Float.toString(value);
    }
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.just(value);
    }
    
    @Override
    public ICalcValue toFloat() {
        return this;
    }

    @Override
    public ICalcValue negate() {
        return new FloatValue(-value);
    }
    
    @Override
    public ICalcValue add(ICalcValue other) {
        return other.withNonError(otherSafe -> {
           return new FloatValue(value + other.getFloatValue().unwrap()) ;
        });
    }
    
    @Override
    public ICalcValue subtract(ICalcValue other) {
        return other.withNonError(otherSafe -> {
           return new FloatValue(value - other.getFloatValue().unwrap());
        });
    }
    
    @Override
    public ICalcValue divide(ICalcValue other) {
        return other.withNonError(otherSafe -> {
           return new FloatValue(value / other.getFloatValue().unwrap());
        });
    }
    
    @Override
    public ICalcValue multiply(ICalcValue other) {
        return other.withNonError(otherSafe -> {
           return new FloatValue(value * other.getFloatValue().unwrap());
        });
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFloat(this, context);
    }
    
}
