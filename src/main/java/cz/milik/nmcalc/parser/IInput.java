/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import java.util.Iterator;

/**
 *
 * @author jan
 */
public interface IInput extends Iterator<Character> {
    
    public char peek();
    
    public String getFileName();
    
}
