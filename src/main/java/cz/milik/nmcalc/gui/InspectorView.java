/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.Interpreter;
import cz.milik.nmcalc.ReprContext;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author jan
 */
public class InspectorView extends CalcViewBase {
    
    public InspectorView() {
        initializeComponent();
    }
    
    public InspectorView(Interpreter interpreter, ReprContext reprContext) {
        super(interpreter, reprContext);
        initializeComponent();
    }
    
    protected void initializeComponent() {
        //setLayout(new GridBagLayout());
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 400));
        
        splitPane = new JSplitPane();
        splitPane.setBorder(null);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.5 * splitPane.getHeight());
        add(splitPane, BorderLayout.CENTER);
        
        upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        splitPane.setTopComponent(upperPanel);
        
        classLabel = new JLabel();
        classLabelConstraints = new GridBagConstraints();
        classLabelConstraints.fill = GridBagConstraints.BOTH;
        classLabelConstraints.weightx = 1.0f;
        classLabelConstraints.weighty = 0f;
        classLabelConstraints.gridx = 0;
        classLabelConstraints.gridx = 0;
        classLabelConstraints.anchor = GridBagConstraints.PAGE_START;
        upperPanel.add(classLabel, BorderLayout.PAGE_START);
        // add(classLabel, classLabelConstraints);
        
        innerViewConstraints = new GridBagConstraints();
        innerViewConstraints.fill = GridBagConstraints.BOTH;
        innerViewConstraints.weightx = 1.0f;
        innerViewConstraints.weighty = 0.1f;
        innerViewConstraints.gridx = 0;
        innerViewConstraints.gridy = 1;
        innerViewConstraints.anchor = GridBagConstraints.PAGE_START;
        
        helpScrollPane = new JScrollPane();
        
        helpPane = new JTextPane();
        helpPaneConstraints = new GridBagConstraints();
        helpPaneConstraints.fill = GridBagConstraints.BOTH;
        helpPaneConstraints.weightx = 1.0f;
        helpPaneConstraints.weighty = 1f;
        helpPaneConstraints.gridx = 0;
        helpPaneConstraints.gridy = 2;
        helpPaneConstraints.anchor = GridBagConstraints.PAGE_START | GridBagConstraints.PAGE_END | GridBagConstraints.LINE_START | GridBagConstraints.LINE_END;
        //add(helpPane, helpPaneConstraints);
        
        helpScrollPane.setViewportView(helpPane);
        splitPane.setBottomComponent(helpScrollPane);
        //add(helpScrollPane, helpPaneConstraints);
        
        setSize(300, 400);
    }
    
    
    private JSplitPane splitPane;
    
    private JPanel upperPanel;
    
    private JLabel classLabel;
    private GridBagConstraints classLabelConstraints;
    
    private JScrollPane helpScrollPane;
    private JTextPane helpPane;
    private GridBagConstraints helpPaneConstraints;
    
    private JComponent innerView;
    private GridBagConstraints innerViewConstraints;

    public JComponent getInnerView() {
        return innerView;
    }
    
    public void setInnerView(JComponent innerView) {
        if (this.innerView != null) {
            remove(this.innerView);
        }
        this.innerView = innerView;
        if (this.innerView != null) {
            upperPanel.add(this.innerView, BorderLayout.CENTER);
            //add(this.innerView, BorderLayout.CENTER);
            //add(this.innerView, innerViewConstraints);
        }
    }
    
    
    @Override
    protected void onModelChanged(ICalcValue oldModel, ICalcValue newModel) {
        super.onModelChanged(oldModel, newModel);
        
        if (newModel == null) {
            classLabel.setText("null");
            helpPane.setVisible(false);
        } else {
            String clsName = newModel.getClass().getSimpleName();
            if ((clsName == null) || clsName.isEmpty()) {
                clsName = newModel.getClass().getName();
            }
            classLabel.setText(clsName);
            
            Optional<String> help = newModel.getHelp();
            if (help.isPresent()) {
                try {
                    helpPane.getDocument().remove(0, helpPane.getDocument().getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(InspectorView.class.getName()).log(Level.SEVERE, null, ex);
                }
                HyperTextPane.appendMarkup(helpPane.getStyledDocument(), help.get());
                helpPane.setVisible(true);
            } else {
                helpPane.setVisible(false);
            }
        }
        
        JComponent view = GUIManager.getInstance().createView(
                this.getInterpreter(), this.getReprContext(), newModel);
        setInnerView(view);
    }

    
    
    
    /*
    @Override
    public Dimension getPreferredSize() {
        int width = 1;
        int height = 1;
        
        Dimension d1 = classLabel.getPreferredSize();
        Dimension d2 = helpPane.getPreferredSize();
        
        //width = Math.max(width, Math.max(d1.width, d2.width));
        height = Math.max(height, Math.max(d1.height, d2.height));
        
        if (this.innerView != null) {
            d1 = this.innerView.getPreferredSize();
            width = Math.max(width, d1.width);
            height = Math.min(height, d1.height);
        }
        
        return new Dimension(width, height);
    }
    */
    
}
