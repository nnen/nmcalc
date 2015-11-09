/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.text.IPrintable;
import cz.milik.nmcalc.text.TextWriter;
import cz.milik.nmcalc.values.ErrorValue;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.utils.IMonad;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public class Context implements IPrintable {
    
    private final Context parent;
    
    public Context getParent() { return parent; }
    
    
    private Environment environment;
    
    public Environment getEnvironment() { return environment; }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    
    private final ICalcValue method;
    
    public ICalcValue getMethod() { return method; }
    
    
    private MathContext mathContext = MathContext.DECIMAL128;
    
    public MathContext getMathContext() {
        return mathContext;
    }
    
    
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

    public Context setReturnedError(String message, Object... args) {
        setReturnedValue(CalcValue.error(this, message, args));
        return this;
    }
    
    
    private ReprContext reprContext;
    
    public ReprContext getReprContext() {
        return reprContext;
    }
    
    public void setReprContext(ReprContext reprContext) {
        this.reprContext = reprContext;
    }
    
    
    public Context(Context parent, Environment env, ICalcValue method) {
        this.parent = parent;
        this.environment = env;
        this.method = method;
        this.pc = 0;
        if (parent == null) {
            this.reprContext = ReprContext.getDefault();
        } else {
            this.reprContext = parent.getReprContext();
        }
    }
    
    public Context(Context parent, Environment env, ICalcValue method, ReprContext reprContext) {
        this.parent = parent;
        this.environment = env;
        this.method = method;
        this.pc = 0;
        this.reprContext = reprContext;
    }
    
    
    public Context createChild(ICalcValue method) {
        return new Context(this, getEnvironment(), method);
    }
    
    public ExecResult execute(Process process) {
        try {
            return execute(process, getPC());
        } catch (NMCalcException ex) {
            Logger.getLogger(Context.class.getName()).log(Level.SEVERE, null, ex);
            return ctxReturn(CalcValue.error(this, ex));
        }
    }
    
    protected ExecResult execute(Process process, int pc) throws NMCalcException {
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

    
    public void printStackTrace(TextWriter out, ReprContext ctx) {
        Context current = this;
        Environment lastEnv = null;
        int index = 0;
        
        out.startTable();
        
        out.startTableRow();
        out.tableCell(true, "Frame");
        out.tableCell(true, "PC");
        out.tableCell(true, "Description");
        out.end();
        
        while (current != null) {
            out.startTableRow();
            
            out.startTableCell();
            out.plain(Integer.toString(index));
            out.end();
            
            out.startTableCell();
            out.plain(Integer.toString(current.getPC()));
            out.end();
            
            out.startTableCell();
            current.printDescription(out, ctx);
            out.end();
            
            out.end();
            
            Environment env = current.getEnvironment();
            if (env != lastEnv) {
                out.startTableRow();
                out.startTableCell();
                out.end();
                out.startTableCell();
                out.end();
                out.startTableCell();
                env.printDebug(out, ctx);
                out.end();
                out.end();
                lastEnv = env;
            }
            
            current = current.getParent();
            index++;
        }
        
        out.end();
    }
    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        printDebug(out, ctx);
    }
    
    @Override
    public void printDebug(TextWriter out, ReprContext ctx) {
        printStackTrace(out, ctx);
        /*
        Context current = this;
        Environment lastEnv = null;
        int index = 0;
        
        while (current != null) {
            Environment env = current.getEnvironment();
            
            out.startPar();
            out.monospace("[%d] (%d) ", index, current.getPC());
            current.printDescription(out, ctx);
            out.end();
            
            if (env != lastEnv) {
                out.startBlockQuote();
                env.printDebug(out, ctx);
                out.end();
                lastEnv = env;
            }
            
            current = current.getParent();
            index++;
        }
        */
    }
    
    protected void printDescription(TextWriter out, ReprContext ctx) {
        out.plain("executing ");
        if (getMethod() == null) {
            out.monospace(getClass().getName());
        } else {
            out.monospace(getMethod().getExprRepr(ctx));
        }
    }
    
    
    public static Context createRoot() { return createRoot(new Environment()); }
    public static Context createRoot(Environment env) { return new ReturnContext(env); }
    
    
    protected ExecResult invalidPC(int value) {
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
    
    protected ExecResult eval(Interpreter interpreter, ICalcValue expr) {
        return new ExecResult(
                ExecResult.ExitCodes.CONTINUE,
                interpreter.eval(expr, this),
                null
        );
    }
    
    protected ExecResult ctxContinue(Context ctx) {
        return new ExecResult(
                ExecResult.ExitCodes.CONTINUE,
                ctx,
                null
        );
    }
    
    protected ExecResult ctxReturn(ICalcValue value) {
        return new ExecResult(
                ExecResult.ExitCodes.RETURN,
                this,
                value
        );
    }
    
    protected ExecResult error(String message, Object... args) {
        return ctxReturn(CalcValue.error(
                this,
                message,
                args
        ));
    }
    
    protected ExecResult ctxGetItem(ICalcValue container, ICalcValue index) {
        return ctxContinue(container.getItem(this, index));
    }
    
    protected ExecResult ctxBreak() {
        return new ExecResult(
                ExecResult.ExitCodes.BREAK,
                this,
                null
        );
    }
    
    
    public static class ReturnContext extends Context {
        
        public ReturnContext(Environment env) {
            super(null, env, null);
        }
        
        @Override
        public ExecResult execute(Process process) {
            return new ExecResult(
                    ExecResult.ExitCodes.EXIT,
                    this,
                    getReturnedValue()
            );
        }
        
        @Override
        protected void printDescription(TextWriter out, ReprContext ctx) {
            out.plain("root");
        }
        
    }
    
    
    public static abstract class ApplyContext extends Context {
        
        private final List<? extends ICalcValue> arguments;
        
        private final List<ICalcValue> evaluated = new ArrayList();
        
        public ApplyContext(Context parent, Environment env, ICalcValue method, List<? extends ICalcValue> arguments) {
            super(parent, env, method);
            this.arguments = arguments;
        }

        @Override
        public void setReturnedValue(ICalcValue returnedValue) {
            super.setReturnedValue(returnedValue);
            evaluated.add(returnedValue);
        }
        
        @Override
        public ExecResult execute(Process process) {
            int pc = getPC();
            Context newCtx;
            
            switch (pc) {
                case 0:
                    setPC(pc + 1);
                    return eval(process.getInterpreter(), getMethod());
                case 1:
                    ICalcValue head = getReturnedValue();
                    if (head.isSpecialForm()) {
                        newCtx = process.getInterpreter().applySpecial(head, this, arguments);
                        setPC(4);
                        return new ExecResult(
                                ExecResult.ExitCodes.CONTINUE,
                                newCtx,
                                null
                        );
                    } else if (arguments.size() == 1) {
                        setPC(3);
                        return evalArgument(process.getInterpreter());
                    } else if (arguments.size() > 1) {
                        setPC(pc + 1);
                        return evalArgument(process.getInterpreter());
                    } else {
                        setPC(4);
                        return new ExecResult(
                                ExecResult.ExitCodes.CONTINUE,
                                process.getInterpreter().apply(head, this, arguments),
                                null
                        );
                    }
                case 2:
                    if (evaluated.size() == arguments.size()) {
                        setPC(pc + 1);
                    }
                    return evalArgument(process.getInterpreter());
                case 3:
                    setPC(pc + 1);
                    return new ExecResult(
                            ExecResult.ExitCodes.CONTINUE,
                            process.getInterpreter().apply(evaluated.get(0), this, evaluated.subList(1, evaluated.size())),
                            null
                    );
                case 4:
                    return ctxReturn(getReturnedValue());
                default:
                    return invalidPC(pc);
            }
        }
        
        protected ExecResult evalArgument(Interpreter interpreter) {
            ICalcValue arg = arguments.get(evaluated.size() - 1);
            return eval(interpreter, arg);
        }
        
        protected abstract ExecResult innerApply(ICalcValue function, List<ICalcValue> arguments);

        
        @Override
        protected void printDescription(TextWriter out, ReprContext ctx) {
            out.plain("applying ");
            out.monospace(getMethod().getExprRepr(ctx));
        }
        
    }
    
    
    public static abstract class StackContext extends Context {
        
        public StackContext(Context parent, Environment env, ICalcValue method) {
            super(parent, env, method);
        }

        public StackContext(Context parent, Environment env, ICalcValue method, ReprContext reprContext) {
            super(parent, env, method, reprContext);
        }
        
        public StackContext(Context parent, ICalcValue method) {
            this(parent, parent.getEnvironment(), method);
        }
        
        
        private final List<ICalcValue> stack = new ArrayList();
        
        public ICalcValue push(ICalcValue value) {
            stack.add(value);
            return value;
        }
        
        public ICalcValue top() {
            int i = stack.size() - 1;
            ICalcValue top = stack.get(i);
            return top;
        }
        
        public ICalcValue top(int offset) {
            int i = stack.size() - 1 - offset;
            ICalcValue top = stack.get(i);
            return top;
        }
        
        public ICalcValue pop() {
            int i = stack.size() - 1;
            ICalcValue top = stack.get(i);
            stack.remove(i);
            return top;
        }
        
        public void pop(int count) {
            stack.subList(stack.size() - count, stack.size()).clear();
        }
        
        public ICalcValue peek(int offset) {
            int i = stack.size() - 1 - offset;
            return stack.get(i);
        }
        
        public ICalcValue peek() {
            return peek(0);
        }
        
        public ICalcValue toList(int offset, int length) {
            ListBuilder lb = new ListBuilder();
            lb.addAll(stack.subList(offset, offset + length));
            return lb.makeList();
        }
        
        
        public ExecResult ctxReturn() {
            return ctxReturn(top());
        }
        
        public ExecResult ctxReturn(int offset) {
            return ctxReturn(top(offset));
        }
        
        
        @Override
        public void setReturnedValue(ICalcValue returnedValue) {
            stack.add(returnedValue);
            super.setReturnedValue(returnedValue);
        }
        
    }
}
