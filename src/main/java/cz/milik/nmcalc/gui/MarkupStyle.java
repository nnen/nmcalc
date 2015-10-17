/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.text.ITextElement;
import cz.milik.nmcalc.text.ITextElementVisitor;
import cz.milik.nmcalc.text.Text;
import javax.swing.text.AttributeSet;

/**
 *
 * @author jan
 */
public class MarkupStyle {
    
    public AttributeSet getCharacterStyle(ITextElement element) {
        return element.visit(new ITextElementVisitor<MarkupStyle, AttributeSet>() {
            
            @Override
            public AttributeSet visitFragment(Text.Fragment fragment, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitParagraph(Text.Paragraph paragraph, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitCodeBlock(Text.CodeBlock codeBlock, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitHeadline(Text.Headline headline, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitPlainText(Text.PlainText plainText, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitMonospace(Text.Monospace plainText, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitItalic(Text.Italic italic, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitBold(Text.Bold bold, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitCalcValue(Text.CalcValue calcValue, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitOther(ITextElement element, MarkupStyle ctx) {
                return null;
            }

            @Override
            public <C, R> R visitBulletList(Text.BulletList bulletList, C ctx) {
                return null;
            }

            @Override
            public <C, R> R visitBulletPoint(Text.BulletPoint bulletPoint, C ctx) {
                return null;
            }
            
        }, this);
    }
    
    public AttributeSet getParagraphStyle(ITextElement element) {
        return element.visit(new ITextElementVisitor<MarkupStyle, AttributeSet>() {
            
            @Override
            public AttributeSet visitFragment(Text.Fragment fragment, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitParagraph(Text.Paragraph paragraph, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitCodeBlock(Text.CodeBlock codeBlock, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitHeadline(Text.Headline headline, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitPlainText(Text.PlainText plainText, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitMonospace(Text.Monospace plainText, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitItalic(Text.Italic italic, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitBold(Text.Bold bold, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitCalcValue(Text.CalcValue calcValue, MarkupStyle ctx) {
                return null;
            }

            @Override
            public AttributeSet visitOther(ITextElement element, MarkupStyle ctx) {
                return null;
            }

            @Override
            public <C, R> R visitBulletList(Text.BulletList bulletList, C ctx) {
                return null;
            }

            @Override
            public <C, R> R visitBulletPoint(Text.BulletPoint bulletPoint, C ctx) {
                return null;
            }
            
        }, this);
    }

}
