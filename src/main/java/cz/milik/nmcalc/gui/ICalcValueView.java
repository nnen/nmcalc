/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.utils.IMonad;

/**
 *
 * @author jan
 */
public interface ICalcValueView {
    
    public void setModel(ICalcValue model);
    public IMonad<ICalcValue> getModel();
    
}
