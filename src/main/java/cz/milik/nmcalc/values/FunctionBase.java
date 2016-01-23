/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.Context;

/**
 *
 * @author jan
 */
public abstract class FunctionBase extends CalcValue {
    
    private String functionName;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    
    public FunctionBase(String functionName) {
        this.functionName = functionName;
    }
    
    
    @Override
    public Context bind(Context ctx, ICalcValue obj) {
        ctx.setReturnedValue(new BoundMethod(this, obj));
        return ctx;
    }
    
}
