/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.text.ITextElement;
import cz.milik.nmcalc.text.ITextElementVisitor;
import cz.milik.nmcalc.text.MarkupParser;
import cz.milik.nmcalc.text.Text;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.text.Font;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author jan
 */
public class HyperTextPane extends JTextPane {
    
    //public static final Font NORMAL_FONT = Font.getDefault();
    
    public static final MutableAttributeSet PLAIN_TEXT = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE1 = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE2 = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE3 = new SimpleAttributeSet();
    
    public static final MutableAttributeSet PARAGRAPH_BLOCK = new SimpleAttributeSet();
    
    public static final MutableAttributeSet CODE_BLOCK = new SimpleAttributeSet();
    public static final MutableAttributeSet CODE_BLOCK_BLOCK = new SimpleAttributeSet();
    
    public static final MutableAttributeSet ITALIC = new SimpleAttributeSet();
    public static final MutableAttributeSet BOLD = new SimpleAttributeSet();
    
    static {
        StyleConstants.setFontFamily(PLAIN_TEXT, "sans serif");
        StyleConstants.setFontSize(PLAIN_TEXT, 12);
        
        StyleConstants.setBold(HEADLINE1, true);
        //StyleConstants.setUnderline(HEADLINE1, true);
        StyleConstants.setFontFamily(HEADLINE1, "sans serif");
        StyleConstants.setFontSize(HEADLINE1, 24);
        
        StyleConstants.setBold(HEADLINE2, true);
        //StyleConstants.setUnderline(HEADLINE2, true);
        StyleConstants.setFontFamily(HEADLINE2, "sans serif");
        StyleConstants.setFontSize(HEADLINE2, 18);
        
        //StyleConstants.setBold(HEADLINE3, true);
        StyleConstants.setUnderline(HEADLINE3, true);
        StyleConstants.setFontFamily(HEADLINE3, "sans serif");
        StyleConstants.setFontSize(HEADLINE3, 12);
        
        StyleConstants.setAlignment(PARAGRAPH_BLOCK, StyleConstants.ALIGN_JUSTIFIED);
        StyleConstants.setSpaceBelow(PARAGRAPH_BLOCK, 12);
        
        StyleConstants.setFontFamily(CODE_BLOCK, "consolas");
        
        StyleConstants.setAlignment(CODE_BLOCK_BLOCK, StyleConstants.ALIGN_LEFT);
        StyleConstants.setSpaceBelow(CODE_BLOCK_BLOCK, 12);
        StyleConstants.setLeftIndent(CODE_BLOCK_BLOCK, 12);
        
        StyleConstants.setItalic(ITALIC, true);
        
        StyleConstants.setBold(BOLD, true);
    }
    
    public static void append(StyledDocument doc, ITextElement element) {
        element.visit(new ITextElementVisitor<Object, Object>() {

            @Override
            public Object visitFragment(Text.Fragment fragment, Object ctx) {
                for (ITextElement child : fragment.getChildren()) {
                    child.visit(this, ctx);
                }
                return null;
            }
            
            @Override
            public Object visitParagraph(Text.Paragraph paragraph, Object ctx) {
                int offset = doc.getLength();
                for (ITextElement child : paragraph.getChildren()) {
                    child.visit(this, ctx);
                }
                try {
                    doc.insertString(doc.getLength(), "\n", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                int length = doc.getLength() - offset;
                doc.setParagraphAttributes(offset, length, PARAGRAPH_BLOCK, false);
                return null;
            }
            
            @Override
            public Object visitCodeBlock(Text.CodeBlock codeBlock, Object ctx) {
                int offset = doc.getLength();
                for (ITextElement child : codeBlock.getChildren()) {
                    child.visit(this, ctx);
                }
                try {
                    doc.insertString(doc.getLength(), "\n", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                int length = doc.getLength() - offset;
                doc.setCharacterAttributes(offset, length, CODE_BLOCK, true);
                doc.setParagraphAttributes(offset, length, CODE_BLOCK_BLOCK, false);
                return null;
            }
            
            @Override
            public <C, R> R visitBulletList(Text.BulletList bulletList, C ctx) {
                for (ITextElement item : bulletList.getChildren()) {
                    item.visit(this, ctx);
                }
                try {
                    doc.insertString(doc.getLength(), "\n", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            
            @Override
            public <C, R> R visitBulletPoint(Text.BulletPoint bulletPoint, C ctx) {
                try {
                    doc.insertString(doc.getLength(), "\u2022 ", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                for (ITextElement item : bulletPoint.getChildren()) {
                    item.visit(this, ctx);
                }
                try {
                    doc.insertString(doc.getLength(), "\n", null);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            
            @Override
            public Object visitHeadline(Text.Headline headline, Object ctx) {
                try {
                    int offset = doc.getLength();
                    doc.insertString(offset, headline.getText() + "\n", null);
                    AttributeSet attrs = HEADLINE3;
                    switch (headline.getLevel()) {
                        case 1:
                            attrs = HEADLINE1;
                            break;
                        case 2:
                            attrs = HEADLINE2;
                            break;
                    }
                    doc.setCharacterAttributes(offset, headline.getText().length(), attrs, false);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            public Object visitPlainText(Text.PlainText plainText, Object ctx) {
                try {
                    int offset = doc.getLength();
                    doc.insertString(doc.getLength(), plainText.getText(), null);
                    doc.setCharacterAttributes(offset, plainText.getText().length(), PLAIN_TEXT, false);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            public Object visitMonospace(Text.Monospace plainText, Object ctx) {
                int offset = doc.getLength();
                try {
                    doc.insertString(offset, plainText.getText(), null);
                    doc.setCharacterAttributes(offset, plainText.getText().length(), CODE_BLOCK, true);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            public Object visitItalic(Text.Italic italic, Object ctx) {
                int offset = doc.getLength();
                try {
                    doc.insertString(offset, italic.getText(), null);
                    doc.setCharacterAttributes(offset, italic.getText().length(), ITALIC, false);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            public Object visitBold(Text.Bold bold, Object ctx) {
                int offset = doc.getLength();
                try {
                    doc.insertString(offset, bold.getText(), null);
                    doc.setCharacterAttributes(offset, bold.getText().length(), BOLD, false);
                } catch (BadLocationException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            
            @Override
            public Object visitCalcValue(Text.CalcValue calcValue, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
            @Override
            public Object visitOther(ITextElement element, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        }, doc);
    }
    
    public static void appendMarkup(StyledDocument doc, String markup) {
        MarkupParser parser = new MarkupParser();
        ITextElement parsed = parser.parse(markup);
        append(doc, parsed);
    }
    
}

