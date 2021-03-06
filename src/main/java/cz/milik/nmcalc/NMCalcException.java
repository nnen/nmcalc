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
public class NMCalcException extends Exception {

    private final Context context;

    public Context getContext() {
        return context;
    }
    
    
    public NMCalcException() {
        context = null;
    }
    
    public NMCalcException(String message) {
        super(message);
        context = null;
    }
    
    public NMCalcException(String message, Context aContext) {
        super(message);
        context = aContext;
    }

    public NMCalcException(String message, Throwable cause) {
        super(message, cause);
        context = null;
    }
    
    public NMCalcException(String message, Throwable cause, Context aContext) {
        super(message, cause);
        context = aContext;
    }

    public NMCalcException(Throwable cause) {
        super(cause);
        context = null;
    }

    public NMCalcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        context = null;
    }
    
}
