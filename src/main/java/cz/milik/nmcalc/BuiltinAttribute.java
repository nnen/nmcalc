/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.values.SymbolValue;

/**
 *
 * @author jan
 */
public abstract class BuiltinAttribute<T extends ICalcValue> {
 
    public Context get(T obj, SymbolValue name, Context ctx) {
        ctx.setReturnedValue(getInner(obj, name, ctx));
        return ctx;
    }
    
    public Context set(T obj, SymbolValue name, ICalcValue value, Context ctx) {
        ctx.setReturnedValue(setInner(obj, name, value, ctx));
        return ctx;
    }
    
    
    protected abstract ICalcValue getInner(T obj, SymbolValue name, Context ctx);
    
    protected ICalcValue setInner(T obj, SymbolValue name, ICalcValue value, Context ctx) {
        return CalcValue.error(
                ctx,
                "Attribute %s of %s is read-only.",
                name.getRepr(ctx.getReprContext()),
                obj.getRepr(ctx.getReprContext())
        );
    }
    
}
