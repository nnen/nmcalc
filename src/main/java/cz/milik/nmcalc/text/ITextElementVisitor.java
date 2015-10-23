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
public interface ITextElementVisitor<C, R> {
    
    public R visitFragment(Text.Fragment fragment, C ctx);
    
    public R visitParagraph(Text.Paragraph paragraph, C ctx);
    
    public R visitCodeBlock(Text.CodeBlock codeBlock, C ctx);
    
    public R visitHeadline(Text.Headline headline, C ctx);
    
    public R visitPlainText(Text.PlainText plainText, C ctx);
    
    public R visitMonospace(Text.Monospace plainText, C ctx);
    
    public R visitItalic(Text.Italic italic, C ctx);
    
    public R visitBold(Text.Bold bold, C ctx);
    
    public R visitCalcValue(Text.CalcValue calcValue, C ctx);
    
    
    public R visitOther(ITextElement element, C ctx);

    public <C, R> R visitBulletList(Text.BulletList bulletList, C ctx);

    public <C, R> R visitBulletPoint(Text.BulletPoint bulletPoint, C ctx);
    
}
