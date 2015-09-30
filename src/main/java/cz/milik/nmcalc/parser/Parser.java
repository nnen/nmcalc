/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.parser;

import cz.milik.nmcalc.ast.ASTNodeTypes;
import cz.milik.nmcalc.ast.ASTNode;
import cz.milik.nmcalc.utils.ListMap;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jan
 */
public class Parser {
 
    private Scanner scanner;
    
    public ASTNode parse(String input)
    {
        return parse(new StringReader(input), "<string>");
    }
    
    public ASTNode parse(Reader input, String fileName)
    {
        Scanner s = new Scanner();
        s.reset(input, fileName);
        return parse(s);
    }
    
    public ASTNode parse(Scanner scanner)
    {
        this.scanner = scanner;
        
        ParseResult result = parseExpression(
                new SymbolContext(new TokenStream(scanner)));
        if (!result.isSuccess())
        {
            return null;
        }
        
        return result.getNode();
    }
    
    private boolean expect(SymbolContext ctx, Token.Types tokenType)
    {
        return ctx.getTokens().peek().getType() == tokenType;
    }
    
    private Token.Types peekType(SymbolContext ctx)
    {
        return ctx.getTokens().peek().getType();
    }
    
    private ParseResult parseExpression(SymbolContext ctx)
    {
        ParseResult pr = parseSum(ctx.getChildCtx());
        if (pr.isSuccess())
        {
            return pr;
        }
        
        pr = parseParents(ctx.getChildCtx());
        if (pr.isSuccess())
        {
            return pr;
        }
        
        return new ParseResult(false);
    }
    
    private ParseResult parseFloat(SymbolContext ctx)
    {
        if (!expect(ctx, Token.Types.FLOAT))  
        {
            return new ParseResult(false);
        }
        return new ParseResult(new ASTNode(
                ASTNodeTypes.REAL_LITERAL,
                ctx.getTokens().next()));
    }
    
    private ParseResult parseParents(SymbolContext ctx) {
        if (!expect(ctx, Token.Types.LPAR))
        {
            return new ParseResult(false);
        }
        
        ParseResult pr = parseExpression(ctx.getChildCtx());
        if (pr.isError())
        {
            return pr;
        }
        
        if (!expect(ctx, Token.Types.RPAR))
        {
            return new ParseResult(true);
        }
        
        return pr;
    }
    
    private ParseResult parseSum(SymbolContext ctx)
    {
        ParseResult firstTerm = parseTerm(ctx.getChildCtx());
        if (firstTerm.isError())
        {
            return firstTerm;
        }
        ctx.addChild(firstTerm.getNode());
        
        switch (peekType(ctx))
        {
            case PLUS:
            {
                Token token = ctx.getTokens().next();
                ParseResult rest = parseSum(ctx.getChildCtx());
                if (!rest.isSuccess())
                {
                    return rest;
                }
                return new ParseResult(new ASTNode(
                    ASTNodeTypes.ADDITION,
                    token,
                    new ASTNode[] { firstTerm.getNode(), rest.getNode() }));
            }
            case MINUS:
            {
                Token token = ctx.getTokens().next();
                ParseResult rest = parseSum(ctx.getChildCtx());
                if (!rest.isSuccess())
                {
                    return rest;
                }
                return new ParseResult(new ASTNode(
                    ASTNodeTypes.SUBTRACTION,
                    token,
                    new ASTNode[] { firstTerm.getNode(), rest.getNode() }));
            }
            default:
                return firstTerm;
        }
    }
    
    /*
    private ParseResult parseSum(SymbolContext ctx)
    {
        ParseResult firstTerm = parseTerm(ctx.getChildCtx());
        if (firstTerm.isError())
        {
            return firstTerm;
        }
        ctx.addChild(firstTerm.getNode());
        
        Token token = null;
        while (true)
        {
            switch (peekType(ctx))
            {
                case TOKEN_PLUS:
                    token = ctx.getTokens().next();
                    break;
                default:
                    if (ctx.children.getDefaultList().size() == 1)
                    {
                        return firstTerm;
                    }
                    else
                    {
                        return new ParseResult(new ASTNode(
                            ASTNodeTypes.AST_ADDITION,
                            token,
                            ctx.children.getDefaultList()));
                    }
            }
            
            ParseResult otherTerm = parseTerm(ctx.getChildCtx());
            if (otherTerm.isError())
            {
                return otherTerm;
            }
            else if (!otherTerm.isSuccess())
            {
                return new ParseResult(true);
            }
            ctx.addChild(otherTerm.getNode());
        }
    }
    */
    
    private ParseResult parseTerm(SymbolContext ctx)
    {
        return parseFloat(ctx);
    }
    
    
    private static class ParseResult
    {
        private final boolean success;
        private final boolean error;
        private final ASTNode node;
        
        public boolean isSuccess() { return success; }
        public boolean isError() { return error; }
        public ASTNode getNode() { return node; }
        
        public ParseResult(boolean error)
        {
            this.success = false;
            this.error = error;
            this.node = null;
        }
        
        public ParseResult(ASTNode node)
        {
            this.success = true;
            this.error = false;
            this.node = node;
        }
    }
    
    
    private static class SymbolContext
    {
        private final TokenStream tokenStream;
        
        public TokenStream getTokens()
        {
            return tokenStream;
        }
        
        
        private ASTNodeTypes nodeType;
        
        public ASTNodeTypes getNodeType() { return nodeType; }
        
        
        private List<Token> tokens;
        
        private final ListMap<String, ASTNode> children = new ListMap<>();
        
        
        public SymbolContext(TokenStream tokenStream)
        {
            this.tokenStream = tokenStream;
        }
        
        public SymbolContext(SymbolContext parent)
        {
            tokenStream = parent.getTokens();
        }
        
        
        public Token addToken(Token token)
        {
            if (tokens == null)
            {
                tokens = new ArrayList<>();
            }
            tokens.add(token);
            return token;
        }
        
        public ASTNode addChild(ASTNode node)
        {
            return children.add(node);
        }
        
        public ASTNode addChild(String name, ASTNode node)
        {
            return children.add(name, node);
        }
        
        public SymbolContext getChildCtx()
        {
            return new SymbolContext(this);
        }
    }
    
}
