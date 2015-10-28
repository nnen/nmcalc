/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.peg.CalcScanner;
import cz.milik.nmcalc.peg.IScanner;
import cz.milik.nmcalc.peg.ITokenSequence;
import cz.milik.nmcalc.peg.TokenList;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author jan
 */
public class HighlightedCodePane extends JTextPane {
    
    private MutableAttributeSet defaultStyle = new SimpleAttributeSet();
    private MutableAttributeSet literal = new SimpleAttributeSet();
    private MutableAttributeSet symbolLiteral = new SimpleAttributeSet();
    private MutableAttributeSet keyword = new SimpleAttributeSet();
    private MutableAttributeSet unknownToken = new SimpleAttributeSet();
    
    private ITokenSequence tokens;
    private IScanner scanner = new CalcScanner();
    
    {
        StyleConstants.setBold(defaultStyle, false);
        StyleConstants.setForeground(defaultStyle, Color.black);
        StyleConstants.setUnderline(defaultStyle, false);
        StyleConstants.setItalic(defaultStyle, false);
        
        StyleConstants.setForeground(literal, Color.red);
        
        StyleConstants.setBold(symbolLiteral, true);
        StyleConstants.setForeground(symbolLiteral, Color.magenta);
        
        StyleConstants.setBold(keyword, true);
        StyleConstants.setForeground(keyword, Color.blue);
        
        StyleConstants.setUnderline(unknownToken, true);
        StyleConstants.setItalic(unknownToken, true);
        StyleConstants.setForeground(unknownToken, Color.red);
        
        setFont(GUIUtils.getCodeFont());
    }
    
    public HighlightedCodePane() {
    }
    
    public HighlightedCodePane(StyledDocument doc) {
        super(doc);
    }
    
    
    public void clearSyntax() {
        StyledDocument doc = getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
    }
    
    public void setSyntax(ITokenSequence tokens) {
        clearSyntax();
        setSyntax(0, tokens);
    }
    
    public void setSyntax(int offset, ITokenSequence tokens) {
        StyledDocument doc = getStyledDocument();
        int i = 0;
        Token token = tokens.get(0);
        while (token != null) {
            if (token.getType().isKeyword) {
                doc.setCharacterAttributes(offset + token.getOffset(), token.getValue().length(), keyword, true);
            }
            switch (token.getType()) {
                case FLOAT:
                case HEX_LITERAL:
                case OCT_LITERAL:
                case BIN_LITERAL:
                case STRING:
                    doc.setCharacterAttributes(offset + token.getOffset(), token.getValue().length(), literal, true);
                    break;
                case SYMBOL:
                    doc.setCharacterAttributes(offset + token.getOffset(), token.getValue().length(), symbolLiteral, true);
                    break;
                case UNKNOWN:
                    doc.setCharacterAttributes(offset + token.getOffset(), token.getValue().length(), unknownToken, true);
                    break;
            }
            token = tokens.get(++i);
        }
    }
    
    public void updateSyntax() {
        if (!getText().isEmpty()) {
            scanner.reset(getText(), "<string>");
            tokens = new TokenList(scanner.readTokens());
            setSyntax(tokens);
        }
    }
    
    
    public void append(String text, ITokenSequence tokens) {
        StyledDocument doc = getStyledDocument();
        int offset = doc.getLength();
        
        try {
            doc.insertString(offset, text, null);
            setSyntax(offset, tokens);
        } catch (BadLocationException ex) {
            Logger.getLogger(HighlightedCodePane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void append(String text) {
        scanner.reset(text, "<string>");
        append(text, new TokenList(scanner.readTokens()));
    }
    
}
