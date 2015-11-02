/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.loader;

import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ISource;
import cz.milik.nmcalc.NMCalcException;

/**
 *
 * @author jan
 */
public interface ICalcLoader {
    public ISource getSource(String name, Context ctx) throws NMCalcException;
}
