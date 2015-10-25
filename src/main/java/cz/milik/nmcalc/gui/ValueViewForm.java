/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ICalcValue;
import cz.milik.nmcalc.utils.IMonad;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author jan
 */
public class ValueViewForm extends JFrame {
    
    private JComponent component;
    
    public JComponent getComponent() {
        return component;
    }
    
    public void setComponent(JComponent component) {
        if (this.component == component) {
            return;
        }
        
        if (this.component != null) {
            remove(component);
        }
        
        this.component = component;
        if (component instanceof ICalcValueView) {
            setValueView((ICalcValueView)component);
        }
        
        if (this.component != null) {
            add(component, BorderLayout.CENTER);
            component.setVisible(true);
            this.getContentPane().setSize(component.getPreferredSize());
            pack();
            /*
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getContentPane().setSize(component.getPreferredSize());
                }
            });
                    */
            //this.setSize(component.getPreferredSize());
        }
    }
    
    
    private ICalcValueView valueView;

    public ICalcValueView getValueView() {
        return valueView;
    }
    
    protected void setValueView(ICalcValueView valueView) {
        this.valueView = valueView;
    }
    
    
    public IMonad<ICalcValue> getModel() {
        return valueView.getModel();
    }
    
    public void setModel(ICalcValue model) {
        valueView.setModel(model);
    }

    
    public ValueViewForm() throws HeadlessException {
        setLayout(new BorderLayout());
    }
}
