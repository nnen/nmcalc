/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import cz.milik.nmcalc.utils.Utils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jan
 */
public class Environment implements Serializable {
    
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
    
    
    public List<String> getVariableNames() {
        return new ArrayList(variables.keySet());
    }
    
    
    public Environment() {
        this.parent = null;
        
        BuiltinCalcValue.initialize(this);
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
        
        ReprContext ctx = new ReprContext();
        
        out.printf("%d variables.", variables.size());
        out.println();
        for (String name : names) {
            out.print(name);
            out.print(": ");
            ICalcValue value = variables.get(name);
            out.println(value.getRepr(ctx));
        }
    }
    
    
    public void serialize(OutputStream stream) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(this);
        oos.close();
    }
    
    public void serialize(String fileName) throws IOException {
        Utils.createAndClose(this, () -> {
            return new FileOutputStream(fileName);
        }, out -> {
            serialize(out);
        });
    }
    
    public static Environment deserialize(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(stream);
        return (Environment)ois.readObject();
    }
    
    public static Environment deserialize(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileName);
            return deserialize(in);
        } finally {
            Utils.closeSilently(in);
        }
    }
}
