/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.utils.StringUtils;
import cz.milik.nmcalc.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jan
 */
public class MarkupParser {
    
    public static final Pattern INDENT_PATTERN = Pattern.compile("^[ \\t]*");
    
    public static final Pattern PAR_SEPARATOR = Pattern.compile("\\r?\\n(\\s*\\r?\\n)+");
    public static final Pattern LINE_SEPARATOR = Pattern.compile("\\s*\\n\\s*");
    
    public static final Pattern CODE_TAG = Pattern.compile("\\s*    \\[([a-z][a-zA-Z0-9_]*)\\]");
    
    public static final Pattern BULLET_LIST_START = Pattern.compile("\\s*^  [ \\t]*-");
    public static final Pattern BULLET_POINT = Pattern.compile("(\\r\\n)*^  [ \\t]*-", Pattern.MULTILINE);
    
    public static final Pattern INLINE_CODE = Pattern.compile("`([^`]*)`");
    public static final Pattern EMPHASIS = Pattern.compile("\\*((\\\\\\*|[^*])*)\\*");
    public static final Pattern STRONG_EMPHASIS = Pattern.compile("\\*\\*((\\\\\\*|[^*])*)\\*\\*");
    public static final Pattern LINK = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");
    
    
    private List<SpanParser> spanParsers = new ArrayList();
    private Pattern spanPattern;
    
    public MarkupParser()
    {
        spanParsers.add(INLINE_CODE_PARSER);
        spanParsers.add(STRONG_EMPHASIS_PARSER);
        spanParsers.add(EMPHASIS_PARSER);
        spanParsers.add(LINK_PARSER);
        
        int groupOffset = 1;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (SpanParser sp : spanParsers) {
            sp.setGroupOffset(groupOffset);
            groupOffset += sp.getGroupCount();
            
            if (first) {
                first = false;
            } else {
                sb.append("|");
            }
            sb.append("(");
            sb.append(sp.getPattern());
            sb.append(")");
        }
        spanPattern = Pattern.compile(sb.toString());
    }
    
    
    public IHighlighter getHighlighter(String language) {
        if (Objects.equals(language, "nmcalc")) {
            return new TokenizerHighlighter();
        } else {
            return new NullHighlighter();
        }
    }
    
    
    public ITextElement parse(String input) {
        final Text.Fragment result = new Text.Fragment();
        
        PAR_SEPARATOR.splitAsStream(input).forEach(para -> {
            result.addChild(parseParagraph(para, para));
        });
        
        return result;
    }
    
    public ITextElement parseParagraph(String input, String trimmed) {
        if (BULLET_LIST_START.matcher(input).lookingAt()) {
            return parseList(input);
        }
        
        String[] lines = trimLines(input.split("\\r?\\n"));
        
        if ((lines.length == 1) || (lines.length == 2 && lines[1].isEmpty())) {
            String line = lines[0].trim();
            if (line.startsWith("# ")) {
                return Text.headline(line.substring(2), 1);
            } else if (line.startsWith("## ")) {
                return Text.headline(line.substring(3), 2);
            } else if (line.startsWith("### ")) {
                return Text.headline(line.substring(4), 3);
            }
        } else if (lines.length == 2 || (lines.length == 3 && lines[2].isEmpty())) {
            String headline = lines[0].trim();
            String underline = lines[1].trim();
            
            if (underline.matches("===+")) {
                return Text.headline(headline, 1);
            } else if (underline.matches("\\-\\-\\-+")) {
                return Text.headline(headline, 2);
            }
        }
        
        String indent = getParIndent(lines);
        if (indent.startsWith("    ")) {
            String[] unindented = unindent(lines, indent);
            String codeLang = null;
            int from = 0;
            
            Matcher codeTag = CODE_TAG.matcher(lines[0]);
            if (codeTag.lookingAt()) {
                codeLang = codeTag.group(1);
                from = 1;
            }
            
            
            IHighlighter hl = getHighlighter(codeLang);
            
            TextWriter tw = new TextWriter();
            tw.startCodeBlock(codeLang);
            ITextElement result = tw.peek();
            hl.highlight(tw, StringUtils.joinStr("\n", unindented, from));
            tw.end();
            
            return result;
            /*
            return Text.codeBlock(
                    StringUtils.joinStr("\n", unindented, from),
                    codeLang
            );
                    */
        }
        
        return parseText(input);
    }
    
    public ITextElement parseText(String input) {
        String glued = StringUtils.join(
                " ",
                LINE_SEPARATOR.splitAsStream(input)).toString();
        ITextElement result = Text.paragraph();
        return parseText(glued, result);
    }
    
    public ITextElement parseText(String input, ITextElement result) {
        int offset = 0;
        
        while (offset < input.length()) {
            Matcher m = find(input, spanPattern, offset);
            
            if (m == null) {
                break;
            }
            
            for (SpanParser sp : spanParsers) {
                int groupOffset = sp.getGroupOffset();
                
                if (!Utils.isNullOrEmpty(m.group(groupOffset))) {
                    if (m.start() > offset) {
                        result.addChild(Text.plain(input.substring(offset, m.start())));
                    }
                    result.addChild(sp.parse(m, result));
                    offset = m.end();
                    break;
                }
            }
        }
        
        if (offset < input.length()) {
            result.addChild(Text.plain(input.substring(offset, input.length())));
        }
        
        return result;
    }
    
    public ITextElement parseList(String input) {
        ITextElement result = Text.bulletList();
        return parseList(input, result);
    }
    
