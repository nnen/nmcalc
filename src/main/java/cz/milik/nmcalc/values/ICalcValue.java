/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueAnnotation;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.SerializationContext;
import cz.milik.nmcalc.TextLoc;
import cz.milik.nmcalc.text.IPrintable;
import cz.milik.nmcalc.text.TextWriter;
import cz.milik.nmcalc.utils.LinkedList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author jan
 */
public interface ICalcValue extends java.io.Serializable, IPrintable {
    
    public String getRepr(ReprContext ctx);
    public String getExprRepr(ReprContext ctx);
    public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx);
    public void printApplyExpr(TextWriter out, List<? extends ICalcValue> arguments, ReprContext ctx);
    public void printDebug(TextWriter out, ReprContext ctx);
    
    public Optional<TextLoc> getTextLoc();
    public void setTextLoc(TextLoc loc);
    
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
    
    public Context bitwiseAnd(ICalcValue other, Context ctx);
    public Context bitwiseOr(ICalcValue other, Context ctx);
    public Context bitwiseXor(ICalcValue other, Context ctx);
    
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
    
    public Context substitute(Context ctx, ICalcValue value, ICalcValue replacement);
    
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function);
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function);
    
    public Optional<UUID> getId();
    
    public boolean serialize(SerializationContext ctx) throws NMCalcException;
    public Context serialize(Context ctx, SerializationContext serCtx);
    
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context);
    
}

