/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jan
 */
public class MarkupParser {
    
    public static final Pattern PAR_SEPARATOR = Pattern.compile("\\r?\\n(\\r?\\n)+");
    public static final Pattern LINE_SEPARATOR = Pattern.compile("\\s*\\n\\s*");
    
    public static final Pattern BULLET_LIST_START = Pattern.compile("\\s*^  [ \\t]*-");
    public static final Pattern BULLET_POINT = Pattern.compile("(\\r\\n)*^  [ \\t]*-", Pattern.MULTILINE);
    
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
        
        String[] lines = input.split("\\r?\\n");
        
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
            
            System.err.println("headline = \"" + headline + "\"");
            System.err.println("underline = \"" + underline + "\"");
            
            if (underline.matches("===+")) {
                return Text.headline(headline, 1);
            } else if (underline.matches("\\-\\-\\-+")) {
                return Text.headline(headline, 2);
            }
        }
        
        boolean isCodeBlock = true;
        for (String line : lines) {
            if (!line.isEmpty() && !line.startsWith("    ")) {
                isCodeBlock = false;
                break;
            }
        }
        if (isCodeBlock) {
            List<String> newLines = new ArrayList();
            for (String line : lines) {
                newLines.add(line.substring(4));
            }
            return Text.codeBlock(StringUtils.join("\n", newLines.iterator()).toString());
        }
        
        return parseText(input);
        //return Text.paragraph(input);  
    }
    
    public ITextElement parseText(String input) {
        String glued = StringUtils.join(
                " ",
                LINE_SEPARATOR.splitAsStream(input)).toString();
        ITextElement result = Text.paragraph();
        return parseText(glued, result);
    }
    
    public ITextElement parseText(String input, ITextElement result) {
        Pattern p = Pattern.compile("`([^`]*)`|\\*\\*((\\\\\\*|[^*])*)\\*\\*|\\*((\\\\\\*|[^*])*)\\*");
        Matcher m = p.matcher(input);
        int lastOffset = 0;
        
        while (m.find()) {
            if (lastOffset < m.start()) {
                result.addChild(Text.plain(input.substring(lastOffset, m.start())));
            }
            
            if (m.group(2) != null) {
                Text.Bold bold = Text.bold();
                parseText(m.group(2).replace("\\*", "*"), bold);
                result.addChild(bold);
            } else if (m.group(4) != null) {
                result.addChild(Text.italic(m.group(4).replace("\\*", "*")));
            } else {
                result.addChild(Text.monospace(m.group(1)));
            }
            
            lastOffset = m.end();
        }
        
        if (lastOffset < input.length()) {
            result.addChild(Text.plain(input.substring(lastOffset)));
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
    
}
