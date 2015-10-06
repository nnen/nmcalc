/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.BuiltinCalcValue.QuoteValue;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import cz.milik.nmcalc.utils.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public abstract class CalcValue implements ICalcValue {

    public static ICalcValue make(float value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue make(double value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue make(String value) {
        return new StringValue(value);
    }
    
    public static ICalcValue make(boolean value) {
        if (value) {
            return BoolValue.TRUE;
        }
        return BoolValue.FALSE;
    }
    
    public static ICalcValue makeSymbol(String name) {
        return new SymbolValue(name);
    }
    
    public static ICalcValue list(ICalcValue... items) {
        return new ListValue(items);
    }
    
    public static ICalcValue list(Collection<? extends ICalcValue> items) {
        return new ListValue(items);
    }
    
    public static ICalcValue list(ICalcValue head, Collection<? extends ICalcValue> tail) {
        return new ListValue(head, tail);
    }

    public static ICalcValue quote(ICalcValue value) {
        return new QuoteValue(value);
    }
    
    
    public static boolean areValuesEqual(ICalcValue a, ICalcValue b) {
        if (a.isError()) {
            return false;
        }
        if (b.isError()) {
            return false;
        }
        return a.isValueEqual(b);
    }
    
    public static ICalcValue binaryOp(ICalcValue a, ICalcValue b, BiFunction<ICalcValue, ICalcValue, ICalcValue> fn) {
        if (a.isError()) { 
            return a;
        }
        if (b.isError()) {
            return b;
        }
        return fn.apply(a, b);
    }
    
    
    @Override
    public String getRepr() {
        return getClass().getSimpleName();
    }
    
    @Override
    public String getExprRepr() {
        return getRepr();
    }

    @Override
    public String getApplyRepr(List<? extends ICalcValue> arguments) {
        return getExprRepr() + "(" + StringUtils.join(", ", arguments.stream().map(arg -> arg.getExprRepr())) + ")";
    }
    
    
    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isSpecialForm() {
        return false;
    }
    
    
    @Override
    public Context getAttribute(String attrName, Context ctx) {
        ctx.setReturnedValue(ErrorValue.formatted(ctx, "%s doesn't have attribute '%s'.", getRepr(), attrName));
        return ctx;
    }
    
    @Override
    public Context setAttribute(String attrName, ICalcValue value, Context ctx) {
        ctx.setReturnedValue(ErrorValue.formatted(ctx, "Cannot assign attribute '%s' to %s.", attrName, getRepr()));
        return ctx;
    }
    
    
    @Override
    public boolean getBooleanValue() { return true; }
    
    
    @Override
    public ICalcValue toFloat() {
        return ErrorValue.formatted("Cannot convert %s to float.", getRepr());
    }
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.nothing();
    }

    @Override
    public double getDoubleValue() {
        return 0.0;
    }
    
    @Override
    public BigDecimal getDecimalValue() {
        return BigDecimal.ZERO;
    }
    
    
    @Override
    public ICalcValue toStringValue() {
        return new StringValue(getRepr());
    }
    
    @Override
    public IMonad<String> getStringValue() {
        return Monad.just(getRepr());
    }

    
    @Override
    public ICalcValue toSymbolValue() {
        return ErrorValue.formatted("%s cannot be converted to a symbol.", getRepr());
    }
    
    
    @Override
    public boolean isValueEqual(ICalcValue other) {
        return Objects.equals(this, other);
    }
    
    @Override
    public int compareValue(ICalcValue other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    @Override
    public ICalcValue negate() {
        return new ErrorValue();
    }
    
    @Override
    public ICalcValue add(ICalcValue other) {
        if (other.isError()) {
            return other;
        }
        return ErrorValue.formatted("%s cannot be added to %s.", getRepr(), other.getRepr());
    }
    
    @Override
    public ICalcValue subtract(ICalcValue other) {
        return new ErrorValue();
    }
    
    @Override
    public ICalcValue multiply(ICalcValue other) {
        return new ErrorValue();
    }
    
    @Override
    public ICalcValue divide(ICalcValue other) {
        return new ErrorValue();
    }
    
    
    @Override
    public boolean hasLength() {
        return false;
    }

    @Override
    public int length() {
        return 1;
    }
    
    @Override
    public ICalcValue getItem(int index) {
        return new ErrorValue(String.format(
                "%s value doesn't support indexing.",
                getClass().getSimpleName(),
                index
        ));
    }
    
    
    @Override
    public Context eval(Context ctx) {
        ctx.setReturnedValue(this);
        return ctx;
    }
    
    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        try {
            return applyInner(ctx, arguments);
        } catch (NMCalcException e) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    e.getContext(),
                    "%s: %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            return ctx;
        }
    }
    
    protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        ctx.setReturnedValue(ErrorValue.formatted("%s %s cannot be applied.", getClass().getSimpleName(), getRepr()));
        return ctx;
    }
    
    @Override
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments) {
        try {
            return applySpecialInner(ctx, arguments);
        } catch (NMCalcException e) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    e.getContext(),
                    "%s: %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            return ctx;
        }
    }
    
    protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        return applyInner(ctx, arguments);
    }
    
    
    @Override
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function) {
        return function.apply(this);
    }
    
    @Override
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function) {
        return other.withNonError(otherNonError -> {
           return function.apply(this, otherNonError);
        });
    }
 
    
    protected void invalidArgumentCount(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        throw new NMCalcException(String.format("Invalid argument count: %d.", arguments.size()), ctx);
    }
    
    protected boolean checkArguments(Context ctx, List<? extends ICalcValue> arguments, int expectedCount) {
        if (arguments.size() != expectedCount) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    "%s cannot be applied to %d argument(s). Exactly %d argument(s) are expected.",
                    getRepr(), arguments.size(), expectedCount));
            return false;
        }
        return true;
    }
    
    public static SymbolValue asSymbol(ICalcValue value) throws NMCalcException {
        if (value == null) {
            throw new NMCalcException("Expected a symbol, got null.");
        }
        
        if (value instanceof SymbolValue) {
            return (SymbolValue)value;
        }
        
        throw new NMCalcException(String.format("Expected a symbol, got: %s.", value.getRepr()));
    }
    
    public static List<? extends SymbolValue> asSymbolList(ICalcValue value) throws NMCalcException {
        if (value == null) {
            throw new NMCalcException("Expected a list of symbols, got null.");
        }
        
        if (value instanceof ListValue) {
            ListValue listValue = (ListValue)value;
            List<SymbolValue> result = new ArrayList();
            for (ICalcValue item : listValue.getValues()) {
                result.add(asSymbol(item));
            }
            return result;
        }
        
        throw new NMCalcException(String.format("Expected a list of symbols, got: %s.", value.getRepr()));
    }
}