    public ITextElement parseList(String input, ITextElement listElement) {
        Matcher m = BULLET_POINT.matcher(input);
        int lastOffset = -1;
        
        while (m.find()) {
            if ((lastOffset < m.start()) && (lastOffset >= 0)) {
                Text.BulletPoint point = Text.bulletPoint();
                parseText(input.substring(lastOffset, m.start()), point);
                listElement.addChild(point);
            }
            lastOffset = m.end();
        }
        
        if (lastOffset >= 0) {
            Text.BulletPoint point = Text.bulletPoint();
            parseText(input.substring(lastOffset), point);
            listElement.addChild(point);
        }
        
        return listElement;
    }
    
    
    protected Matcher find(CharSequence input, Pattern p, int offset) {
        Matcher m = p.matcher(input);
        if (!m.find(offset)) {
            return null;
        }
        return m;
    }
    
    
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().isEmpty();
    }
    
    public static String commonPrefix(CharSequence a, CharSequence b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length() && i < b.length(); i++) {
            char charA = a.charAt(i);
            char charB = b.charAt(i);
            if (charA != charB) {
                break;
            }
            sb.append(charA);
        }
        return sb.toString();
    }
    
    public static String getParIndent(String[] lines) {
        String indent = null;
        
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            Matcher m = INDENT_PATTERN.matcher(line);
            if (indent == null) {
                if (m.lookingAt()) {
                    indent = m.group();
                } else {
                    indent = "";
                }
            } else {
                indent = commonPrefix(indent, line);
                if (indent.isEmpty()) {
                    return indent;
                }
            }
        }
        
        if (indent == null) {
            indent = "";
        }
        
        return indent;
    }
    
    public static String[] unindent(String[] lines, String indent) {
        String[] newLines = new String[lines.length];
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            if (line.trim().isEmpty()) {
                newLines[i] = "";
            } else if (line.startsWith(indent)) {
                newLines[i] = line.substring(indent.length());
            } else {
                newLines[i] = line;
            }
        }
        
        return newLines;
    }
    
    public static String[] trimLines(String[] lines) {
        List<String> newLines = new ArrayList();
        List<String> emptyLines = new ArrayList();
        
        int i = 0;
        
        while (isEmpty(lines[i])) {
            i++;
        }
        
        for (; i < lines.length; i++) {
            if (isEmpty(lines[i])) {
                emptyLines.add(lines[i]);
            } else {
                newLines.addAll(emptyLines);
                emptyLines.clear();
                newLines.add(lines[i]);
            }
        }
        
        return Utils.toArray(newLines);
    }

    
    public static class ParagraphParser {
        public List<ITextElement> parse(String input) {
            String[] lines = trimLines(input.split("\\r?\\n"));
            return parse(lines);
        }
        
        public List<ITextElement> parse(String[] lines) {
            return Collections.emptyList();
        }
    }
    
    public ParagraphParser BULLET_LIST_PARSER = new ParagraphParser() {
        @Override
        public List<ITextElement> parse(String input) {
            if (!BULLET_LIST_START.matcher(input).lookingAt()) {
                return null;
            }
            
            return super.parse(input); //To change body of generated methods, choose Tools | Templates.
        }
    };
    
    public ParagraphParser NORMAL_PAR_PARSER = new ParagraphParser() {
        @Override
        public List<ITextElement> parse(String input) {
            ITextElement paragraph = Text.paragraph();
            parseText(input, paragraph);
            return Collections.singletonList(paragraph);
        }
    };
    
    
    public static class SpanParser {
        private final String pattern;
        
        public String getPattern() {
            return pattern;
        }
        
        private final int groupCount;
        
        public int getGroupCount() {
            return groupCount;
        }
        
        private int groupOffset;

        public int getGroupOffset() {
            return groupOffset;
        }

        public void setGroupOffset(int groupOffset) {
            this.groupOffset = groupOffset;
        }
        
        
        public SpanParser(String pattern) {
            this.pattern = pattern;
            this.groupCount = 1;
        }

        public SpanParser(String pattern, int groupCount) {
            this.pattern = pattern;
            this.groupCount = groupCount;
        }
        
        
        public ITextElement parse(Matcher m, ITextElement parent) {
            return Text.plain(m.group(getGroupOffset()));
        }
    }
    
    public SpanParser INLINE_CODE_PARSER = new SpanParser("`([^`]*)`", 2) {
        @Override
        public ITextElement parse(Matcher m, ITextElement parent) {
            return Text.monospace(m.group(getGroupOffset() + 1));
        }
    };
    
    public SpanParser STRONG_EMPHASIS_PARSER = new SpanParser("\\*\\*((\\\\\\*|[^*])*)\\*\\*", 3) {
        @Override
        public ITextElement parse(Matcher m, ITextElement parent) {
            ITextElement result = Text.bold();
            parseText(m.group(getGroupOffset() + 1), result);
            return result;
            //return Text.bold(m.group(getGroupOffset() + 1));
        }
    };
    
    public SpanParser EMPHASIS_PARSER = new SpanParser("\\*((\\\\\\*|[^*])*)\\*", 3) {
        @Override
        public ITextElement parse(Matcher m, ITextElement parent) {
            ITextElement result = Text.italic();
            parseText(m.group(getGroupOffset() + 1), result);
            return result;
        }
    };
    
    public SpanParser LINK_PARSER = new SpanParser("\\[(.*?)\\]\\((.*?)\\)", 3) {
        @Override
        public ITextElement parse(Matcher m, ITextElement parent) {
            ITextElement result = Text.link(m.group(getGroupOffset() + 2), null);
            parseText(m.group(getGroupOffset() + 1), result);
            return result;
        }
    };
}

