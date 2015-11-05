/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.IPrintable;
import cz.milik.nmcalc.text.ITextElement;
import cz.milik.nmcalc.text.ITextElementVisitor;
import cz.milik.nmcalc.text.MarkupParser;
import cz.milik.nmcalc.text.Text;
import cz.milik.nmcalc.text.TextWriter;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author jan
 */
public class HyperTextPane extends JPanel {
    
    //public static final Font NORMAL_FONT = Font.getDefault();
    
    public static final MutableAttributeSet PLAIN_TEXT = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE1 = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE2 = new SimpleAttributeSet();
    public static final MutableAttributeSet HEADLINE3 = new SimpleAttributeSet();
    
    public static final MutableAttributeSet PARAGRAPH_BLOCK = new SimpleAttributeSet();
    
    public static final MutableAttributeSet BLOCK_QUOTE = new SimpleAttributeSet();
    
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
        
        StyleConstants.setLeftIndent(BLOCK_QUOTE, 12);
        
        StyleConstants.setFontFamily(CODE_BLOCK, "consolas");
        
        StyleConstants.setAlignment(CODE_BLOCK_BLOCK, StyleConstants.ALIGN_LEFT);
        StyleConstants.setSpaceBelow(CODE_BLOCK_BLOCK, 12);
        StyleConstants.setLeftIndent(CODE_BLOCK_BLOCK, 12);
        
        StyleConstants.setItalic(ITALIC, true);
        
        StyleConstants.setBold(BOLD, true);
    }
    
    
    public static int append(StyledDocument doc, String text, AttributeSet charAttrs, AttributeSet parAttrs) {
        int offset = doc.getLength();
        try {
            doc.insertString(offset, text, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        int length = doc.getLength() - offset;
        if (charAttrs != null) {
            doc.setCharacterAttributes(offset, length, charAttrs, true);
        }
        if (parAttrs != null) {
            doc.setCharacterAttributes(offset, length, parAttrs, true);
        }
        return length;
    }
    
    public static int append(StyledDocument doc, String text) {
        return append(doc, text, null, null);
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
            public Object visitBlockQuote(Text.BlockQuote blockQuote, Object ctx) {
                int offset = doc.getLength();
                for (ITextElement child : blockQuote.getChildren()) {
                    child.visit(this, ctx);
                }
                append(doc, "\n");
                int length = doc.getLength() - offset;
                doc.setParagraphAttributes(offset, length, BLOCK_QUOTE, true);
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
            public Object visitBulletList(Text.BulletList bulletList, Object ctx) {
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
            public Object visitBulletPoint(Text.BulletPoint bulletPoint, Object ctx) {
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
            public Object visitSpan(Text.Span element, Object ctx) {
                for (ITextElement child : element.getChildren()) {
                    child.visit(this, ctx);
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
    
    
    public String toHtml(ITextElement element) {
        HTMLBuilder builder = new HTMLBuilder();
        return builder.toHtml(element);
    }
    
    public Element getBody() {
        Element root = document.getDefaultRootElement();
        for (int i = 0; i < root.getElementCount(); i++) {
            Element e = root.getElement(i);
            if (e.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
                return e;
            }
        }
        return null;
    }
    
    public void append(ITextElement element) {
        Element body = getBody();
        if (body == null) {
            throw new IllegalStateException("Expected to find a BODY tag.");
        }
        try {
            document.insertBeforeEnd(body, toHtml(element));
        } catch (BadLocationException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        textPane.select(document.getLength(), document.getLength());
    }
    
    public void prepend(ITextElement element) {
        Element body = getBody();
        if (body == null) {
            throw new IllegalStateException("Expected to find a BODY tag.");
        }
        try {
            document.insertAfterStart(body, toHtml(element));
        } catch (BadLocationException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void append(IPrintable printable, ReprContext ctx) {
        append(TextWriter.print(printable, ctx));
    }
    
    public void prepend(IPrintable printable, ReprContext ctx) {
        prepend(TextWriter.print(printable, ctx));
    }
    
    public void prependMarkup(String markup) {
        MarkupParser parser = new MarkupParser();
        prepend(parser.parse(markup));
    }
    
    public void appendMarkup(String markup) {
        MarkupParser parser = new MarkupParser();
        append(parser.parse(markup));
    }
    
    
    private JScrollPane scrollPane;
    private JTextPane textPane;
    
    private HTMLEditorKit editorKit;
    private HTMLDocument document;
    
    public HyperTextPane() {
        initializeComponent();
    }
    
    protected void initializeComponent() {
        setLayout(new BorderLayout());
        
        scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);
        
        editorKit = new HTMLEditorKit();
        document = (HTMLDocument)editorKit.createDefaultDocument();
        
        document.getStyleSheet().addRule(
                "body { font-family: 'Helvetica Neue', Helvetica, 'Segoe UI', Arial, freesans, sans-serif; font-size: 10px; }\n" +
                "p { margin: 0px 0px 10px 0px; }"
        );
        document.getStyleSheet().addRule(
                "blockquote { background-color: #eeeeee; margin: 0px; padding-left: 12pt; border-left: solid 3px black; }"
        );
        document.getStyleSheet().addRule(
                "pre { font-family: Consolas, 'Liberation Mono', Menlo, Courier, monospace; background-color: #ffffcc; padding: 3pt; border-left: solid 12pt #ddddaa; margin: 0px 0px 10px 0px; }\n" +
                "tt { font-family: Consolas, 'Liberation Mono', Menlo, Courier, monospace; background-color: #dddddd; border-bottom-left-radius: 3px; border-bottom-right-radius: 3px; border-top-left-radius: 3px; border-top-right-radius: 3px; }\n" +
                ".result { background-color: #ffffff; border: none; }\n" +
                ".keyword { color: #0000ff; font-weight: bold; }\n" +
                ".name { font-style: italic; }\n" +
                ".literal { color: #cc0000; }"
        );
        document.getStyleSheet().addRule(
                "blockquote code pre { background-color: transparent; }"
        );
        
        textPane = new JTextPane();
        textPane.setEditorKit(editorKit);
        textPane.setDocument(document);
        scrollPane.setViewportView(textPane);
    }
    
    
    public static class HTMLBuilder implements ITextElementVisitor<StringBuilder, Object> {

        public String toHtml(ITextElement element) {
            StringBuilder sb = new StringBuilder();
            element.visit(this, sb);
            System.err.println("HTML: " + sb.toString());
            return sb.toString();
        }
        
        protected void visitChildren(ITextElement e, StringBuilder ctx) {
            for (ITextElement child : e.getChildren()) {
                child.visit(this, ctx);
            }
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
}

