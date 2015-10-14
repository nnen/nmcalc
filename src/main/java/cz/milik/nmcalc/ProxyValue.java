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
public abstract class ProxyValue implements ICalcValue {
    
    private final ICalcValue target;

    public ProxyValue(ICalcValue target) {
        this.target = target;
    }

    @Override
    public boolean isHelp() {
        return target.isHelp();
    }

    @Override
    public Optional<String> getHelp() {
        return target.getHelp();
    }

    @Override
    public void setHelp(String help) {
        target.setHelp(help);
    }
    
    @Override
    public boolean isObject() {
        return target.isObject();
    }

    @Override
    public <T extends ICalcValueAnnotation> Optional<T> getAnnotation(Class<T> cls) {
        return target.getAnnotation(cls);
    }
    
    @Override
    public LinkedList<ICalcValueAnnotation> getAnnotations() {
        return target.getAnnotations();
    }

    @Override
    public ICalcValue addAnnotation(ICalcValueAnnotation value) {
        target.addAnnotation(value);
        return this;
    }
    
    @Override
    public ICalcValue unwrap(Context ctx) {
        return target.unwrap(ctx);
    }
    
    @Override
    public Context getHead(Context ctx) {
        return target.getHead(ctx);
    }

    @Override
    public Context getTail(Context ctx) {
        return target.getTail(ctx);
    }
    
    @Override
    public boolean isSymbol() {
        return target.isSymbol();
    }

    @Override
    public boolean isList() {
        return target.isList();
    }

    @Override
    public boolean isSome() {
        return target.isSome();
    }
    
    @Override
    public boolean isNothing() {
        return target.isNothing();
    }
    
    @Override
    public String getRepr(ReprContext ctx) {
        return target.getRepr(ctx);
    }

    @Override
    public String getExprRepr(ReprContext ctx) {
        return target.getExprRepr(ctx);
    }

    @Override
    public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
        return target.getApplyRepr(arguments, ctx);
    }

    @Override
    public boolean isError() {
        return target.isError();
    }

    @Override
    public boolean isSpecialForm() {
        return target.isSpecialForm();
    }

    @Override
    public Context getAttribute(SymbolValue attrName, Context ctx) {
        return target.getAttribute(attrName, ctx);
    }

    @Override
    public Context setAttribute(SymbolValue attrName, ICalcValue value, Context ctx) {
        return target.setAttribute(attrName, value, ctx);
    }

    @Override
    public boolean getBooleanValue() {
        return target.getBooleanValue();
    }

    @Override
    public ICalcValue toFloat(Context ctx) {
        return target.toFloat(ctx);
    }
    
    @Override
    public BigDecimal getDecimalValue() {
        return target.getDecimalValue();
    }

    @Override
    public ICalcValue toStringValue(Context ctx) {
        return target.toStringValue(ctx);
    }

    @Override
    public String getStringValue(Context ctx) {
        return target.getStringValue(ctx);
    }

    @Override
    public ICalcValue toSymbolValue(Context ctx) {
        return target.toSymbolValue(ctx);
    }

    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        return target.isValueEqual(other, ctx);
    }

    @Override
    public int compareValue(ICalcValue other, Context ctx) {
        return target.compareValue(other, ctx);
    }

    @Override
    public ICalcValue negate(Context ctx) {
        return target.negate(ctx);
    }

    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        return target.add(other, ctx);
    }

    @Override
    public ICalcValue subtract(ICalcValue other, Context ctx) {
        return target.subtract(other, ctx);
    }

    @Override
    public ICalcValue multiply(ICalcValue other, Context ctx) {
        return target.multiply(other, ctx);
    }

    @Override
    public ICalcValue divide(ICalcValue other, Context ctx) {
        return target.divide(other, ctx);
    }

    @Override
    public boolean hasLength() {
        return target.hasLength();
    }

    @Override
    public int length() {
        return target.length();
    }

    @Override
    public ICalcValue getItem(int index) {
        return target.getItem(index);
    }

    @Override
    public Context unpack(Context ctx) {
        return target.unpack(ctx);
    }

    @Override
    public Context eval(Context ctx) {
        return target.eval(ctx);
    }

    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        return target.apply(ctx, arguments);
    }

    @Override
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments) {
        return target.applySpecial(ctx, arguments);
    }

    @Override
    public Context unapply(Context ctx, ICalcValue value) {
        return target.unapply(ctx, value);
    }

    @Override
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function) {
        return target.withNonError(function);
    }

    @Override
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function) {
        return target.withNonError(other, function);
    }

    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return target.visit(visitor, context);
    }
    
    
    
}
