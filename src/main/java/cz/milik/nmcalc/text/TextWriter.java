/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.IReprContextProvider;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.text.Text.Fragment;
import cz.milik.nmcalc.text.Text.Link;
import cz.milik.nmcalc.values.ICalcValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.function.Consumer;

/**
 *
 * @author jan
 */
public class TextWriter {
    
    private Stack<ITextElement> stack;
    
    
    private ITextElement result;
    
    public ITextElement getResult() {
        return result;
    }
    
    
    private StringBuilder sb = new StringBuilder();
    
    
    private ReprContext defaultReprContext = ReprContext.getDefault();
    
    public ReprContext getDefaultReprContext() {
        return defaultReprContext;
    }

    public void setDefaultReprContext(ReprContext defaultReprContext) {
        this.defaultReprContext = defaultReprContext;
    }
    
    
    public TextWriter() {
        result = new Fragment();
        stack = new Stack();
        stack.push(result);
    }
    
    
    public ITextElement peek() {
        return stack.peek();
    }
    
    
    public TextWriter start(ITextElement element) {
        stack.peek().addChild(element);
        stack.push(element);
        return this;
    }
    
    public TextWriter startPar() {
        return start(Text.paragraph());
    }
    
    public TextWriter startBlockQuote() {
        return start(Text.blockQuote());
    }
    
    public TextWriter startCodeBlock(String codeLang) {
        return start(Text.codeBlock(null, codeLang));
    }
    
    public TextWriter startTable() {
        return start(Text.table());
    }
    
    public TextWriter startTableRow() {
        return start(Text.tableRow());
    }
    
    public TextWriter startTableRow(boolean changesParity) {
        return start(Text.tableRow(changesParity));
    }
    
    public TextWriter startTableCell() {
        return start(Text.tableCell());
    }
    
    public TextWriter startTableCell(boolean header, int columnSpan, Text.HAlignment hAlign) {
        return start(Text.tableCell(header, columnSpan, hAlign));
    }
    
    public TextWriter tableCell(String fmt, Object... args) {
        return append(Text.tableCell(fmt, args));
    }
    
    public TextWriter tableCell(boolean header, String fmt, Object... args) {
        return append(Text.tableCell(header, fmt, args));
    }
    
    public TextWriter tableCell(boolean header, int columnSpan, String fmt, Object... args) {
        return append(Text.tableCell(header, columnSpan, fmt, args));
    }
    
    public TextWriter tableCell(boolean header, int columnSpan, Text.HAlignment hAlign, String fmt, Object... args) {
        return append(Text.tableCell(header, columnSpan, hAlign, fmt, args));
    }
    
    public TextWriter startBold() {
        return start(Text.bold());
    }
    
    public TextWriter startMonospace() {
        return start(Text.monospace());
    }
    
    public TextWriter startSpan(String spanType) {
        return start(Text.span(spanType));
    }
    
    public TextWriter end() {
        stack.pop();
        return this;
    }
    
    
    public TextWriter append(ITextElement... elements) {
        for (ITextElement e : elements) {
            stack.peek().addChild(e);
        }
        return this;
    }
    
    public TextWriter link(String text, Consumer<Link> action) {
        return append(Text.link(text, action));
    }
    
    public TextWriter plain(String text) {
        return append(Text.plain(text));
    }
    
    public TextWriter format(String fmt, Object... args) {
        return plain(String.format(fmt, args));
    }
    
    public TextWriter monospace(String text, Object... args) {
        return append(Text.monospace(text, args));
    }
    
    public TextWriter italic(String text) {
        return append(Text.italic(text));
    }
    
    public TextWriter bold(String text) {
        return append(Text.bold(text));
    }
    
    public TextWriter headline(int level, String value) {
        return append(Text.headline(value, level));
    }
    
    public TextWriter span(String spanType, String value) {
        return append(Text.span(spanType, value));
    }
    
    public TextWriter par(String text) {
        return append(Text.paragraph(text));
    }
    
    public TextWriter codeBlock(String text) {
        return append(Text.codeBlock(text));
    }
    
    public TextWriter append(ICalcValue value) {
        return append(Text.value(value));
    }
    
