/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ErrorValue;

/**
 *
 * @author jan
 */
public interface IInputView {
 
    public ErrorValue getError();
    public void setError(ErrorValue error);
    public void clearError();
    
    public String getInput();
    
    public void addListener(IInputViewListener listener);
    public void removeListener(IInputViewListener listener);
    
    public static interface IInputViewListener {
        
        public void onInput(IInputView view, String input);
        
    }
    
}
