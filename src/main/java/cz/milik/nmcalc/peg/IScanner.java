/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import java.util.List;

/**
 *
 * @author jan
 */
public interface IScanner {
    
    public void reset(CharSequence input, String fileName);
    
    public List<Token> readTokens();
    
}
