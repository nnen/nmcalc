/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

/**
 *
 * @author jan
 */
public class Session {
    private final Interpreter interpreter;

    public Interpreter getInterpreter() {
        return interpreter;
    }

    
    public Session(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    
    public interface IListener {
        public void onStartNewSession();
    }
    
    
    public static class Adapter implements IListener {
        @Override
        public void onStartNewSession() {
            // Do nothing.
        }
    }
}
