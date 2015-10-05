/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jan
 */
public class ListValue extends CalcValue {

    private final List<ICalcValue> values = new ArrayList();

    
    public ListValue() {
    }

    public ListValue(ICalcValue[] items) {
        for (ICalcValue item : items) {
            values.add(item);
        }
    }
    
    public ListValue(Collection<? extends ICalcValue> items) {
        values.addAll(items);
    }

    public ListValue(ICalcValue head, Collection<? extends ICalcValue> tail) {
        values.add(head);
        values.addAll(tail);
    }
    
    
    public List<ICalcValue> getValues() {
        return Collections.unmodifiableList(values);
    }
    
    
    @Override
    public String toString() {
        return "ListValue{" + "values=" + values + '}';
    }

    
    @Override
    public String getRepr() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[");
        
        sb.append(StringUtils.join(
                ", ",
                values.stream().map(val -> val.getRepr())
        ));
        
        sb.append("]");
        
        return sb.toString();
    }
    
    @Override
    public String getExprRepr() {
        if (values.isEmpty()) {
            return "[]";
        }
        ICalcValue head = values.get(0);
        return head.getApplyRepr(values.subList(1, values.size()));
    }
    
    
    @Override
    public boolean getBooleanValue() {
        return length() > 0;
    }

    
    @Override
    public ICalcValue add(ICalcValue other) {
        if (other.isError()) {
            return other;
        }
        if (!other.hasLength()) {
            return super.add(other);
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
            ctx.setReturnedValue(ErrorValue.formatted("Cannot eval %s.", getRepr()));
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
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        return visitor.visitList(this, context);
    }
    
}
