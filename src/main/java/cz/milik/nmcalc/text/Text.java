/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.ReprContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public abstract class Text implements IText {

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (ITextElement element : getChildren()) {
            sb.append(element.getText());
        }
        return sb.toString();
    }
    
    
    @Override
    public List<ITextElement> getChildren() {
        return Collections.emptyList();
    }
    
    @Override
    public void addChild(ITextElement child) {
        throw new UnsupportedOperationException();
    }

    
    @Override
    public String toHTML() {
        HTMLBuilder builder = new HTMLBuilder();
        return builder.toHtml(this);
    }
    
    
    @Override
    public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
        return visitor.visitOther(this, ctx);
    }
    
    
    public static PlainText plain(String value) {
        return new PlainText(value);
    }
    
    public static Monospace monospace(String value, Object... args) {
        if (args.length == 0) {
            return new Monospace(plain(value));
        }
        return new Monospace(plain(String.format(value, args)));
    }
    
    public static Monospace monospace() {
        return new Monospace();
    }
    
    public static Italic italic(ITextElement... children) {
        return new Italic(children);
    }
    
    public static Italic italic(String value) {
        return italic(plain(value));
    }
    
    public static Bold bold(String value) {
        return new Bold(plain(value));
    }
    
    public static Bold bold(ITextElement... children) {
        return new Bold(children);
    }
    
    public static Paragraph paragraph(ITextElement... elements) {
        Paragraph par = new Paragraph();
        for (ITextElement element : elements) {
            par.addChild(element);
        }
        return par;
    }
    
    public static Paragraph paragraph(String value) {
        return paragraph(plain(value));
    }
    
    public static BlockQuote blockQuote(ITextElement... elements) {
        return new BlockQuote(elements);
    }

    public static CodeBlock codeBlock() {
        return new CodeBlock(null);
    }
    
    public static CodeBlock codeBlock(String value) {
        return new CodeBlock(null, plain(value));
    }
    
    public static CodeBlock codeBlock(String value, String language) {
        if (value == null) {
            return new CodeBlock(language);
        } else {
            return new CodeBlock(language, plain(value));
        }
    }
    
    public static CodeBlock codeBlock(String language, ITextElement... children) {
        return new CodeBlock(language, children);
    }
    
    public static BulletList bulletList(ITextElement... children) {
        return new BulletList(children);
    }
    
    public static BulletPoint bulletPoint(ITextElement... children) {
        return new BulletPoint(children);
    }
    
    public static Table table() {
        return new Table();
    }
    
    public static TableRow tableRow() {
        return new TableRow();
    }
    
    public static TableCell tableCell() {
        return tableCell(false);
    }
    
    public static TableCell tableCell(boolean header) {
        return new TableCell(header);
    }
    
    public static TableCell tableCell(String fmt, Object... args) {
        return tableCell(false, fmt, args);
    }
    
    public static TableCell tableCell(boolean header, String fmt, Object... args) {
        return new TableCell(header, plain(String.format(fmt, args)));
    }
    
    public static Headline headline(String value, int level) {
        return new Headline(level, plain(value));
    }
    
    public static Span span(String spanType, String value) {
        return new Span(spanType, plain(value));
    }
    
    public static Span span(String spanType) {
        return new Span(spanType);
    }
    
    public static Link link(String link, Consumer<Link> action, ITextElement... children) {
        return new Link(link, action, children);
    }
    
    public static Link link(String link, Consumer<Link> action, String text) {
        return new Link(link, action, plain(text));
    }
    
    
    public static CalcValue value(ICalcValue value) {
        return new CalcValue(value, null);
    }
    
    
    public static abstract class ParentElement extends Text {
        private final List<ITextElement> children = new ArrayList();
        
        public ParentElement(ITextElement... children) {
            for (ITextElement child : children) {
                addChild(child);
            }
        }
        
        @Override
        public List<ITextElement> getChildren() {
            return Collections.unmodifiableList(children);
        }
        
        @Override
        public void addChild(ITextElement child) {
            children.add(child);
        }
    
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("{ ");
            for (ITextElement child : getChildren()) {
                sb.append(child.toString());
                sb.append(", ");
            }
            sb.append("}");
            return sb.toString();
        }
    }
    
    
    public static class Fragment extends ParentElement {
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitFragment(this, ctx);
        }
    }
    
    
    public static class Paragraph extends ParentElement {
        public Paragraph(ITextElement... children) {
            super(children);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitParagraph(this, ctx);
        }
    }
    
    
    public static class BlockQuote extends ParentElement {
        public BlockQuote(ITextElement... children) {
            super(children);
        }

        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitBlockQuote(this, ctx);
        }
    }
    
    
    public static class CodeBlock extends ParentElement {
        private String language;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
        
        public CodeBlock(String language, ITextElement... children) {
            super(children);
            this.language = language;
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitCodeBlock(this, ctx);
        }
    }
    
    
    public static class BulletList extends ParentElement {
        public BulletList(ITextElement... children) {
            super(children);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitBulletList(this, ctx);
        }
    }
    
    
    public static class BulletPoint extends ParentElement {
        public BulletPoint(ITextElement... children) {
            super(children);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitBulletPoint(this, ctx);
        }
    }
    
    
    public static class Table extends ParentElement {
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitTable(this, ctx);
        }
    }
    
    
    public static class TableRow extends ParentElement {
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitTableRow(this, ctx);
        }
    }
    
    
    public static class TableCell extends ParentElement {
        private boolean header = false;

        public boolean isHeader() {
            return header;
        }

        public void setHeader(boolean header) {
            this.header = header;
        }
        
        
        public TableCell(ITextElement... children) {
            super(children);
        }
        
        public TableCell(boolean header, ITextElement... children) {
            super(children);
            this.header = header;
        }
        
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitTableCell(this, ctx);
        }
    }
    
    
    public static class PlainText extends Text {
        private String text = "";

        public PlainText() {
        }

        public PlainText(String text) {
            this.text = text;
        }

        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("{\"");
            sb.append(text);
            sb.append("\"}");
            return sb.toString();
            //return "PlainText{" + "text=" + text + '}';
        }
        
        
        @Override
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitPlainText(this, ctx);
        }
    }
    
    
    public static class Bold extends ParentElement {
        public Bold(ITextElement... children) {
            super(children);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitBold(this, ctx);
        }
    }
    
    
    public static class Italic extends ParentElement {
        public Italic(ITextElement... children) {
            super(children);
        }

        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitItalic(this, ctx);
        }
    }
    
    
    public static class Monospace extends ParentElement {
        public Monospace(ITextElement... children) {
            super(children);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitMonospace(this, ctx);
        }    
    }
    
    
    public static class Headline extends ParentElement {
        private int level = 0;
        
        public int getLevel() {
            return level;
        }
        
        public void setLevel(int level) {
            this.level = level;
        }

        
        public Headline(int level, ITextElement... children) {
            super(children);
            this.level = level;
        }
        
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitHeadline(this, ctx);
        }
    }
    
    
    public static class Span extends ParentElement {
        private String spanType;

        public String getSpanType() {
            return spanType;
        }

        public void setSpanType(String spanType) {
            this.spanType = spanType;
        }
        
        public Span(ITextElement... children) {
            super(children);
        }
        
        public Span(String spanType, ITextElement... children) {
            super(children);
            this.spanType = spanType;
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitSpan(this, ctx);
        }
    }
    
    
    public abstract static class AbstractLink extends ParentElement {
        public AbstractLink(ITextElement... children) {
            super(children);
        }
        
        public abstract void activateLink();
    }
    
    
    public static class Link extends AbstractLink {
        public Link(String link, Consumer<Link> callback, ITextElement... children) {
            super(children);
            this.link = link;
            this.callback = callback;
        }
        
        
        private String link;

        public String getLink() {
            return link;
        }
        
        public void setLink(String link) {
            this.link = link;
        }
        
        
        public URL getUrl() {
            try {
                return new URL(getLink());
            } catch (MalformedURLException ex) {
                return null;
            }
        }
        
        
        private Consumer<Link> callback;
        
        public Consumer<Link> getCallback() {
            return callback;
        }
        
        public void setCallback(Consumer<Link> callback) {
            this.callback = callback;
        }
        
        
        @Override
        public void activateLink() {
            getCallback().accept(this);
        }

        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitLink(this, ctx);
        }
    }

    
    public static class CalcValue extends Text {
        private ICalcValue value;
        
        public ICalcValue getValue() {
            return value;
        }
        
        public void setValue(ICalcValue value) {
            this.value = value;
        }
        
        
        private ReprContext reprContext = ReprContext.getDefault();

        public ReprContext getReprContext() {
            return reprContext;
        }

        public void setReprContext(ReprContext reprContext) {
            this.reprContext = reprContext;
        }
        
        
        public CalcValue(ICalcValue value, ReprContext ctx) {
            setValue(value);
            if (ctx == null) {
                ctx = ReprContext.getDefault();
            }
            setReprContext(ctx);
        }
        
        
        @Override
        public String getText() {
            if (getValue() == null) {
                return "null";
            }
            return getValue().getRepr(getReprContext());
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitCalcValue(this, ctx);
        }
    }
}
