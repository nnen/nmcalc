/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jan
 */
public class Environment {
    
    private final Map<String, ICalcValue> variables = new HashMap();
    
    public IMonad<ICalcValue> getVariable(String name) {
        return Monad.maybe(variables.get(name));
    }
    
    public void setVariable(String name, ICalcValue value) {
        variables.put(name, value);
    }
    
    
    public Environment() {
        setVariable(BuiltinCalcValue.LIST.getName(), BuiltinCalcValue.LIST);
        setVariable(BuiltinCalcValue.APPLY.getName(), BuiltinCalcValue.APPLY);
        setVariable(BuiltinCalcValue.EVAL.getName(), BuiltinCalcValue.EVAL);
    }
    
}
