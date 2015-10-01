/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;

/**
 *
 * @author jan
 */
public class SymbolValue extends StringValue {

    public SymbolValue(String value) {
        super(value);
    }
    
    @Override
    public String getRepr() {
        return "$" + getValue();
    }
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitSymbol(this, context);
    }
    
    @Override
    public Context eval(Context ctx) {
        IMonad<ICalcValue> value = ctx.getVariable(getValue());
        value.orValue(
                () -> ErrorValue.formatted("Undefined variable: %s.", getValue())
        ).bind(val -> {
            ctx.setReturnedValue(val);
        });
        return ctx;
    }
    
}
