/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.gui.SimpleForm;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author jan
 */
public class Program {
    
    public static void Main(String[] argv)
    {
        final JFrame form = new SimpleForm();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                form.setVisible(true);
            }
        });
    }
    
}
