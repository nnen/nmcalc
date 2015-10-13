/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jan
 */
public class ObjectValue extends CalcValue {

    private Map<SymbolValue, ICalcValue> slots = new HashMap();
    
    public ObjectValue() {
    }
    
    
    @Override
    public boolean isObject() { return true; }
    
    @Override
    public Context getAttribute(SymbolValue attrName, Context ctx) {
        ICalcValue value = slots.get(attrName);
        if (value == null) {
            ctx.setReturnedError(
                    "Object doesn't have attribute '%s'.",
                    attrName.getValue()
            );
        } else {
            ctx.setReturnedValue(value);
        }
        return ctx;
    }
    
    @Override
    public Context setAttribute(SymbolValue attrName, ICalcValue value, Context ctx) {
        slots.put(attrName, value);
        ctx.setReturnedValue(value);
        return ctx;
    }
    
    
    @Override
    public <T, U> T visit(ICalcValueVisitor<T, U> visitor, U context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public static class SpecialMethodContext extends Context {
        
        private final ObjectValue object;
        private final SymbolValue specialMethodName;
        
        public SpecialMethodContext(ObjectValue obj, SymbolValue specialMethodName, Context parent, ICalcValue method) {
            super(parent, parent.getEnvironment(), method);
            this.object = obj;
            this.specialMethodName = specialMethodName;
        }

        @Override
        public ExecResult execute(Interpreter interpreter) {
            int pc = getPC();
            
            switch (pc) {
                case 0:
                    setPC(pc + 1);
                    return this.ctxContinue(object.getAttribute(specialMethodName, this));
                case 1:
                    
                default:
                    return invalidPC(pc);
            }
        }
        
    }
    
}
