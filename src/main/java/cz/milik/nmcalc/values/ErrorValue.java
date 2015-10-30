/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.ReprContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public class ErrorValue extends CalcValue {
    
    private final String message;
    
    public String getMessage() {
        return message;
    }
    
    
    private final Context origin;
    
    public Context getOrigin() { return origin; }
    
    
    private final Exception cause;
    
    public Exception getCause() { return cause; }
    
    
    public ErrorValue() {
        this.message = "Unknown Error";
        this.origin = null;
        this.cause = null;
    }
    
    public ErrorValue(String message) {
        this.message = message;
        this.origin = null;
        this.cause = null;
    }
    
    public ErrorValue(String message, Context origin) {
        this.message = message;
        this.origin = origin;
        this.cause = null;
    }
    
    public ErrorValue(String message, Exception cause) {
        this.message = message;
        this.origin = null;
        this.cause = cause;
    }

    public ErrorValue(String message, Context origin, Exception cause) {
        this.message = message;
        this.origin = origin;
        this.cause = cause;
    }
    
    
    public static ErrorValue formatted(String format, Object... args) {
        return new ErrorValue(String.format(format, args));
    }
    
    public static ErrorValue formatted(Context origin, String format, Object... args) {
        return new ErrorValue(String.format(format, args), origin);
    }
    
    
    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public String getRepr(ReprContext ctx) {
        if (getCause() != null) {
            return String.format("error(\"%s\", %s)", getMessage(), getCause().getClass().getSimpleName());
        }
        return "error(\"" + getMessage() + "\")";
    }
    
    @Override
    public boolean isError() {
        return true;
    }

    
    @Override
    public boolean getBooleanValue() {
        return false;
    }
    
    
    @Override
    public ICalcValue toFloat(Context ctx) {
        return this;
    }
    
    @Override
    public ICalcValue divide(ICalcValue other, Context ctx) {
        return this;
    }

    @Override
    public ICalcValue multiply(ICalcValue other, Context ctx) {
        return this;
    }

    @Override
    public ICalcValue subtract(ICalcValue other, Context ctx) {
        return this;
    }

    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        return this;
    }
    
    @Override
    public ICalcValue negate(Context ctx) {
        return this;
    }
    
    @Override
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function) {
        return this;
    }
    
    @Override
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function) {
        return this;
    }
    
    
    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        ctx.setReturnedValue(this);
        return ctx;
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitError(this, context);
    }
    
}

