/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ErrorValue;
import cz.milik.nmcalc.ICalcValue;
import cz.milik.nmcalc.Interpreter;
import cz.milik.nmcalc.gui.IInputView.IInputViewListener;
import cz.milik.nmcalc.peg.CalcParser;
import cz.milik.nmcalc.peg.ParseResult;

/**
 *
 * @author jan
 */
public class SimpleForm extends javax.swing.JFrame {

    private final CalcParser parser = new CalcParser();
    private final Interpreter interpreter = new Interpreter();
    
    private ICalcValue result;
    private ParseResult<ICalcValue> parsed;
    
    /**
     * Creates new form SimpleForm
     */
    public SimpleForm() {
        initComponents();
        
        inputPane.addListener(new IInputViewListener() {
            
            @Override
            public void onInput(IInputView view, String input) {
                evaluate();
            }
            
            @Override
            public void onInputCommited(IInputView view, String input) {
                commitInput();
                inputPane.clearError();
                inputPane.clearInput();
            }
            
        });
    }
    
    public boolean evaluate() {
        try {
            parsed = parser.parseList(inputPane.getInput());
            if (parsed.isSuccess()) {
                result = interpreter.eval(parsed.getValue());
                //outputPane.setModel(parsed.getValue());
                if (result.isError()) {
                    inputPane.setError((ErrorValue)result);
                    if (result instanceof ErrorValue) {
                        ErrorValue err = (ErrorValue)result;
                        if (err.getCause() != null) {
                            err.getCause().printStackTrace();
                        }
                    }
                } else {
                    outputPane.setModel(result);
                    inputPane.clearError();
                }
                return true;
            } else {
                if (parsed.getCause() != null) {
                    parsed.getCause().printStackTrace();
                }
                parsed.getContext().printStack(System.err);
                ErrorValue err = ErrorValue.formatted(parsed.getErrorMessage());
                inputPane.setError(err);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorValue err = new ErrorValue(e.getMessage(), e);
            result = err;
            outputPane.setModel(err);
            return true;
        }
    }
    
    public void commitInput() {
        if (evaluate()) {
            historyView.append(inputPane.getInput(), result);
        } else {
            historyView.append(inputPane.getInput(), parsed);
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        outputPane = new cz.milik.nmcalc.gui.SimpleCalcValueView();
        inputPane = new cz.milik.nmcalc.gui.SimpleCalcInput();
        historyView = new cz.milik.nmcalc.gui.HistoryView();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NMCalc");

        jSplitPane2.setDividerLocation(100);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(1.0);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

        outputPane.setMinimumSize(new java.awt.Dimension(41, 41));
        jSplitPane1.setRightComponent(outputPane);
        jSplitPane1.setLeftComponent(inputPane);

        jSplitPane2.setBottomComponent(jSplitPane1);
        jSplitPane2.setTopComponent(historyView);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SimpleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimpleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimpleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimpleForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimpleForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private cz.milik.nmcalc.gui.HistoryView historyView;
    private cz.milik.nmcalc.gui.SimpleCalcInput inputPane;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private cz.milik.nmcalc.gui.SimpleCalcValueView outputPane;
    // End of variables declaration//GEN-END:variables
}
