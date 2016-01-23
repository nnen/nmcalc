/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.gui;

import cz.milik.nmcalc.IReprContextProvider;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.IPrintable;
import cz.milik.nmcalc.text.ITextElement;
import cz.milik.nmcalc.text.ITextElementVisitor;
import cz.milik.nmcalc.text.MarkupParser;
import cz.milik.nmcalc.text.Text;
import cz.milik.nmcalc.text.TextWriter;
import cz.milik.nmcalc.utils.ListenerCollection;
import cz.milik.nmcalc.utils.Utils;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    
    
    private IReprContextProvider reprContextProvider = ReprContext.getDefault();

    public IReprContextProvider getReprContextProvider() {
        return reprContextProvider;
    }

    public void setReprContextProvider(IReprContextProvider reprContextProvider) {
        this.reprContextProvider = reprContextProvider;
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
            public Object visitTable(Text.Table element, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Object visitTableRow(Text.TableRow element, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Object visitTableCell(Text.TableCell element, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            public Object visitLink(Text.Link element, Object ctx) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        return element.toHTML();
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
                "th { padding: 2pt 6pt 2pt 6pt; background-color: #999999; color: white; }\n" +
                "td { padding: 2pt 6pt 2pt 6pt; vertical-align: top; }\n" +
                "td.number_cell { text-align: right; }\n" +
                "tr.odd_row td { background-color: #dddddd; }"
        );
        document.getStyleSheet().addRule(
                "pre { font-family: Consolas, 'Liberation Mono', Menlo, Courier, monospace; background-color: #ffffcc; padding: 3pt; border-left: solid 12pt #ddddaa; margin: 0px 0px 10px 0px; }\n" +
                "tt { font-family: Consolas, 'Liberation Mono', Menlo, Courier, monospace; background-color: #eeeeee; border-bottom-left-radius: 3px; border-bottom-right-radius: 3px; border-top-left-radius: 3px; border-top-right-radius: 3px; }\n" +
                ".result { background-color: #ffffff; border: none; }\n" +
                ".keyword { color: #0000ff; font-weight: bold; }\n" +
                ".func_name { font-weight: bold; }\n" +
                ".arg_name { font-style: italic; }\n" +
                ".literal { color: #cc0000; }\n" +
                ".builtin { color: #cc00cc; }\n"
        );
        document.getStyleSheet().addRule(
                "blockquote code pre { background-color: transparent; }"
        );
        
        textPane = new JTextPane();
        textPane.setEditorKit(editorKit);
        textPane.setDocument(document);
        scrollPane.setViewportView(textPane);
        
        textPane.setComponentPopupMenu(new PopupMenu());
        
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                URI uri;
                try {
                    uri = new URI(e.getDescription());
                } catch (URISyntaxException ex) {
                    Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
                System.err.printf("Hyperlink activated: %s, %s, %s.\n", e.getEventType().toString(), Objects.toString(e.getURL()), e.getDescription());
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    listeners.handleEvent(l -> l.onLinkActivated(uri));
                }
            }
        });
        
        textPane.setEditable(false);
    }
    
    public String getHTML() {
        StringWriter sw = new StringWriter();
        try {
            editorKit.write(sw, document, 0, document.getLength());
        } catch (IOException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sw.toString();
    }
    
    public void saveTo(String fileName) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileName, "utf-8");
            pw.write(getHTML());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeSilently(pw);
        }
    }
    
    public void clear() {
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(HyperTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public class PopupMenu extends JPopupMenu {
        JMenuItem saveAsItem;
        JMenuItem clearItem;
        JCheckBoxMenuItem hyperTextPrintItem;
        
        public PopupMenu() {
            saveAsItem = new JMenuItem(new SaveAsAction());
            add(saveAsItem);
            
            addSeparator();
            
            clearItem = new JMenuItem(new ClearAction());
            add(clearItem);
            
            addSeparator();
            
            hyperTextPrintItem = new JCheckBoxMenuItem(new HyperTextPrintAction());
            add(hyperTextPrintItem);
        }
        
        public void doPop(MouseEvent e){
            this.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    
    public class SaveAsAction extends AbstractAction {

        public SaveAsAction() {
            putValue(NAME, "Save As...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser ch = new JFileChooser();
            ch.setFileFilter(new FileNameExtensionFilter("HTML file", "html"));
            int result = ch.showSaveDialog(HyperTextPane.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String fileName = ch.getSelectedFile().getPath();
                if (!fileName.toLowerCase().endsWith(".html")) {
                    fileName += ".html";
                }
                HyperTextPane.this.saveTo(fileName);
            }
        }
        
    }
    
    
    public class ClearAction extends AbstractAction {

        public ClearAction() {
            putValue(Action.NAME, "Clear");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            HyperTextPane.this.clear();
        }
        
    }
    
    
    public class HyperTextPrintAction extends AbstractAction {

        public HyperTextPrintAction() {
            putValue(Action.NAME, "HyperText print");
            updateSelectedKey();
        }
        
        protected void updateSelectedKey() {
            putValue(
                    Action.SELECTED_KEY,
                    getReprContextProvider().getReprContext().isHyperTextPrint());
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            ReprContext rc = getReprContextProvider().getReprContext();
            if (rc.isHyperTextPrint()) {
                rc.removeFlags(ReprContext.Flags.HYPERTEXT_PRINT);
            } else {
                rc.addFlags(ReprContext.Flags.HYPERTEXT_PRINT);
            }
            updateSelectedKey();
        }
        
    }
    
    
    private final ListenerCollection<IListener> listeners =
            new ListenerCollection(new IListener[] {});

    public boolean addHyperTextListener(IListener e) {
        return listeners.add(e);
    }
    
    public boolean removeHyperTextListener(IListener o) {
        return listeners.remove(o);
    }
    
    
    public interface IListener {
        void onLinkActivated(URI uri);
    }
    
    
    public static class Adapter implements IListener {
        @Override
        public void onLinkActivated(URI uri) {
            // Do nothing.
        }
    }
}

