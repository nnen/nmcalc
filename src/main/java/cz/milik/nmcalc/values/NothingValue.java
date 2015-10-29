/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.ReprContext;

/**
 *
 * @author jan
 */
public class NothingValue extends CalcValue {

    public static final NothingValue INSTANCE = new NothingValue();
    
    @Override
    public boolean isNothing() {
        return true;
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        return "nothing";
    }
    
    @Override
    public String getExprRepr(ReprContext ctx) {
        return getRepr(ctx);
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
