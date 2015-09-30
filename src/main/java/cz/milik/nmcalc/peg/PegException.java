/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

/**
 *
 * @author jan
 */
public class PegException extends Exception {

    public PegException() {
    }

    public PegException(String message) {
        super(message);
    }

    public PegException(String message, Throwable cause) {
        super(message, cause);
    }

    public PegException(Throwable cause) {
        super(cause);
    }

    public PegException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
