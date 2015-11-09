/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.peg.CalcScanner;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jan
 */
public class TokenizerHighlighter implements IHighlighter {
    
    private static final Set<String> KEYWORDS = new HashSet<String>();
    
    static {
        KEYWORDS.add("def");
        KEYWORDS.add("if");
        KEYWORDS.add("then");
        KEYWORDS.add("else");
        KEYWORDS.add("match");
        KEYWORDS.add("case");
        KEYWORDS.add("do");
    }
    
    public void highlight(TextWriter tw, String input) {
        CalcScanner scanner = new CalcScanner();
        scanner.reset(input, "<string>");
        int offset = 0;
        
        for (Token t : scanner.readTokens()) {
            if (t.getOffset() > offset) {
                tw.plain(input.substring(offset, t.getOffset()));
            }
            
            switch (t.getType()) {
                case FLOAT:
                case HEX_LITERAL:
                case BIN_LITERAL:
                case OCT_LITERAL:
                case STRING:
                case KW_TRUE:
                case KW_FALSE:
                case KW_NOTHING:
                    tw.span("literal", t.getValue());
                    break;
                
                case IDENTIFIER:
                    if (KEYWORDS.contains(t.getValue().toLowerCase())) {
                        tw.span("keyword", t.getValue());
                    } else {
                        tw.plain(t.getValue());
                    }
                    break;
                    
                default:
                    tw.plain(t.getValue());
                    break;
            }
            
            offset = t.getOffset() + t.getValue().length();
        }
        
        if (offset < input.length()) {
            tw.plain(input.substring(offset, input.length()));
        }
    }
    
}
