/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author jan
 */
public final class GUIUtils {
    
    public static Font getCodeFont() {
        return getCodeFont(12);
    }
    
    public static Font getCodeFont(float size) {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font font : e.getAllFonts()) {
            if ("Consolas".equals(font.getName())) {
                return font.deriveFont(size);
            }
        }
        return new Font(Font.MONOSPACED, Font.PLAIN, (int)size);
    }
    
}
