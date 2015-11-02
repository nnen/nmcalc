/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author jan
 */
public class FilterListBox extends JPanel {
    
    private JScrollPane listScroller;
    private JList list;
    private JTextArea filterBox;
    
    public FilterListBox() {
        initializeComponent();
    }
    
    protected void initializeComponent() {
        setLayout(new BorderLayout());
        
        listScroller = new JScrollPane();
        add(listScroller, BorderLayout.CENTER);
        
        list = new JList();
        listScroller.setViewportView(list);
        //add(list, BorderLayout.CENTER);
        
        filterBox = new JTextArea();
        filterBox.setVisible(false);
        add(filterBox, BorderLayout.PAGE_END);
    }
    
}
