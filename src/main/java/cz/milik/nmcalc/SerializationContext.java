/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 *
 * @author jan
 */
public class SerializationContext {
    
    private OutputStream outStream;

    public OutputStream getOutStream() {
        return outStream;
    }
    
    public void setOutStream(OutputStream outStream) {
        this.outStream = outStream;
    }
    
    
    private InputStream inStream;

    public InputStream getInStream() {
        return inStream;
    }
    
    public void setInStream(InputStream inStream) {
        this.inStream = inStream;
    }
    
    
    public void write(String value) {
        
    }
    
    public void write(BigDecimal value) {
        
    }
    
    public void write(boolean value) {
        
    }
    
    
    public static enum Types {
        BOOL,
        FLOAT,
        STRING,
        LIST,
        DICT,
        SOME,
        NOTHING
    }
}
