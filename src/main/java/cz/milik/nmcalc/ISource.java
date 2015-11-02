/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import java.io.Reader;

/**
 *
 * @author jan
 */
public interface ISource {
    
    public String getName();
    
    public String getContent(Context ctx) throws NMCalcException;
    public Reader getReader(Context ctx) throws NMCalcException;
    
}
