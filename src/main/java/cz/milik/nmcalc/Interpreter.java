/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.values.ErrorValue;
import cz.milik.nmcalc.values.SymbolValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.gui.GUIManager;
import cz.milik.nmcalc.loader.CalcLoader;
import cz.milik.nmcalc.loader.ICalcLoader;
import cz.milik.nmcalc.peg.CalcParser;
import cz.milik.nmcalc.peg.ParseResult;
import cz.milik.nmcalc.utils.ListenerCollection;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public class Interpreter {
    
    private final CalcParser parser = new CalcParser();
    
    private Context defaultContext = Context.createRoot();
    private Environment defaultEnvironment = BuiltinCalcValue.getBuiltins().createChild();

    public Context getDefaultContext() {
        return defaultContext;
    }
    
    
    private ListBuilder listBuilder = new ListBuilder();
    
    public ListBuilder getListBuilder() { return listBuilder; }
    
    
    private ICalcLoader loader = CalcLoader.getInstance();

    public ICalcLoader getLoader() {
        return loader;
    }

    public void setLoader(ICalcLoader loader) {
        if (loader == null) {
            throw new NullPointerException("`loader` argument may not be null");
        }
        this.loader = loader;
    }
    
    
    public ICalcValue parse(String input, Context ctx) {
        ParseResult<ICalcValue> result = parser.parseList(input);
        if (result.isSuccess()) {
            return result.getValue();
        }
        return ErrorValue.formatted(
                ctx,
                "Syntax error: %s",
                result.toString()
        );
    }
    
    public ICalcValue parse(String input) {
        return parse(input, getDefaultContext());
    }
    
    
    public Context evaluate(String input, Context ctx) {
        ICalcValue value = parse(input, ctx);
        return this.eval(value, ctx);
    }
    
    public ICalcValue evaluate(String input) {
        ICalcValue value = parse(input);
        return eval(value);
    }
    
    public ICalcValue eval(ICalcValue value) {
        return eval(value, (Process.IListener)null);
    }
    
    public ICalcValue eval(ICalcValue value, Process.IListener listener) {
        Process process = startEvalProcess(value, listener);
        
        while (process.evalSynchronous()) {
            if (process.getState() == Process.State.BREAKED) {
                System.err.println("Breakpoint reached.");
            }
        }
        
        return process.getReturnedValue();
    }
    
    public Process startEvalProcess(ICalcValue value, Process.IListener listener) {
        Context current = value.eval(Context.createRoot(defaultEnvironment));
        Process process = new Process(this, current);
        process.addListener(processListener);
        if (listener != null) {
            process.addListener(listener);
        }
        fireOnProcessStarted(process);
        return process;
    }
    
    public Process startEvalProcess(ICalcValue value) {
        return startEvalProcess(value, null);
    }
 
    
    public Context eval(ICalcValue value, Context ctx) {
        return value.eval(ctx);
    }
    
    public Context apply(ICalcValue value, Context ctx, List<? extends ICalcValue> arguments) {
        return value.apply(ctx, arguments);
    }
    
    public Context applySpecial(ICalcValue value, Context ctx, List<? extends ICalcValue> arguments) {
        return value.applySpecial(ctx, arguments);
    }
    
    
    public void serializeEnvironment(String fileName) throws IOException {
        defaultEnvironment.serialize(fileName);
    }
    
    public void serializeEnvironment(OutputStream out) throws IOException {
        defaultEnvironment.serialize(out);
    }
    
    public void deserializeEnvironment(String fileName) throws IOException, ClassNotFoundException {
        defaultEnvironment = Environment.deserialize(fileName);
    }
    
    
    private final Process.IListener processListener = new Process.Adapter() {
        @Override
        public boolean onInterrupt(Process process, SymbolValue name, List<? extends ICalcValue> arguments) {
            if (Objects.equals(name.getValue(), "inspect")) {
                ICalcValue value = arguments.get(0);
                GUIManager.getInstance().showValue(Interpreter.this, null, value);
            }
            return false;
        }
    };
    
    
    private final ListenerCollection<IListener> listeners = new ListenerCollection(new IListener[] {});

    public boolean addListener(IListener e) {
        return listeners.add(e);
    }
    
    public boolean remove(IListener o) {
        return listeners.remove(o);
    }
    
    private void fireOnProcessStarted(Process process) {
        listeners.handleEvent(listener -> {
            listener.onProcessStarted(this, process);
        });
    }
    
    
    public String getHelp(String name, Context ctx) {
        ISource source;
        
        try {
            source = getLoader().getSource(name, ctx);
        } catch (NMCalcException ex) {
            return null;
        }
        
        try {
            return source.getContent(ctx);
        } catch (NMCalcException ex) {
            Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String getHelp(String name) {
        return getHelp(name, defaultContext);
    }
    
    
    public interface IListener {
        public void onProcessStarted(Interpreter intr, Process process);
    }
    
    
    public static class Adapter implements IListener {
        @Override
        public void onProcessStarted(Interpreter intr, Process process) {
            // Do nothing.
        }
    }
}
