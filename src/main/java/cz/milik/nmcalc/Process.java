/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.ErrorValue;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.SymbolValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.utils.ListenerCollection;
import java.util.List;

/**
 *
 * @author jan
 */
public class Process {
    
    public enum State {
        NOT_STARTED,
        RUNNING,
        BREAKED,
        FINISHED,
        FINISHED_WITH_ERROR,
        HANDLING_INTERRUPT,
    }
    
    
    private Interpreter interpreter;    
    
    public Interpreter getInterpreter() {
        return interpreter;
    }
    
    
    private Context context;

    public Context getContext() {
        return context;
    }
    
    
    private State state;

    public State getState() {
        return state;
    }
    
    protected void setState(State state) {
        this.state = state;
    }
    
    
    private SymbolValue interruptName;
    
    private List<? extends ICalcValue> interruptArguments;

    public SymbolValue getInterruptName() {
        return interruptName;
    }

    public List<? extends ICalcValue> getInterruptArguments() {
        return interruptArguments;
    }
    
    public void setInterrupt(SymbolValue name, List<? extends ICalcValue> args) {
        this.interruptName = name;
        this.interruptArguments = args;
    }
    
    
    private ICalcValue interruptReturn;

    public ICalcValue getInterruptReturn() {
        return interruptReturn;
    }
    
    public void setInterruptReturn(ICalcValue interruptReturn) {
        this.interruptReturn = interruptReturn;
    }
    
    
    public ICalcValue getReturnedValue() {
        return context.getReturnedValue();
    }
    
    
    public Process(Interpreter interpreter, Context context) {
        this.interpreter = interpreter;
        this.context = context;
        setState(State.NOT_STARTED);
    }
    
    
    private ExecResult execute() {
        Context current = context;
        
        try {    
            Context parent;
            ExecResult result;
            ICalcValue method;
            List<? extends ICalcValue> arguments;
            
            while (true) {
                result = current.execute(this);

                if (result.getNewContext() != null) {
                    current = result.getNewContext();
                }

                switch (result.getExitCode()) {
                    case CONTINUE:
                        break;

                    case ERROR:
                    case EXIT:
                    case YIELD:
                        return result;

                    case RETURN:
                        parent = current.getParent();
                        if (parent == null) {
                            return result;
                        }
                        parent.setReturnedValue(result.getReturnValue());
                        current = parent;
                        break;

                    case CALL:
                        method = result.getReturnValue();
                        arguments = result.getArguments();
                        current = method.apply(current, arguments);
                        break;
                    
                    case BREAK:
                        return result;
                        
                    case INTERRUPT:
                        return result;
                    
                    default:
                        throw new AssertionError(String.format(
                                "Unknown exit code: %s.",
                                result.getExitCode().toString()
                        ));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return new ExecResult(
                    ExecResult.ExitCodes.ERROR,
                    current,
                    new ErrorValue(
                            String.format(
                                    "Internal error (%s: %s).",
                                    e.getClass().getSimpleName(),
                                    e.getMessage()
                            ),
                            current,
                            e
                    )
            );
        }
    }
    
    public boolean evalSynchronous() {
        switch (getState())
        {
            case FINISHED:
            case FINISHED_WITH_ERROR:
                return false;
        }
        
        setState(State.RUNNING);
        
        boolean continueEval = true;
        while (continueEval) {
            ExecResult result = execute();
            
            if (result.getNewContext() != null) {
                context = result.getNewContext();
            }
            
            switch (result.getExitCode()) {
                case EXIT:
                    //continueEval = false;
                    setState(State.FINISHED);
                    fireOnFinished();
                    return false;
                    
                case ERROR:
                    //continueEval = false;
                    setState(State.FINISHED_WITH_ERROR);
                    fireOnError();
                    return false;
                    
                case BREAK:
                    //continueEval = false;
                    setState(State.BREAKED);
                    fireOnBreak();
                    return true;
                
                case INTERRUPT:
                    setInterruptReturn(CalcValue.nothing());
                    setState(State.HANDLING_INTERRUPT);
                    fireOnInterrupt(getInterruptName(), getInterruptArguments());
                    setState(State.RUNNING);
                    return true;
            }
        }
        
        setState(State.RUNNING);
        return true;
    }
    
    
    private final ListenerCollection<IListener> listeners = new ListenerCollection(new IListener[] {});

    public boolean addListener(IListener listener) {
        return listeners.add(listener);
    }
    
    public boolean removeListener(IListener listener) {
        return listeners.remove(listener);
    }
    
    protected void fireOnFinished() {
        listeners.handleEvent(listener -> {
            listener.onFinished(this);
        });
    }
    
    protected void fireOnError() {
        listeners.handleEvent(listener -> {
            listener.onError(this);
        });
    }
    
    protected void fireOnBreak() {
        listeners.handleEvent(l -> { l.onBreak(this); });
    }
    
    protected void fireOnInterrupt(SymbolValue name, List<? extends ICalcValue> arguments) {
        listeners.handleChain(l -> l.onInterrupt(this, name, arguments));
    }
    
    
    public interface IListener {
        public void onFinished(Process process);
        public void onError(Process process);
        public void onBreak(Process process);
        
        /**
         * Returns true if the interrupt was handled and doesn't need to be
         * passed to further listeners.
         * 
         * @param process Calling process.
         * @param name Name of the interrupt.
         * @param arguments Arguments for the interrupt.
         * @return 
         */
        public boolean onInterrupt(Process process, SymbolValue name, List<? extends ICalcValue> arguments);
    }
    
    
    public static class Adapter implements IListener {
        @Override
        public void onFinished(Process process) {
            // Do nothing.
        }

        @Override
        public void onError(Process process) {
            // Do nothing.
        }
        
        @Override
        public void onBreak(Process process) {
            // Do nothing.
        }
        
        @Override
        public boolean onInterrupt(Process process, SymbolValue name, List<? extends ICalcValue> arguments) {
            // Do nothing.
            return false;
        }
    }
    
}
