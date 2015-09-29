/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import java.util.List;

/**
 *
 * @author jan
 */
public class TokenList implements ITokenSequence {

    private final List<Token> list;
    
    protected List<Token> getList() { return list; }
    
    
    public TokenList(List<Token> aList)
    {
        list = aList;
    }
    
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Token get(int index) {
        if (index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
    
    @Override
    public ITokenSequence advance(int amount) {
        return new TokenList(list.subList(amount, list.size() - amount + 1));
    }
    
}
