/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.values.ICalcValue;

/**
 *
 * @author jan
 */
public class SomeValue extends CalcValue {
    
    private final ICalcValue value;

    
    public SomeValue(ICalcValue value) {
        this.value = value;
    }

    
    @Override
    public boolean isSome() { return true; }

    
    @Override
    public ICalcValue unwrap(Context ctx) {
        return value;
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        return "some(" + value.getRepr(ctx) + ")";
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
