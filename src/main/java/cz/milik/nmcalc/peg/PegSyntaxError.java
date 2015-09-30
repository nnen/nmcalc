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
public class PegSyntaxError extends PegException {

    private final PegParser<?> parser;
    
    public PegParser<?> getParser() { return parser; }
    
    public PegSyntaxError(PegParser<?> parser) {
        this.parser = parser;
    }

    public PegSyntaxError(PegParser<?> parser, String message) {
        super(message);
        this.parser = parser;
    }

    public PegSyntaxError(PegParser<?> parser, String message, Throwable cause) {
        super(message, cause);
        this.parser = parser;
    }

    public PegSyntaxError(PegParser<?> parser, Throwable cause) {
        super(cause);
        this.parser = parser;
    }
    
}
