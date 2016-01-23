/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import java.util.List;

/**
 *
 * @author jan
 */
public interface ITextElement {
    public boolean isParityChange();
    
    public String getText();
    
    public List<ITextElement> getChildren();
    public void addChild(ITextElement child);
    
    default ITextElement getLastChild() {
        return null;
    }
    
    public String toHTML();
    
    public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx);
}
