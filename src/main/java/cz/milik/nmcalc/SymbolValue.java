/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import java.util.Objects;

/**
 *
 * @author jan
 */
public class SymbolValue extends StringValue {

    public SymbolValue(String value) {
        super(value);
    }

    
    @Override
    public boolean isSymbol() {
        return true;
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        if (isIdent()) {
            return "'" + getValue();
        }
        return "'$" + getValue();
    }

    @Override
    public String getExprRepr(ReprContext ctx) {
        if (isIdent()) {
            return getValue();
        }
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
    
    @Override
    protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
        if (Objects.equals(getValue(), "_")) {
            ctx.setReturnedValue(CalcValue.some(CalcValue.list()));
            return ctx;
        }
        ICalcValue varValue = ctx.getVariable(getValue()).unwrap();
        if (varValue == null) {
            ctx.setVariable(getValue(), value);
            ctx.setReturnedValue(CalcValue.some(CalcValue.list()));
            return ctx;
        }
        return varValue.unapply(ctx, value);
    }
    
    
    public boolean isIdent() {
        if (isKeyword()) {
            return false;
        }
        return getValue().codePoints().allMatch(c -> Character.isJavaIdentifierPart(c));
    }
    
}
