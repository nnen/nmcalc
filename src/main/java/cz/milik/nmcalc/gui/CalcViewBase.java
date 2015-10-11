/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ICalcValue;
import javax.swing.JPanel;

/**
 *
 * @author jan
 */
public class CalcViewBase extends JPanel implements ICalcValueView {

    private ICalcValue model;
    
    @Override
    public void setModel(ICalcValue model) {
        ICalcValue oldModel = this.model;
        this.model = model;
        if (oldModel != model) {
            onModelChanged(oldModel, model);
        }
    }
    
    protected void onModelChanged(ICalcValue oldModel, ICalcValue newModel) {
        
    }
    
    @Override
    public ICalcValue getModel() {
        return model;
    }
    
}
