/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.math.BigDecimal;

/**
 *
 * @author jan
 */
public class BoolValue extends CalcValue {
    
    public static final BoolValue TRUE = new BoolValue(true);
    public static final BoolValue FALSE = new BoolValue(false);
    
    
    private final boolean value;

    public boolean isValue() {
        return value;
    }

    
    public BoolValue(boolean value) {
        this.value = value;
    }
    
    
    @Override
    public String getRepr() {
        return value ? "true" : "false";
    }

    
    @Override
    public ICalcValue toSymbolValue() {
        return CalcValue.makeSymbol(getRepr());
    }

    @Override
    public ICalcValue toStringValue() {
        return CalcValue.make(getRepr());
    }
    
    @Override
    public IMonad<String> getStringValue() {
        return Monad.just(getRepr());
    }
    
    @Override
    public ICalcValue toFloat() {
        return CalcValue.make(value ? 1.0 : 0.0);
    }

    @Override
    public BigDecimal getDecimalValue() {
        return value ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Override
    public double getDoubleValue() {
        return value ? 1.0 : 0.0;
    }
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.just(value ? 1.0f : 0.0f);
    }
    
    @Override
    public boolean getBooleanValue() { return value; }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
