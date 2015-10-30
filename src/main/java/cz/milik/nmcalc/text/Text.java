/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.ReprContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
    public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
        return visitor.visitOther(this, ctx);
    }
    
    
    public static PlainText plain(String value) {
        return new PlainText(value);
    }
    
    public static Monospace monospace(String value, Object... args) {
        if (args.length == 0) {
            return new Monospace(value);
        }
        return new Monospace(String.format(value, args));
    }
    
    public static Italic italic(String value) {
        return new Italic(value);
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
    
    public static CodeBlock codeBlock(String value) {
        CodeBlock result = new CodeBlock();
        result.addChild(plain(value));
        return result;
    }
    
    public static BulletList bulletList(ITextElement... children) {
        return new BulletList(children);
    }
    
    public static BulletPoint bulletPoint(ITextElement... children) {
        return new BulletPoint(children);
    }
    
    public static Headline headline(String value, int level) {
        return new Headline(value, level);
    }
    
    public static Link link(String text, Consumer<Link> action) {
        return new Link(text, action);
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
    
    
    public static class Italic extends PlainText {
        public Italic() {
        }

        public Italic(String text) {
            super(text);
        }
    }
    
    
    public static class Monospace extends PlainText {
        public Monospace() {
        }

        public Monospace(String text) {
            super(text);
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitMonospace(this, ctx);
        }    
    }
    
    
    public static class Headline extends PlainText {
        private int level = 0;
        
        public int getLevel() {
            return level;
        }
        
        public void setLevel(int level) {
            this.level = level;
        }

        
        public Headline(String text, int level) {
            super(text);
            this.level = level;
        }
        
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitHeadline(this, ctx);
        }
    }
    
    
    public abstract static class AbstractLink extends PlainText {
        public AbstractLink() {
        }

        public AbstractLink(String text) {
            super(text);
        }
        
        public abstract void activateLink();
    }
    
    
    public static class Link extends AbstractLink {
        public Link(String text, Consumer<Link> callback) {
            super(text);
            this.callback = callback;
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
