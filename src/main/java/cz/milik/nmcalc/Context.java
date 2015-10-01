/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class Context {
    
    private final Context parent;
    
    public Context getParent() { return parent; }
    
    
    private Environment environment;
    
    public Environment getEnvironment() { return environment; }
    
    
    private final ICalcValue method;
    
    public ICalcValue getMethod() { return method; }
    
    
    private int pc;
    
    public int getPC() { return pc; }
    
    public int setPC(int value) {
        pc = value;
        return value;
    }
    
    
    private ICalcValue returnedValue;
    
    public ICalcValue getReturnedValue() {
        return returnedValue;
    }
    
    public void setReturnedValue(ICalcValue returnedValue) {
        this.returnedValue = returnedValue;
    }
    
    
    public Context(Context parent, Environment env, ICalcValue method) {
        this.parent = parent;
        this.environment = env;
        this.method = method;
        this.pc = 0;
    }
    
    
    public Context createChild(ICalcValue method) {
        return new Context(this, getEnvironment(), method);
    }
    
    public ExecResult execute(Interpreter interpreter) {
        return new ExecResult(
                ExecResult.ExitCodes.ERROR,
                this,
                ErrorValue.formatted("Cannot execute context %s.", this)
        );
    }
    
    
    public IMonad<ICalcValue> getVariable(String name) {
        return getEnvironment().getVariable(name);
    }
    
    public void setVariable(String aName, ICalcValue aValue) {
        getEnvironment().setVariable(aName, aValue);
    }
    
    
    public static Context createRoot() { return createRoot(new Environment()); }
    public static Context createRoot(Environment env) { return new ReturnContext(env); }
     
    
    public static class ReturnContext extends Context {
        
        public ReturnContext(Environment env) {
            super(null, env, null);
        }
        
        @Override
        public ExecResult execute(Interpreter interpreter) {
            return new ExecResult(
                    ExecResult.ExitCodes.EXIT,
                    this,
                    getReturnedValue()
            );
        }
        
    }
    
    
    public static abstract class ApplyContext extends Context {
        
        private final List<? extends ICalcValue> arguments;
        private final List<ICalcValue> results = new ArrayList();
        
        public ApplyContext(Context parent, Environment env, ICalcValue method, List<? extends ICalcValue> arguments) {
            super(parent, env, method);
            this.arguments = arguments;
        }
        
        @Override
        public ExecResult execute(Interpreter interpreter) {
            int pc = getPC();
            
            if (pc < arguments.size()) {
                if (pc > 0) {
                   results.add(getReturnedValue());
                }
                Context newCtx = arguments.get(pc).eval(this);
                setPC(pc + 1);
                return new ExecResult(
                        ExecResult.ExitCodes.CONTINUE,
                        newCtx,
                        null
                );
            } else if (pc == arguments.size()) {
                if (pc > 0) {
                   results.add(getReturnedValue());
                }
                setPC(pc + 1);
                Context newCtx = getMethod().eval(this);
                return new ExecResult(
                        ExecResult.ExitCodes.CONTINUE,
                        newCtx,
                        null
                );
            } else if (pc == arguments.size() + 1) {
                ICalcValue head = getReturnedValue();
                setPC(pc + 1);
                return innerApply(head, results);
            } else if (pc == arguments.size() + 2) {
                setPC(pc + 1);
                return new ExecResult(
                        ExecResult.ExitCodes.RETURN,
                        this,
                        getReturnedValue()
                );
            } else {
                return new ExecResult(
                        ExecResult.ExitCodes.ERROR,
                        this,
                        ErrorValue.formatted(
                                "Invalid PC value in %s: %d.",
                                this.toString(),
                                pc
                        )
                );
            }
        }
        
        protected abstract ExecResult innerApply(ICalcValue function, List<ICalcValue> arguments);
        
    }
    
}
