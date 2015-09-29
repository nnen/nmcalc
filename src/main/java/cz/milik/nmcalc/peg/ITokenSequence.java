/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;

/**
 *
 * @author jan
 */
public interface ITokenSequence {
 
    public boolean isEmpty();
    
    public Token get(int index);
    
    public ITokenSequence advance(int amount);
    
}
