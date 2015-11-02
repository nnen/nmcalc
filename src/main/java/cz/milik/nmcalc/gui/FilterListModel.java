/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import java.util.Objects;
import javax.swing.AbstractListModel;

/**
 *
 * @author jan
 */
public abstract class FilterListModel extends AbstractListModel {
    
    private String filterString;

    public String getFilterString() {
        return filterString;
    }

    public void setFilterString(String filterString) {
        String oldFilter = this.filterString;
        this.filterString = filterString;
        if (!Objects.equals(oldFilter, filterString)) {
            onFilterStringChanged(oldFilter, filterString);
        }
    }
    
    protected void onFilterStringChanged(String oldFilter, String newFilter) {
        
    }
    
}
