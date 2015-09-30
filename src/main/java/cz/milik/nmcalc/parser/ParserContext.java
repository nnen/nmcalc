/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

/**
 *
 * @author jan
 */
public class ParserContext {
    
    private TokenStream tokenStream;
    
    public ParserContext(TokenStream tokenStream)
    {
        this.tokenStream = tokenStream;
    }
    
    public Token peek()
    {
        return tokenStream.peek();
    }
    
    public Token next()
    {
        return tokenStream.next();
    }
    
}
