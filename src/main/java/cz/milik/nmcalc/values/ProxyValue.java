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
import cz.milik.nmcalc.values.ICalcValue;
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
public abstract class ProxyValue implements ICalcValue {
    
    private ICalcValue target;
    
    /**
     * @return the target
     */
    public ICalcValue getTarget() {
        return target;
    }
    
    public void setTarget(ICalcValue target) {
        this.target = target;
    }
    
    
    public ProxyValue(ICalcValue target) {
        this.target = target;
    }
    
    public ProxyValue() {
        this.target = CalcValue.nothing();
    }

    @Override
    public Context bitwiseAnd(ICalcValue other, Context ctx) {
        return target.bitwiseAnd(other, ctx);
    }

    @Override
    public Context bitwiseOr(ICalcValue other, Context ctx) {
        return target.bitwiseOr(other, ctx);
    }

    @Override
    public Context bitwiseXor(ICalcValue other, Context ctx) {
        return target.bitwiseXor(other, ctx);
    }
    
    @Override
    public boolean serialize(SerializationContext ctx) throws NMCalcException {
        return getTarget().serialize(ctx);
    }
    
    @Override
    public Context serialize(Context ctx, SerializationContext serCtx) {
        return getTarget().serialize(ctx, serCtx);
    }
    
    @Override
    public Optional<UUID> getId() {
        return getTarget().getId();
    }

    @Override
    public Context substitute(Context ctx, ICalcValue value, ICalcValue replacement) {
        return getTarget().substitute(ctx, value, replacement);
    }
    
    @Override
    public Context setItem(Context ctx, ICalcValue index, ICalcValue value) {
        return getTarget().setItem(ctx, index, value);
    }

    @Override
    public void setItem(ICalcValue index, ICalcValue value) throws NMCalcException {
        getTarget().setItem(index, value);
    }

    @Override
    public Context getItem(Context ctx, ICalcValue index) {
        return getTarget().getItem(ctx, index);
    }
    
    @Override
    public boolean isHelp() {
        return getTarget().isHelp();
    }

    @Override
    public Optional<String> getHelp() {
        return getTarget().getHelp();
    }

    @Override
    public void setHelp(String help) {
        getTarget().setHelp(help);
    }
    
    @Override
    public boolean isObject() {
        return getTarget().isObject();
    }

    @Override
    public <T extends ICalcValueAnnotation> Optional<T> getAnnotation(Class<T> cls) {
        return getTarget().getAnnotation(cls);
    }
    
    @Override
    public LinkedList<ICalcValueAnnotation> getAnnotations() {
        return getTarget().getAnnotations();
    }

    @Override
    public ICalcValue addAnnotation(ICalcValueAnnotation value) {
        getTarget().addAnnotation(value);
        return this;
    }
    
    @Override
    public ICalcValue unwrap(Context ctx) {
        return getTarget().unwrap(ctx);
    }
    
    @Override
    public Context getHead(Context ctx) {
        return getTarget().getHead(ctx);
    }

    @Override
    public Context getTail(Context ctx) {
        return getTarget().getTail(ctx);
    }
    
    @Override
    public boolean isSymbol() {
        return getTarget().isSymbol();
    }

    @Override
    public boolean isList() {
        return getTarget().isList();
    }

    @Override
    public boolean isSome() {
        return getTarget().isSome();
    }
    
    @Override
    public boolean isNothing() {
        return getTarget().isNothing();
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        return getTarget().getRepr(ctx);
    }

    @Override
    public String getExprRepr(ReprContext ctx) {
        return getTarget().getExprRepr(ctx);
    }

    @Override
    public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
        return getTarget().getApplyRepr(arguments, ctx);
    }

    @Override
    public boolean isError() {
        return getTarget().isError();
    }

    @Override
    public boolean isSpecialForm() {
        return getTarget().isSpecialForm();
    }

    @Override
    public Context getAttribute(SymbolValue attrName, Context ctx) {
        return getTarget().getAttribute(attrName, ctx);
    }

    @Override
    public Context setAttribute(SymbolValue attrName, ICalcValue value, Context ctx) {
        return getTarget().setAttribute(attrName, value, ctx);
    }

    @Override
    public boolean getBooleanValue() {
        return getTarget().getBooleanValue();
    }

    @Override
    public ICalcValue toFloat(Context ctx) {
        return getTarget().toFloat(ctx);
    }
    
    @Override
    public BigDecimal getDecimalValue() {
        return getTarget().getDecimalValue();
    }

    @Override
    public ICalcValue toStringValue(Context ctx) {
        return getTarget().toStringValue(ctx);
    }

    @Override
    public String getStringValue(Context ctx) {
        return getTarget().getStringValue(ctx);
    }

    @Override
    public ICalcValue toSymbolValue(Context ctx) {
        return getTarget().toSymbolValue(ctx);
    }

    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        return getTarget().isValueEqual(other, ctx);
    }

    @Override
    public int compareValue(ICalcValue other, Context ctx) {
        return getTarget().compareValue(other, ctx);
    }

    @Override
    public ICalcValue negate(Context ctx) {
        return getTarget().negate(ctx);
    }

    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        return getTarget().add(other, ctx);
    }

    @Override
    public ICalcValue subtract(ICalcValue other, Context ctx) {
        return getTarget().subtract(other, ctx);
    }

    @Override
    public ICalcValue multiply(ICalcValue other, Context ctx) {
        return getTarget().multiply(other, ctx);
    }

    @Override
    public ICalcValue divide(ICalcValue other, Context ctx) {
        return getTarget().divide(other, ctx);
    }

    @Override
    public boolean hasLength() {
        return getTarget().hasLength();
    }

    @Override
    public int length() {
        return getTarget().length();
    }

    @Override
    public ICalcValue getItem(int index) {
        return getTarget().getItem(index);
    }

    @Override
    public Context unpack(Context ctx) {
        return getTarget().unpack(ctx);
    }

    @Override
    public Context eval(Context ctx) {
        return getTarget().eval(ctx);
    }

    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        return getTarget().apply(ctx, arguments);
    }

    @Override
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments) {
        return getTarget().applySpecial(ctx, arguments);
    }

    @Override
    public Context unapply(Context ctx, ICalcValue value) {
        return getTarget().unapply(ctx, value);
    }

    @Override
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function) {
        return getTarget().withNonError(function);
    }

    @Override
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function) {
        return getTarget().withNonError(other, function);
    }

    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return getTarget().visit(visitor, context);
    }

    
    
    
    
}
