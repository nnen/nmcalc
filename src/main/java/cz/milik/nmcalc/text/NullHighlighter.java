/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

/**
 *
 * @author jan
 */
public class NullHighlighter implements IHighlighter {

    @Override
    public void highlight(TextWriter tw, String input) {
        tw.plain(input);
    }
    
}
