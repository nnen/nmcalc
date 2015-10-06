/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.math.BigDecimal;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public class FloatValue extends CalcValue {
    
    private final BigDecimal value;
    
    
    public FloatValue(float value)
    {
        this.value = BigDecimal.valueOf((double)value);
    }
    
    public FloatValue(double value)
    {
        this.value = BigDecimal.valueOf(value);
    }
    
    public FloatValue(BigDecimal value)
    {
        this.value = value;
    }
    
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + value.hashCode();
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
        return value.compareTo(other.value) == 0;
    }

    @Override
    public String getRepr() {
        return value.toString();
    }
    
    @Override
    public boolean getBooleanValue() {
        return value.compareTo(BigDecimal.ZERO) != 0;
    }
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.just(value.floatValue());
    }
    
    @Override
    public double getDoubleValue() {
        return value.doubleValue();
    }

    @Override
    public BigDecimal getDecimalValue() {
        return value;
    }
    
    
    @Override
    public ICalcValue toFloat() {
        return this;
    }

    
    @Override
    public boolean isValueEqual(ICalcValue other) {
        ICalcValue otherFloat = other.toFloat();
        if (otherFloat.isError()) {
            return false;
        }
        return value.compareTo(otherFloat.getDecimalValue()) == 0;
    }
    
    @Override
    public int compareValue(ICalcValue other) {
        ICalcValue otherFloat = other.toFloat();
        return value.compareTo(otherFloat.getDecimalValue());
    }
    
    
    @Override
    public ICalcValue negate() {
        return new FloatValue(value.negate());
    }
    
    protected ICalcValue binaryOp(ICalcValue other, Function<BigDecimal, BigDecimal> fn) {
        ICalcValue casted = other.toFloat();
        if (casted.isError()) {
            return casted;
        }
        return new FloatValue(fn.apply(casted.getDecimalValue()));
    }
    
    @Override
    public ICalcValue add(ICalcValue other) {
        return binaryOp(other, decimal -> value.add(decimal));
    }
    
    @Override
    public ICalcValue subtract(ICalcValue other) {
        return binaryOp(other, decimal -> value.subtract(decimal));
    }
    
    @Override
    public ICalcValue divide(ICalcValue other) {
        return binaryOp(other, decimal -> value.divide(decimal));
    }
    
    @Override
    public ICalcValue multiply(ICalcValue other) {
        return binaryOp(other, decimal -> value.multiply(decimal));
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitFloat(this, context);
    }
    
}
