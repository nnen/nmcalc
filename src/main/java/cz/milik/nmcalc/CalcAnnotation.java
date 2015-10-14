/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.parser.Token;

/**
 *
 * @author jan
 */
public class CalcAnnotation implements ICalcValueAnnotation {
    
    public static SourceAnnotation source(int offset, int length) {
        return new SourceAnnotation(offset, length);
    }
    
    public static SourceAnnotation source(Token token) {
        return source(token, token);
    }
    
    public static SourceAnnotation source(Token first, Token last) {
        return new SourceAnnotation(first.getOffset(), last.getOffset() + last.getValue().length());
    }
    
    public static HelpAnnotation help(String value) {
        return new HelpAnnotation(value);
    }
    
    public static IsHelp isHelp() { return new IsHelp(); }
    
    
    public static class SourceAnnotation extends CalcAnnotation {
        
        private final int offset;
        private final int length;

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public SourceAnnotation(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
        
    }
    
    
    public static class HelpAnnotation extends CalcAnnotation {
        
        private String help;
        
        public String getHelp() {
            return help;
        }
        
        public void setHelp(String help) {
            this.help = help;
        }
        
        public HelpAnnotation(String help) {
            this.help = help;
        }
        
    }
    
    
    public static class IsHelp extends CalcAnnotation {
        
    }
    
}