    public TextWriter append(ICalcValue value, IReprContextProvider ctx) {
        return append(Text.value(value, ctx));
    }
    
    public TextWriter append(IPrintable printable, ReprContext ctx) {
        printable.printDebug(this, ctx);
        return this;
    }
    
    public TextWriter append(IPrintable printable) {
        return append(printable, getDefaultReprContext());
    }
    
    
    public TextWriter appendNull(ReprContext ctx) {
        monospace("null");
        return this;
    }
    
    public TextWriter append(Object obj, ReprContext ctx) {
        if (obj == null) {
            return appendNull(ctx);
        }
        
        if (obj instanceof IPrintable) {
            return append((IPrintable)obj, ctx);
        }
        
        if (obj instanceof Map) {
            return append((Map<?, ?>)obj, ctx);
        }
        
        if (obj instanceof Collection) {
            return append((Collection<?>)obj, ctx);
        }
        
        monospace(obj.toString());
        return this;
    }
    
    public TextWriter append(Object[] array, ReprContext ctx) {
        if (array == null) {
            appendNull(ctx);
            return this;
        }
        
        if (ctx.isHyperTextPrint()) {
            startTable();
            startTableRow();
            tableCell(true, 2, "Array");
            end();
            startTableRow();
            tableCell(true, "Index");
            tableCell(true, "Item");
            end();
            for (int i = 0; i < array.length; i++) {
                startTableCell();
                monospace(Integer.toString(i));
                end();
                startTableCell();
                append(array[i], ctx);
                end();
            }
            end();
        } else {
            monospace("[");
            for (int i = 0; i < array.length; i++) {
                if (i == 0) {
                    monospace(" ");
                } else {
                    monospace(", ");
                }
                monospace("%d: ", i);
                append(array[i], ctx);
            }
            monospace(" ]");
        }
        
        return this;
    }
    
    public TextWriter append(Collection<?> collection, ReprContext ctx) {
        if (ctx.isHyperTextPrint()) {
            startTable();
            startTableRow();
            tableCell(true, 2, collection.getClass().getSimpleName());
            end();
            startTableRow();
            tableCell(true, "Index");
            tableCell(true, "Item");
            end();
            int i = 0;
            for (Object item : collection) {
                startTableRow();
                startTableCell();
                monospace(Integer.toString(i));
                end();
                startTableCell();
                append(item, ctx);
                end();
                end();
            }
            end();
        } else {
            plain("[");
            boolean first = true;
            for (Object item : collection) {
                if (first) {
                    first = false;
                    plain(" ");
                } else {
                    plain(", ");
                }
                append(item, ctx);
            }
            plain(" ]");
        }
        return this;
    }
    
    public TextWriter append(Map<?, ?> map, ReprContext ctx) {
        if (ctx.isHyperTextPrint()) {
            List<Entry<?, ?>> entries = new ArrayList(map.entrySet());
            startTable();
            startTableRow();
            tableCell(true, 2, map.getClass().getSimpleName());
            end();
            startTableRow();
            tableCell(true, "Key");
            tableCell(true, "Value");
            end();
            for (Entry<?, ?> entry : entries) {
                startTableRow();
                startTableCell();
                append(entry.getKey(), ctx);
                end();
                startTableCell();
                append(entry.getKey(), ctx);
                end();
                end();
            }
            end();
        } else {
            plain("{");
            boolean first = true;
            for (Entry<?, ?> entry : map.entrySet()) {
                if (first) {
                    first = false;
                    plain(" ");
                } else {
                    plain(", ");
                }
                append(entry.getKey(), ctx);
                plain(": ");
                append(entry.getValue(), ctx);
            }
            plain(" }");
        }
        return this;
    }
    
    
    public static ITextElement print(IPrintable printable, ReprContext ctx) {
        TextWriter tw = new TextWriter();
        if (printable == null) {
            tw.monospace("null");
        } else {
            printable.print(tw, ctx);
        }
        return tw.getResult();
    }
    
    public static ITextElement print(Object obj, ReprContext ctx) {
        TextWriter tw = new TextWriter();
        tw.append(obj, ctx);
        return tw.getResult();
    }
}
