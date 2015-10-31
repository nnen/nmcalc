/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.BuiltinCalcValue;

/**
 *
 * @author jan
 */
public class BuiltinProxy extends ProxyValue {
    
    private final int builtinId;

    
    public BuiltinProxy(int builtinId) {
        this.builtinId = builtinId;
    }
    
    
    @Override
    public ICalcValue getTarget() {
        ICalcValue value = BuiltinCalcValue.getBuiltinSet().get(builtinId);
        if (value == null) {
            return CalcValue.nothing();
        }
        return value;
    }
    
    
    protected Object readResolve()
        throws java.io.ObjectStreamException
    {
        return getTarget();
    }
    
}
