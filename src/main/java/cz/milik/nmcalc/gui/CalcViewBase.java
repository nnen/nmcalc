/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.Interpreter;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import javax.swing.JPanel;

/**
 *
 * @author jan
 */
public class CalcViewBase extends JPanel implements ICalcValueView {

    private IMonad<ICalcValue> model = Monad.nothing();
    
    @Override
    public void setModel(ICalcValue model) {
        IMonad<ICalcValue> oldModel = this.model;
        this.model = Monad.maybe(model);
        
        if (oldModel.unwrap() != model) {
            onModelChanged(oldModel.unwrap(), model);
        }
    }
    
    protected void onModelChanged(ICalcValue oldModel, ICalcValue newModel) {
        
    }
    
    @Override
    public IMonad<ICalcValue> getModel() {
        return model;
    }
    
    
    private Interpreter interpreter;

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    
    private ReprContext reprContext;

    public ReprContext getReprContext() {
        return reprContext;
    }

    public void setReprContext(ReprContext reprContext) {
        this.reprContext = reprContext;
    }
    
    
    public CalcViewBase(Interpreter interpreter, ReprContext reprContext) {
        this.interpreter = interpreter;
        this.reprContext = reprContext;
    }
    
    public CalcViewBase() {
    }
    
}
