/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.ICalcValue;
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
    
    
    public static abstract class ParentElement extends Text {
        private final List<ITextElement> children = new ArrayList();
        
        @Override
        public List<ITextElement> getChildren() {
            return Collections.unmodifiableList(children);
        }
        
        @Override
        public void addChild(ITextElement child) {
            children.add(child);
        }
    }
    
    
    public static class Paragraph extends ParentElement {
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitParagraph(this, ctx);
        }
    }
    
    
    public static class PlainText extends Text {
        private String text = "";
        
        @Override
        public String getText() {
            return super.getText(); //To change body of generated methods, choose Tools | Templates.
        }
        
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitPlainText(this, ctx);
        }
    }
    
    
    public static class Bold extends PlainText {
        
    }
    
    
    public static class Italic extends PlainText {
        
    }
    
    
    public static class Headline extends PlainText {
        private int level = 0;
        
        public int getLevel() {
            return level;
        }
        
        public void setLevel(int level) {
            this.level = level;
        }
        
        @Override
        public <C, R> R visit(ITextElementVisitor<C, R> visitor, C ctx) {
            return visitor.visitHeadline(this, ctx);
        }
    }
    
    
    public abstract static class AbstractLink extends PlainText {
        public abstract void activateLink();
    }
    
    
    public static class Link extends AbstractLink {
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
