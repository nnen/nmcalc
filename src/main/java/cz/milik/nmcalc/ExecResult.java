/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.util.List;

/**
 *
 * @author jan
 */
public class ExecResult {
    
    public enum ExitCodes {
        ERROR,
        EXIT,
        CONTINUE,
        /**
         * Current context is returned, return value is in {@link getReturnValue()}.
         */
        RETURN,
        /**
         * Current context is returned, method to be called is in
         * {@link Context.getMethod()}.
         */
        CALL,
        YIELD,
    }
    
    
    private final ExitCodes exitCode;
    
    public ExitCodes getExitCode() { return exitCode; }
    
    
    public final Context newContext;
    
    public Context getNewContext() { return newContext; }
    
    
    public final ICalcValue returnValue;

    public ICalcValue getReturnValue() {
        return returnValue;
    }
    
    
    public final List<? extends ICalcValue> arguments;
    
    public List<? extends ICalcValue> getArguments() {
        return arguments;
    }
    
    
    public ExecResult(ExitCodes mode, Context newContext, ICalcValue returnValue) {
        this.exitCode = mode;
        this.newContext = newContext;
        this.returnValue = returnValue;
        this.arguments = null;
    }
    
    public ExecResult(ExitCodes mode, Context newContext, ICalcValue returnValue, List<? extends ICalcValue> arguments) {
        this.exitCode = mode;
        this.newContext = newContext;
        this.returnValue = returnValue;
        this.arguments = arguments;
    }
    
}
