/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.text;

import cz.milik.nmcalc.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class MarkupParser {
    
    public ITextElement parse(String input) {
        String[] paras = input.split("\\r?\\n(\\r?\\n)+");
        Text.Fragment result = new Text.Fragment();
        
        for (String para : paras) {
            String trimmed = para.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            result.addChild(parseParagraph(para, trimmed));
        }
        
        System.err.println(result.toString());
        
        return result;
    }
    
    public ITextElement parseParagraph(String input, String trimmed) {
        String[] lines = input.split("\\r?\\n");
        
        for (int i = 0; i < lines.length; i++) {
            System.err.printf("[%d] \"%s\"\n", i, lines[i]);
        }
        
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
        
        return Text.paragraph(input);  
    }
    
}
