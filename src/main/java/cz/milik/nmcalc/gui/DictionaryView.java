/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.values.MapValue;
import cz.milik.nmcalc.utils.Pair;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author jan
 */
public class DictionaryView extends CalcViewBase {

    public DictionaryView() {
        initializeComponent();
    }
    
    protected void initializeComponent() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        
        setFont(GUIUtils.getCodeFont(10));
        //GridBagConstraints c = new GridBagConstraints();
    }
    
    
    private final List<JComponent> itemComponents = new ArrayList();
    
    @Override
    protected void onModelChanged(ICalcValue oldModel, ICalcValue newModel) {
        super.onModelChanged(oldModel, newModel); //To change body of generated methods, choose Tools | Templates.
        
        for (JComponent comp : itemComponents) {
            remove(comp);
        }
        
        List<Pair<ICalcValue, ICalcValue>> pairs = getPairs();
        
        
        GridBagConstraints c = new GridBagConstraints();
        int row = 0;
        
        for (Pair<ICalcValue, ICalcValue> pair : pairs) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.weightx = 0f;
            c.gridx = 0;
            c.gridy = row;
            
            SimpleCalcValueView key = new SimpleCalcValueView();
            key.setFont(getFont());
            key.setModel(pair.getFirst());
            add(key, c);
            
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.weightx = 1.0f;
            c.gridx = 1;
            c.gridy = row;
            
            SimpleCalcValueView value = new SimpleCalcValueView();
            value.setFont(getFont());
            value.setModel(pair.getSecond());
            add(value, c);
            
            row++;
        }
    }
    
    
    protected List<Pair<ICalcValue, ICalcValue>> getPairs() {
        ICalcValue model = this.getModel().unwrap();
        if ((model != null) && (model instanceof MapValue)) {
            MapValue mapValue = (MapValue)model;
            if (mapValue.length() == 0) {
                return Collections.emptyList();
            }
            
            List<ICalcValue> keys = new ArrayList(mapValue.getKeys());
            List<Pair<ICalcValue, ICalcValue>> pairs = new ArrayList();
            
            for (ICalcValue key : keys) {
                pairs.add(new Pair(key, mapValue.getItem(key)));
            }
            
            return pairs;
        }
        
        return Collections.emptyList();
    }
    
}
