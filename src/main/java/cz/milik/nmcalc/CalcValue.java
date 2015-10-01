/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.BuiltinCalcValue.QuoteValue;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.util.Collection;
import java.util.List;
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
    
    public static ICalcValue make(String value) {
        return new StringValue(value);
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
    
    
    @Override
    public String getRepr() {
        return getClass().getSimpleName();
    }
    
    
    @Override
    public boolean isError() {
        return false;
    }
    
    @Override
    public ICalcValue toFloat() {
        return new ErrorValue();
    }
    
    @Override
    public IMonad<Float> getFloatValue() {
        return Monad.nothing();
    }

    
    @Override
    public ICalcValue toStringValue() {
        return new StringValue(toString());
    }

    @Override
    public IMonad<String> getStringValue() {
        return Monad.just(toString());
    }
    
    
    @Override
    public ICalcValue negate() {
        return new ErrorValue();
    }
    
    @Override
    public ICalcValue add(ICalcValue other) {
        return new ErrorValue();
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
        ctx.setReturnedValue(ErrorValue.formatted("%s %s cannot be applied.", getClass().getSimpleName(), getRepr()));
        return ctx;
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
 
    
    protected boolean checkArguments(Context ctx, List<? extends ICalcValue> arguments, int expectedCount) {
        if (arguments.size() != expectedCount) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    "%s cannot be applied to %d argument(s). Exactly %d argument(s) are expected.",
                    getRepr(), arguments.size(), expectedCount));
            return false;
        }
        return true;
    }
}
