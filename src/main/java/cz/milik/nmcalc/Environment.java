/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class Environment {
    
    private final Environment parent;
    
    public Environment getParent() {
        return parent;
    }
    
    
    private final Map<String, ICalcValue> variables = new HashMap();
    
    public IMonad<ICalcValue> getVariable(String name) {
        ICalcValue value = variables.get(name);
        if (value != null) {
            return Monad.just(value);
        }
        if (getParent() != null) {
            return getParent().getVariable(name);
        }
        return Monad.nothing();
    }
    
    public void setVariable(String name, ICalcValue value) {
        variables.put(name, value);
    }
    
    public void setVariable(BuiltinCalcValue value) {
        setVariable(value.getName(), value);
    }
    
    
    public Environment() {
        this.parent = null;
        setVariable("let", BuiltinCalcValue.LET);
        setVariable(BuiltinCalcValue.DEF.getName(), BuiltinCalcValue.DEF);
        
        setVariable(BuiltinCalcValue.IF_ELSE);
        
        setVariable(BuiltinCalcValue.LEN);
        
        setVariable(BuiltinCalcValue.LIST.getName(), BuiltinCalcValue.LIST);
        setVariable(BuiltinCalcValue.APPLY.getName(), BuiltinCalcValue.APPLY);
        setVariable(BuiltinCalcValue.EVAL.getName(), BuiltinCalcValue.EVAL);
        
        setVariable(BuiltinCalcValue.SQRT.getName(), BuiltinCalcValue.SQRT);
        
        MathBuiltins.initialize(this);
    }
    
    public Environment(Environment parent) {
        this.parent = parent;
    }
    
    
    public Environment createChild() {
        return new Environment(this);
    }
    
    
    public void dump(PrintStream out) {
        List<String> names = new ArrayList(variables.keySet());
        names.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        
        out.printf("%d variables.", variables.size());
        out.println();
        for (String name : names) {
            out.print(name);
            out.print(": ");
            ICalcValue value = variables.get(name);
            out.println(value.getRepr());
        }
    }
    
}
