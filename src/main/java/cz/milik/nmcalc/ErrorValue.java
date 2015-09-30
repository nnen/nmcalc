/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

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

    public ErrorValue() {
        this.message = "Unknown Error";
    }
    
    public ErrorValue(String message) {
        this.message = message;
    }
    
    
    public static ErrorValue formatted(String format, Object... args) {
        return new ErrorValue(String.format(format, args));
    }
    
    
    @Override
    public String toString() {
        return getMessage();
    }
    
    @Override
    public boolean isError() {
        return true;
    }
    
    @Override
    public ICalcValue toFloat() {
        return this;
    }
    
    @Override
    public ICalcValue divide(ICalcValue other) {
        return this;
    }

    @Override
    public ICalcValue multiply(ICalcValue other) {
        return this;
    }

    @Override
    public ICalcValue subtract(ICalcValue other) {
        return this;
    }

    @Override
    public ICalcValue add(ICalcValue other) {
        return this;
    }
    
    @Override
    public ICalcValue negate() {
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
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitError(this, context);
    }
    
}
