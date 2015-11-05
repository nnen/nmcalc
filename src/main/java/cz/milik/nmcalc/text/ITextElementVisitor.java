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
    
    public R visitFragment(Text.Fragment element, C ctx);
    
    public R visitParagraph(Text.Paragraph element, C ctx);
    
    public R visitBlockQuote(Text.BlockQuote element, C ctx);
    
    public R visitCodeBlock(Text.CodeBlock element, C ctx);
    
    public R visitHeadline(Text.Headline element, C ctx);
    
    public R visitPlainText(Text.PlainText element, C ctx);
    
    public R visitMonospace(Text.Monospace element, C ctx);
    
    public R visitItalic(Text.Italic element, C ctx);
    
    public R visitBold(Text.Bold element, C ctx);
    
    public R visitSpan(Text.Span element, C ctx);
    
    public R visitCalcValue(Text.CalcValue element, C ctx);
    
    
    public R visitOther(ITextElement element, C ctx);

    public R visitBulletList(Text.BulletList element, C ctx);

    public R visitBulletPoint(Text.BulletPoint element, C ctx);
    
}
