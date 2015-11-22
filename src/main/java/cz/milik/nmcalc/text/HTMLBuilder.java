/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import com.numericalmethod.suanshu.misc.StringUtils;
import java.util.Objects;
import java.util.Stack;

/**
 *
 * @author jan
 */
public class HTMLBuilder implements ITextElementVisitor<StringBuilder, Object> {
    
    private Stack<Boolean> oddEvenStack = new Stack();
    
    
    public IHighlighter getHighlighter(String language) {
        if (Objects.equals(language, "nmcalc")) {
            return new TokenizerHighlighter();
        } else {
            return new NullHighlighter();
        }
    }
    
    
    {
        startBlock();
    }
    
    protected void startBlock() {
        oddEvenStack.push(true);
    }
    
    protected void endBlock() {
        oddEvenStack.pop();
    }
    
    protected boolean isOdd() {
        if (oddEvenStack.isEmpty()) {
            return false;
        }
        return oddEvenStack.peek();
    }
    
    protected void switchOddEven() {
        boolean top = oddEvenStack.pop();
        oddEvenStack.push(top ^ true);
    }
    
    
    public String toHtml(ITextElement element) {
        StringBuilder sb = new StringBuilder();
        element.visit(this, sb);
        return sb.toString();
    }

    protected void visitChildren(ITextElement e, StringBuilder ctx) {
        startBlock();
        for (ITextElement child : e.getChildren()) {
            child.visit(this, ctx);
            switchOddEven();
        }
        endBlock();
    }

    protected Object block(ITextElement e, StringBuilder sb, String tagName, String... otherTagNames) {
        sb.append("<").append(tagName).append(">");
        for (int i = 0; i < otherTagNames.length; i++) {
            sb.append("<").append(otherTagNames[i]).append(">");
        }
        visitChildren(e, sb);
        for (int i = otherTagNames.length - 1; i >= 0; i--) {
            sb.append("</").append(otherTagNames[i]).append(">");
        }
        sb.append("</").append(tagName).append(">");
        return null;
    }
    
    protected Object blockWithClass(ITextElement e, StringBuilder sb, String tagName, String cssCls) {
        if (StringUtils.isNullOrEmpty(cssCls)) {
            return block(e, sb, tagName);
        }
        
        sb.append("<").append(tagName).append(" class=\"").append(cssCls).append("\">");
        visitChildren(e, sb);
        sb.append("</").append(tagName).append(">");
        
        return null;
    }
    
    protected void start(StringBuilder sb, String tagName, String clsName) {
        sb.append("<");
        sb.append(tagName);
        if (clsName != null) {
            sb.append(" class=\"");
            sb.append(clsName);
            sb.append("\"");
        }
        sb.append(">");
    }

    protected void end(StringBuilder sb, String tagName) {
        sb.append("</");
        sb.append(tagName);
        sb.append(">");
    }

    @Override
    public Object visitFragment(Text.Fragment fragment, StringBuilder ctx) {
        visitChildren(fragment, ctx);
        return null;
    }

    @Override
    public Object visitParagraph(Text.Paragraph paragraph, StringBuilder ctx) {
        return block(paragraph, ctx, "p");
    }

    @Override
    public Object visitBlockQuote(Text.BlockQuote blockQuote, StringBuilder ctx) {
        return block(blockQuote, ctx, "blockquote");
    }

    @Override
    public Object visitCodeBlock(Text.CodeBlock codeBlock, StringBuilder ctx) {
        return block(codeBlock, ctx, "code", "pre");
    }

    @Override
    public Object visitTable(Text.Table element, StringBuilder ctx) {
        return block(element, ctx, "table");
    }
    
    @Override
    public Object visitTableRow(Text.TableRow element, StringBuilder ctx) {
        if (isOdd()) {
            return blockWithClass(element, ctx, "tr", "odd_row");
        }
        return blockWithClass(element, ctx, "tr", "even_row");
    }

    @Override
    public Object visitTableCell(Text.TableCell element, StringBuilder ctx) {
        if (element.isHeader()) {
            return block(element, ctx, "th");
        } else {
            return block(element, ctx, "td");
        }
    }

    @Override
    public Object visitHeadline(Text.Headline headline, StringBuilder ctx) {
        return block(headline, ctx, "h" + Integer.toString(headline.getLevel()));
    }

    @Override
    public Object visitPlainText(Text.PlainText plainText, StringBuilder ctx) {
        ctx.append(plainText.getText().replace("<", "&lt;"));
        return null;
    }

    @Override
    public Object visitMonospace(Text.Monospace plainText, StringBuilder ctx) {
        return block(plainText, ctx, "tt");
    }

    @Override
    public Object visitItalic(Text.Italic italic, StringBuilder ctx) {
        return block(italic, ctx, "i");
    }

    @Override
    public Object visitBold(Text.Bold bold, StringBuilder ctx) {
        return block(bold, ctx, "b");
    }

    @Override
    public Object visitSpan(Text.Span element, StringBuilder ctx) {
        start(ctx, "span", element.getSpanType());
        visitChildren(element, ctx);
        end(ctx, "span");
        return null;
    }

    @Override
    public Object visitLink(Text.Link element, StringBuilder ctx) {
        ctx.append("<a href=\"");
        ctx.append(element.getLink());
        ctx.append("\">");
        
        visitChildren(element, ctx);
        
        ctx.append("</a>");
        return null;
    }

    @Override
    public Object visitCalcValue(Text.CalcValue calcValue, StringBuilder ctx) {
        ctx.append("<tt class=\"result\">");
        TextWriter.print(
                calcValue.getValue(),
                calcValue.getReprContext()
        ).visit(this, ctx);
        ctx.append("</tt>");
        return null;
    }

    @Override
    public Object visitOther(ITextElement element, StringBuilder ctx) {
        ctx.append(element.getText());
        return null;
    }

    @Override
    public Object visitBulletList(Text.BulletList bulletList, StringBuilder ctx) {
        return block(bulletList, ctx, "ul");
    }

    @Override
    public Object visitBulletPoint(Text.BulletPoint bulletPoint, StringBuilder ctx) {
        return block(bulletPoint, ctx, "li");
    }

}
