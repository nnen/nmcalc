/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.Interpreter;
import cz.milik.nmcalc.values.MapValue;
import cz.milik.nmcalc.ReprContext;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author jan
 */
public class GUIManager {
    
    public boolean showValue(Interpreter interpreter, ReprContext ctx, ICalcValue value) {
        final ReprContext reprCtx = (ctx == null) ? ReprContext.getDefault() : ctx;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //JComponent component = createView(interpreter, reprCtx, value);
                InspectorView component = new InspectorView(interpreter, reprCtx);
                component.setModel(value);
                
                ValueViewForm frame = new ValueViewForm();
                frame.setComponent(component);
                
                //frame.setFocusableWindowState(false);
                frame.setVisible(true);
            }
        });
        
        return false;
    }
    
    
    public JComponent createView(Interpreter interpreter, ReprContext ctx, ICalcValue value) {
        if (value instanceof MapValue) {
            DictionaryView view = new DictionaryView();
            view.setModel(value);
            return view;
        }
        
        SimpleCalcValueView view = new SimpleCalcValueView();
        view.setModel(value);
        return view;
    }
    
    
    private static GUIManager instance;
    
    public static GUIManager getInstance() {
        if (instance == null) {
            instance = new GUIManager();
        }
        return instance;
    }
    
}
