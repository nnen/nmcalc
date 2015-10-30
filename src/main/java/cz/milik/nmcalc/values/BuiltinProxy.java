/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Environment;

/**
 *
 * @author jan
 */
public class BuiltinProxy extends ProxyValue {
    
    private transient Environment env;
    
    private String name;

    
    public BuiltinProxy(Environment env, String name) {
        this.env = env;
        this.name = name;
    }

    
    @Override
    public ICalcValue getTarget() {
        ICalcValue target = super.getTarget();
        
        if (target.isNothing()) {
            env.getVariable(name).bind(var -> {
                setTarget(var);
            });
        }
        
        return super.getTarget();
    }
    
}
