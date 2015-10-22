/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jan
 */
public class ListValue extends CalcValue {

    private final List<ICalcValue> values;

    
    public ListValue() {
        values = new ArrayList();
    }

    public ListValue(ICalcValue[] items) {
        values = new ArrayList();
        for (ICalcValue item : items) {
            values.add(item);
        }
    }
    
    public ListValue(Collection<? extends ICalcValue> items) {
        values = new ArrayList();
        values.addAll(items);
    }
    
    public ListValue(List<ICalcValue> items, boolean takeOwnership) {
        if (takeOwnership) {
            values = items;
        } else {
            values = new ArrayList(items);
        }
    }
    
    public ListValue(ICalcValue head, Collection<? extends ICalcValue> tail) {
        values = new ArrayList();
        values.add(head);
        values.addAll(tail);
    }
    
    
    public List<ICalcValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    
    @Override
    public boolean isList() {
        return true;
    }
    
    
    @Override
    public String toString() {
        return "ListValue{" + "values=" + values + '}';
    }

    
    @Override
    public String getRepr(ReprContext ctx) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        
        sb.append(StringUtils.join(
                ", ",
                values.stream().map(val -> val.getRepr(ctx))
        ));
        
        sb.append("]");
        
        return sb.toString();
    }
    
    @Override
    public String getExprRepr(ReprContext ctx) {
        if (values.isEmpty()) {
            return "[]";
        }
        ICalcValue head = values.get(0);
        return head.getApplyRepr(values.subList(1, values.size()), ctx);
    }
    
    
    @Override
    public boolean getBooleanValue() {
        return length() > 0;
    }

    
    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        if (other.isError()) {
            return other;
        }
        if (!other.hasLength()) {
            return super.add(other, ctx);
        }
        List<ICalcValue> newValues = new ArrayList();
        newValues.addAll(values);
        for (int i = 0; i < other.length(); i++) {
            newValues.add(other.getItem(i));
        }
        return CalcValue.list(newValues);
    }
    
    
    @Override
    public boolean hasLength() {
        return true;
    }
    
    @Override
    public int length() {
        return values.size();
    }
    
    @Override
    public ICalcValue getItem(int index) {
        if ((index < 0) || (index >= values.size())) {
            return new ErrorValue(String.format(
                    "Invalid index: %d.",
                    index
            ));
        }
        return values.get(index);
    }

    @Override
    public Context getItem(Context ctx, ICalcValue index) {
        ICalcValue fltValue = index.toFloat(ctx);
        if (fltValue.isError()) {
            ctx.setReturnedValue(fltValue);
            return ctx;
        }
        BigDecimal dec = fltValue.getDecimalValue();
        int intValue = dec.intValue();
        if ((intValue < 0) || (intValue >= values.size())) {
            ctx.setReturnedError(
                    "Invalid index: %d. Expected value greater or equal to 0 and less than %d.",
                    intValue,
                    values.size()
            );
            return ctx;
        }
        ctx.setReturnedValue(values.get(intValue));
        return ctx;
    }

    @Override
    public Context setItem(Context ctx, ICalcValue index, ICalcValue value) {
        ICalcValue fltValue = index.toFloat(ctx);
        if (fltValue.isError()) {
            ctx.setReturnedValue(fltValue);
            return ctx;
        }
        BigDecimal dec = fltValue.getDecimalValue();
        int intValue = dec.intValue();
        if ((intValue < 0) || (intValue >= values.size())) {
            ctx.setReturnedError(
                    "Invalid index: %d. Expected value greater or equal to 0 and less than %d.",
                    intValue,
                    values.size()
            );
            return ctx;
        }
        values.set(intValue, value);
        ctx.setReturnedValue(value);
        return ctx;
    }
    
    public ICalcValue getHead() {
        if (values.size() > 0) {
            return values.get(0);
        }
        return null;
    }
    
    public List<? extends ICalcValue> getTail() {
        if (values.size() < 2) {
            return Collections.emptyList();
        }
        return values.subList(1, values.size());
    }
    
    
    @Override
    public Context eval(Context ctx) {
        if (values.size() == 0) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    "Cannot eval %s.",
                    getRepr(ctx.getReprContext())));
            return ctx;
        }
        return new Context.ApplyContext(ctx, ctx.getEnvironment(), getHead(), getTail()) {  
            @Override
            protected ExecResult innerApply(ICalcValue function, List<ICalcValue> arguments) {
                return new ExecResult(
                        ExecResult.ExitCodes.CALL,
                        this,
                        function,
                        arguments
                );
            }
        };
        //return getHead().apply(ctx, getTail());
    }

    @Override
    protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
        return new Context(ctx, ctx.getEnvironment(), this) {
            private ICalcValue unapplyResult;
            
            @Override
            public ExecResult execute(Interpreter interpreter) {
                int pc = getPC();
                ICalcValue returned;
                
                if (values.isEmpty()) {
                    // Matched list is empty.
                    return ctxReturn(CalcValue.nothing());
                }
                
                if (pc == 0) {
                    ICalcValue head = values.get(0);
                    setPC(pc + 1);
                    return ctxContinue(head.unapply(this, value));
                } else if (pc == 1) {
                    unapplyResult = getReturnedValue();
                    if (unapplyResult.isError() || unapplyResult.isNothing()) {
                        setPC(1 + values.size());
                        return ctxReturn(unapplyResult);
                    }
                    if (!unapplyResult.isSome()) {
                        setPC(1 + values.size());
                        return ctxReturn(CalcValue.error(
                                this,
                                "Expected the unapply operation of %s to return a 'some' or 'nothing'. %s was returned.",
                                values.get(0).getRepr(getReprContext()),
                                unapplyResult.getRepr(getReprContext())
                        ));
                    }
                    unapplyResult = unapplyResult.unwrap(this);
                    if (!unapplyResult.isList()) {
                        setPC(1 + values.size());
                        return ctxReturn(CalcValue.error(
                                this,
                                "Expected the unapply operation of %s to return some list. %s was returned.",
                                values.get(0).getRepr(getReprContext()),
                                unapplyResult.getRepr(getReprContext())
                        ));
                    }
                    if (unapplyResult.length() != (values.size() - 1)) {
                        setPC(1 + values.size());
                        // Invlaid number of arguments.
                        return ctxReturn(CalcValue.nothing());
                    }
                    setPC(pc + 1);
                    if (values.size() > 1) {
                        return ctxContinue(values.get(pc).unapply(this, unapplyResult.getItem(pc - 1)));
                    } else {
                        return ctxReturn(CalcValue.some(CalcValue.list()));
                    }
                } else if (pc < values.size()) {
                    returned = getReturnedValue();
                    if (returned.isError() || returned.isNothing()) {
                        setPC(1 + values.size());
                        return ctxReturn(returned);
                    }
                    setPC(pc + 1);
                    return ctxContinue(values.get(pc).unapply(this, unapplyResult.getItem(pc - 1)));
                } else if (pc == values.size()) {
                    returned = getReturnedValue();
                    if (returned.isError() || returned.isNothing()) {
                        setPC(1 + values.size());
                        return ctxReturn(returned);
                    }
                    setPC(pc + 1);
                    return ctxReturn(CalcValue.some(CalcValue.list()));
                } else {
                    return invalidPC(pc);
                }
            }
        };
    }

    
    @Override
    public Context substitute(Context ctx, ICalcValue value, ICalcValue replacement) {
        if (value.isError()) {
            ctx.setReturnedValue(value);
            return ctx;
        }
        
        if (replacement.isError()) {
            ctx.setReturnedValue(replacement);
            return ctx;
        }
        
        return new Context.StackContext(ctx, this) {
            @Override
            public ExecResult execute(Interpreter interpreter) {
                int pc = getPC();
                
                if ((pc > 0) && (pc <= values.size())) {
                    if (peek().isError()) {
                        return this.ctxReturn(peek());
                    }
                }
                
                if (pc < values.size()) {
                    ICalcValue item = values.get(pc);
                    setPC(pc + 1);
                    return ctxContinue(item.substitute(this, value, replacement));
                } else if (pc == values.size()) {
                    setPC(pc + 1);
                    return ctxReturn(toList(0, values.size()));
                }
                
                return invalidPC(pc);
            }
        };
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitList(this, context);
    }
    
}
