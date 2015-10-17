/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.LinkedList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public interface ICalcValue extends java.io.Serializable {
    
    public String getRepr(ReprContext ctx);
    public String getExprRepr(ReprContext ctx);
    public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx);
    
    public boolean isError();
    public boolean isSpecialForm();
    public boolean isSymbol();
    public boolean isList();
    public boolean isSome();
    public boolean isNothing();
    public boolean isObject();
    
    public LinkedList<ICalcValueAnnotation> getAnnotations();
    public ICalcValue addAnnotation(ICalcValueAnnotation value);
    public <T extends ICalcValueAnnotation> Optional<T> getAnnotation(Class<T> cls);
    
    public Optional<String> getHelp();
    public void setHelp(String help);
    public boolean isHelp();
    
    public ICalcValue unwrap(Context ctx);
    
    public Context getAttribute(SymbolValue attrName, Context ctx);
    public Context setAttribute(SymbolValue attrName, ICalcValue value, Context ctx);
    
    public boolean getBooleanValue();
    
    public ICalcValue toFloat(Context ctx);
    public BigDecimal getDecimalValue();
    
    public ICalcValue toStringValue(Context ctx);
    public String getStringValue(Context ctx);
    
    public ICalcValue toSymbolValue(Context ctx);
    
    public boolean isValueEqual(ICalcValue other, Context ctx);
    public int compareValue(ICalcValue other, Context ctx);
    //public boolean isValueGreater(ICalcValue other);
    
    public ICalcValue negate(Context ctx);
    public ICalcValue add(ICalcValue other, Context ctx);
    public ICalcValue subtract(ICalcValue other, Context ctx);
    public ICalcValue multiply(ICalcValue other, Context ctx);
    public ICalcValue divide(ICalcValue other, Context ctx);
    
    public boolean hasLength();
    public int length();
    public ICalcValue getItem(int index);
    public Context getItem(Context ctx, ICalcValue index);
    public Context setItem(Context ctx, ICalcValue index, ICalcValue value);
    public void setItem(ICalcValue index, ICalcValue value) throws NMCalcException;
    public Context getHead(Context ctx);
    public Context getTail(Context ctx);
    public Context unpack(Context ctx);
    
    public Context eval(Context ctx);
    public Context apply(Context ctx, List<? extends ICalcValue> arguments);
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments);
    public Context unapply(Context ctx, ICalcValue value);
    
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function);
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function);
    
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context);
    
}
