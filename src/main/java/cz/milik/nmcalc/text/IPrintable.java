/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.ReprContext;

/**
 *
 * @author jan
 */
public interface IPrintable {

    public void print(TextWriter out, ReprContext ctx);
    public void printDebug(TextWriter out, ReprContext ctx);
    
}
