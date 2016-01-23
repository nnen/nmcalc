/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.io.OutputStream;

/**
 *
 * @author jan
 */
public class ValueSerializer {
    
    public enum OpCodes {
        NOOP(""),
        
        PUSH_NOTHING("N"),
        PUSH_BOOLEAN("B"),
        PUSH_FLOAT("F"),
        PUSH_STRING("S"),
        PUSH_SYMBOL("Y"),
        
        MAKE_LIST("L"),
        MAKE_MAP("M"),
        ;
        
        public final String code;

        private OpCodes(String code) {
            this.code = code;
        }
        
    }
    
    
    private OutputStream output;

    public OutputStream getOutput() {
        return output;
    }
    
    public void setOutput(OutputStream output) {
        this.output = output;
    }
    
    
    
    
}
