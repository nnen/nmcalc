/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ICalcValue;

/**
 *
 * @author jan
 */
public interface ICalcValueView {
    
    public void setModel(ICalcValue model);
    public ICalcValue getModel();
    
}
