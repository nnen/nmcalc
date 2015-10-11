/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

/**
 *
 * @author jan
 */
public class ReprCtxValue extends ProxyValue {
    
    private final ReprContext reprContext;

    public ReprCtxValue(ICalcValue target, ReprContext ctx) {
        super(target);
        reprContext = ctx;
    }
    
    @Override
    public String getExprRepr(ReprContext ctx) {
        return super.getExprRepr(reprContext);
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        return super.getRepr(reprContext);
    }
    
}
