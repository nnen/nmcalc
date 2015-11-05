/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jan
 */
public class MarkupParserTest {
    
    MarkupParser parser;
    
    public MarkupParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        parser = new MarkupParser();
    }
    
    @After
    public void tearDown() {
        parser = null;
    }
    
    protected ITextElement parse(String input) {
        ITextElement e = parser.parse(input);
        TextPrinter p = new TextPrinter();
        p.print(e);
        return e;
    }
    
    @Test
    public void testParseCodeBlock() {
        ITextElement e = parse("    code block");
        
        parse("First paragraph.\n\n    code block\n\nSecond paragraph.\n");
    }
    
    
    public static class TextPrinter implements ITextElementVisitor<PrintStream, Object> {
        
        public void print(ITextElement e) {
            e.visit(this, System.err);
        }
        
        
        private int indent = 0;
        
        protected void printIndent(PrintStream out) {
            for (int i = 0; i < indent; i++) {
                out.append("  ");
            }
        }
        
        protected void printChildren(ITextElement e, PrintStream ctx) {
            indent++;
            for (ITextElement child : e.getChildren()) {
                child.visit(this, ctx);
            }
            indent--;
        }
        
        protected Object print(ITextElement e, PrintStream ctx) {
            printIndent(ctx);
            ctx.print(e.getClass().getSimpleName());
            ctx.print(": ");
            ctx.println(e.getText().substring(0, 10));
            printChildren(e, ctx);
            return null;
        }
        
        @Override
        public Object visitFragment(Text.Fragment fragment, PrintStream ctx) {
            return print(fragment, ctx);
        }
        
        @Override
        public Object visitParagraph(Text.Paragraph paragraph, PrintStream ctx) {
            return print(paragraph, ctx);
        }

        @Override
        public Object visitBlockQuote(Text.BlockQuote blockQuote, PrintStream ctx) {
            return print(blockQuote, ctx);
        }

        @Override
        public Object visitCodeBlock(Text.CodeBlock codeBlock, PrintStream ctx) {
            return print(codeBlock, ctx);
        }

        @Override
        public Object visitHeadline(Text.Headline headline, PrintStream ctx) {
            return print(headline, ctx);
        }

        @Override
        public Object visitPlainText(Text.PlainText plainText, PrintStream ctx) {
            return print(plainText, ctx);
        }

        @Override
        public Object visitMonospace(Text.Monospace plainText, PrintStream ctx) {
            return print(plainText, ctx);
        }

        @Override
        public Object visitItalic(Text.Italic italic, PrintStream ctx) {
            return print(italic, ctx);
        }

        @Override
        public Object visitBold(Text.Bold bold, PrintStream ctx) {
            return print(bold, ctx);
        }

        @Override
        public Object visitSpan(Text.Span element, PrintStream ctx) {
            return print(element, ctx);
        }
        
        @Override
        public Object visitCalcValue(Text.CalcValue calcValue, PrintStream ctx) {
            return print(calcValue, ctx);
        }

        @Override
        public Object visitOther(ITextElement element, PrintStream ctx) {
            return print(element, ctx);
        }

        @Override
        public Object visitBulletList(Text.BulletList bulletList, PrintStream ctx) {
            return print(bulletList, ctx);
        }

        @Override
        public Object visitBulletPoint(Text.BulletPoint bulletPoint, PrintStream ctx) {
            return print(bulletPoint, ctx);
        }
        
    }
}
