/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public interface ICalcValue {
    
    public String getRepr();
    public String getExprRepr();
    public String getApplyRepr(List<? extends ICalcValue> arguments);
    
    public boolean isError();
    public boolean isSpecialForm();
    
    public boolean getBooleanValue();
    
    public ICalcValue toFloat();
    public IMonad<Float> getFloatValue();
    public double getDoubleValue();
    
    public ICalcValue toStringValue();
    public IMonad<String> getStringValue();
    
    public ICalcValue toSymbolValue();
    
    public boolean isValueEqual(ICalcValue other);
    
    public ICalcValue negate();
    public ICalcValue add(ICalcValue other);
    public ICalcValue subtract(ICalcValue other);
    public ICalcValue multiply(ICalcValue other);
    public ICalcValue divide(ICalcValue other);
    
    public boolean hasLength();
    public int length();
    public ICalcValue getItem(int index);
    
    public Context eval(Context ctx);
    public Context apply(Context ctx, List<? extends ICalcValue> arguments);
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments);
    
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function);
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function);
    
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context);
    
}
