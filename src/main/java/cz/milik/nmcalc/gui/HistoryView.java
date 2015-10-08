/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ICalcValue;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.peg.ParseResult;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author jan
 */
public class HistoryView extends javax.swing.JPanel {

    /**
     * Creates new form HistoryView
     */
    public HistoryView() {
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
        outputPane = new javax.swing.JTextPane();

        setLayout(new java.awt.BorderLayout());

        outputPane.setEditable(false);
        outputPane.setFont(GUIUtils.getCodeFont());
        scrollPane.setViewportView(outputPane);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    
    public void append(String str) {
        Document doc = outputPane.getDocument();
        try {
            doc.insertString(doc.getLength(), str, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(HistoryView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void append(ICalcValue expr, ICalcValue value) {
        append(expr.getRepr(ReprContext.getDefault()), value);
    }
    
    public void append(String expr, ICalcValue value) {
        append(expr, value.getRepr(ReprContext.getDefault()));
    }
    
    public void append(String expr, String value) {
        append(expr + " => " + value + "\n");
    }
    
    public void append(String expr, ParseResult<ICalcValue> parsed) {
        if (parsed.isSuccess()) {
            append(expr, parsed.getValue());
        } else {
            append(expr, parsed.getErrorMessage());
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane outputPane;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
