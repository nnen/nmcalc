/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.Environment;
import javax.swing.JPanel;

/**
 *
 * @author jan
 */
public class EnvironmentView extends JPanel {

    private Environment environment;
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    
    public EnvironmentView() {
        initialize();
    }
    
    
    protected void initialize() {
        
    }
   
}
