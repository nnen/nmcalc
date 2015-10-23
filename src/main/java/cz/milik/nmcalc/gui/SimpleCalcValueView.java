/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.BuiltinCalcValue;
import cz.milik.nmcalc.ICalcValue;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.MarkupParser;
import cz.milik.nmcalc.utils.IMonad;
import cz.milik.nmcalc.utils.Monad;
import java.util.Optional;

/**
 *
 * @author jan
 */
public class SimpleCalcValueView extends javax.swing.JPanel {

    private boolean showingNull = true;

    public boolean isShowingNull() {
        return showingNull;
    }
    
    public void setShowingNull(boolean showingNull) {
        this.showingNull = showingNull;
    }
    
    
    private IMonad<ICalcValue> model = Monad.nothing();
    
    public IMonad<ICalcValue> getModel() {
        return model;
    }
    
    public void setModel(IMonad<ICalcValue> aModel) {
        IMonad<ICalcValue> oldModel = model;
        model = Monad.nonNull(aModel);
        onModelChanged(oldModel, model);
    }
    
    public void setModel(ICalcValue model) {
        setModel(Monad.maybe(model));
    }
    
    protected void onModelChanged(IMonad<ICalcValue> oldModel, IMonad<ICalcValue> newModel) {
        ICalcValue value = newModel.unwrap();
        
        if (value == null) {
            if (isShowingNull()) {
                textPane.setText("null");
            }
            textPane.setText("");
        } else {
            if (value instanceof BuiltinCalcValue) {
                Optional<String> help = value.getHelp();
                if (help.isPresent()) {
                    MarkupParser markup = new MarkupParser();
                    HyperTextPane.append(textPane.getStyledDocument(), markup.parse(help.get()));
                    return;
                }
            }
            textPane.setText(value.getRepr(ReprContext.getDefault()));
            textPane.updateSyntax();
        }
    }
    
    
    /**
     * Creates new form SimpleCalcValueView
     */
    public SimpleCalcValueView() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        textPane = new cz.milik.nmcalc.gui.HighlightedCodePane();

        setLayout(new java.awt.BorderLayout());

        textPane.setFont(GUIUtils.getCodeFont(24)
        );
        textPane.setText("Value");
        scrollPane.setViewportView(textPane);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private cz.milik.nmcalc.gui.HighlightedCodePane textPane;
    // End of variables declaration//GEN-END:variables
}
