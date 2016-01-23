/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueVisitor;
import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.TextWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class BoundMethod extends CalcValue {

    private final ICalcValue method;

    public ICalcValue getMethod() {
        return method;
    }
    
    
    private final ICalcValue object;

    public ICalcValue getObject() {
        return object;
    }
    
    
    public BoundMethod(ICalcValue method, ICalcValue object) {
        this.method = method;
        this.object = object;
    }
    
    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        method.print(out, ctx);
        out.plain(" bound to ");
        object.print(out, ctx);
    }
    
    
    @Override
    public boolean isSpecialForm() {
        return method.isSpecialForm();
    }
    
    @Override
    protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        List<ICalcValue> newArguments = new ArrayList();
        newArguments.add(object);
        newArguments.addAll(arguments);
        return method.applySpecial(ctx, newArguments);
    }
    
    @Override
    protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        List<ICalcValue> newArguments = new ArrayList();
        newArguments.add(object);
        newArguments.addAll(arguments);
        return method.apply(ctx, newArguments);
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
