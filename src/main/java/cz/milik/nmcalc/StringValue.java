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
public class StringValue extends PrimitiveValueBase<String> {

    public StringValue(String value) {
        super(value);
    }

    
    @Override
    public String getRepr() {
        return "\"" + getValue() + "\"";
    }

    
    @Override
    public boolean getBooleanValue() {
        return !getValue().isEmpty();
    }
    
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.just(Float.parseFloat(getValue()));
    }

    @Override
    public ICalcValue toFloat() {
        return new FloatValue(Float.parseFloat(getValue()));
    }
    
    
    @Override
    public IMonad<String> getStringValue() {
        return Monad.just(getValue());
    }
    
    @Override
    public ICalcValue toStringValue() {
        return this;
    }
    
    
    @Override
    public ICalcValue getItem(int index) {
        if ((index < 0) || (index >= getValue().length())) {
            return new ErrorValue();
        }
        return new StringValue(Character.toString(getValue().charAt(index)));
    }
    
    @Override
    public int length() {
        return getValue().length();
    }
    
    @Override
    public boolean hasLength() {
        return true;
    }
    
    
    @Override
    public ICalcValue add(ICalcValue other) {
        if (other.isError()) {
            return other;
        }
        
        return other.getStringValue().bind(otherStr -> {
            return Monad.<ICalcValue>just(
                    new StringValue(getValue() + otherStr)
            );
        }).unwrap(() -> { return new ErrorValue(); });
    }

    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitString(this, context);
    }
    
}
