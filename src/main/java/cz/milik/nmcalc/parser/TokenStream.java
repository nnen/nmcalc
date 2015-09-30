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
public class TokenStream {
    
    private Scanner scanner;
    private Token token;
    
    public TokenStream(Scanner scanner)
    {
        this.scanner = scanner;
    }
    
    public Token peek()
    {
        if (token == null)
        {
            token = scanner.nextToken();
        }
        return token;
    }
    
    public Token next()
    {
        Token t = peek();
        token = null;
        return t;
    }
    
}
