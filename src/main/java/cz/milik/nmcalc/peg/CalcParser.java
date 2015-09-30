/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.BuiltinCalcValue;
import cz.milik.nmcalc.CalcValue;
import cz.milik.nmcalc.FunctionValue;
import cz.milik.nmcalc.ICalcValue;
import cz.milik.nmcalc.SymbolValue;
import cz.milik.nmcalc.ast.ASTBuilder;
import cz.milik.nmcalc.ast.ASTNode;
import cz.milik.nmcalc.parser.Scanner;
import cz.milik.nmcalc.parser.Token;
import static cz.milik.nmcalc.peg.PegParser.concatAny;
import cz.milik.nmcalc.utils.Utils;
import java.io.StringReader;
import java.util.List;


/**
 *
 * @author jan
 */
public class CalcParser extends PegParser<ASTNode> {

    private final ASTBuilder builder = new ASTBuilder();
    
    private final PegGrammar<ASTNode> grammar = new PegGrammar<ASTNode>() {
        @Override
        protected void initializeGrammar() {
            nt("expr", s("assignment"));
            
            nt("assignment",
                concatAny(
                        concatAny(
                                s(Token.Types.IDENTIFIER, "lhs"),
                                s(Token.Types.EQUALS, "op")
                        ).maybe(),
                        s("addition", "rhs")
                ).map(ctx -> {
                    return ctx.withNamedValue("op", Token.class, op -> {
                        return builder.assignment(
                            op,
                            ctx.getNamedValue("lhs", Token.class),
                            ctx.getNamedValue("rhs", ASTNode.class)
                        );
                    }, () -> {
                        return ctx.getNamedValue("rhs", ASTNode.class);
                    });
                })
            );
            
            nt("addition", concatAny(
                    s("term", "first"),
                    concatAny(
                            s(Token.Types.PLUS,
                                    Token.Types.MINUS
                            ).named("operators"),
                            s("term", "rest")
                    ).repeat()
            ).map(ctx -> {
                ASTNode first = ctx.getNamedValue("first", ASTNode.class);
                List<Token> operators = ctx.getNamedValues("operators", Token.class);
                List<ASTNode> rest = ctx.getNamedValues("rest", ASTNode.class);
                ASTNode result = first;
                
                for (int i = 0; i < operators.size(); i++) {
                    result = builder.binaryOp(
                            operators.get(i),
                            result,
                            rest.get(i));
                }
                
                return result;
            }));
            
            /*
            nt("addition", or(
                    concatAny(
                            s("term", "left"),
                            s(
                                    Token.Types.TOKEN_PLUS,
                                    Token.Types.TOKEN_MINUS
                            ).named("operator"),
                            s("addition", "right")
                    ).map(
                            ctx -> builder.binaryOp(
                                    ctx.getNamedValue("operator", Token.class),
                                    ctx.getNamedValue("left", ASTNode.class),
                                    ctx.getNamedValue("right", ASTNode.class)
                            )
                    ),
                    s("term")
            ));
                    */
            
            nt("term", concatAny(
                    s("factor", "first"),
                    concatAny(
                            s(Token.Types.ASTERISK,
                                    Token.Types.SLASH
                            ).named("operators"),
                            s("factor", "rest")
                    ).repeat()
            ).map(ctx -> {
                    ASTNode first = ctx.getNamedValue("first", ASTNode.class);
                    List<Token> operators = ctx.getNamedValues("operators", Token.class);
                    List<ASTNode> rest = ctx.getNamedValues("rest", ASTNode.class);
                    ASTNode result = first;

                    for (int i = 0; i < operators.size(); i++) {
                        result = builder.binaryOp(
                                operators.get(i),
                                result,
                                rest.get(i));
                    }

                    return result;
            }));
            
            nt("factor", or(
                    concatAny(
                            s(Token.Types.LPAR),
                            s("expr", "expr"),
                            s(Token.Types.RPAR)
                    ).map(
                            ctx -> {
                                return ctx.getNamedValue("expr", ASTNode.class);
                            }
                    ),
                    nt("real"),
                    nt("var")
            ));
            
            nt("real", s(Token.Types.FLOAT).map(
                    t -> builder.realLiteral(t)));
            
            nt("var", s(Token.Types.IDENTIFIER).map(
                    t -> builder.variable(t)));
        }
    };
    
    private final PegGrammar<ICalcValue> listGrammar = new PegGrammar<ICalcValue>() {
        @Override
        protected void initializeGrammar() {
            nt("expr", s("assignment"));
            
            nt("assignment",
                concatAny(
                        concatAny(
                                s(Token.Types.IDENTIFIER, "lhs"),
                                s(Token.Types.EQUALS, "op")
                        ).maybe(),
                        s("addition", "rhs")
                ).map(ctx -> {
                        return ctx.withNamedValue("op", Token.class, op -> {
                            Token lhs = ctx.getNamedValue("lhs", Token.class);
                            return CalcValue.list(
                                    BuiltinCalcValue.LET,
                                    CalcValue.makeSymbol(lhs.getValue()),
                                    //ctx.getNamedValue("lhs", ICalcValue.class),
                                    ctx.getNamedValue("rhs", ICalcValue.class)
                            );
                        }, () -> {
                            return ctx.getNamedValue("rhs", ICalcValue.class);
                        });
                })
            );
            
            nt("addition", concatAny(
                    s("term", "first"),
                    concatAny(
                            s(Token.Types.PLUS,
                                    Token.Types.MINUS
                            ).named("operators"),
                            s("term", "rest")
                    ).repeat()
            ).map(ctx -> {
                    ICalcValue first = ctx.getNamedValue("first", ICalcValue.class);
                    List<Token> operators = ctx.getNamedValues("operators", Token.class);
                    List<ICalcValue> rest = ctx.getNamedValues("rest", ICalcValue.class);
                    ICalcValue result = first;

                    for (int i = 0; i < operators.size(); i++) {
                        result = CalcValue.list(
                                BuiltinCalcValue.forOperator(operators.get(i).getType()).unwrap(),
                                result,
                                rest.get(i)
                        );
                    }

                    return result;
            }));
            
            nt("term", concatAny(
                    s("factor", "first"),
                    concatAny(
                            s(Token.Types.ASTERISK,
                                    Token.Types.SLASH
                            ).named("operators"),
                            s("factor", "rest")
                    ).repeat()
            ).map(ctx -> {
                    ICalcValue first = ctx.getNamedValue("first", ICalcValue.class);
                    List<Token> operators = ctx.getNamedValues("operators", Token.class);
                    List<ICalcValue> rest = ctx.getNamedValues("rest", ICalcValue.class);
                    ICalcValue result = first;
                    
                    for (int i = 0; i < operators.size(); i++) {
                        result = CalcValue.list(
                                BuiltinCalcValue.forOperator(operators.get(i).getType()).unwrap(),
                                result,
                                rest.get(i)
                        );
                    }

                    return result;
            }));
            
            nt("factor", concatAny(
                    s("primary", "primary"),
                    concatAny(
                            s(Token.Types.LPAR),
                            concatAny(
                                    s("expr", "args"),
                                    concatAny(
                                            s(Token.Types.COMMA),
                                            s("expr", "args")
                                    ).repeat()
                            ),
                            s(Token.Types.RPAR)
                    ).named("call").maybe()
            ).map(ctx -> {
                return ctx.withNamedValue("call", IPegContext.class, call-> {
                    return CalcValue.list(
                        ctx.getNamedValue("primary", ICalcValue.class),
                        ctx.getNamedValues("args", ICalcValue.class)
                    );
                }, () -> {
                    return ctx.getNamedValue("primary", ICalcValue.class);
                });
            }));
            
            nt("primary", or(
                    concatAny(
                            s(Token.Types.KW_DEF),
                            s(Token.Types.IDENTIFIER, "name"),
                            concatAny(
                                    s(Token.Types.LPAR),
                                    s(Token.Types.IDENTIFIER, "args"),
                                    concatAny(
                                            s(Token.Types.COMMA),
                                            s(Token.Types.IDENTIFIER, "args")
                                    ).repeat(),
                                    s(Token.Types.RPAR)
                            ).maybe(),
                            s("expr", "body")
                    ).map(ctx -> {
                        Token name = ctx.getNamedValue("name", Token.class);
                        List<Token> args = ctx.getNamedValues("args", Token.class);
                        ICalcValue body = ctx.getNamedValue("body", ICalcValue.class);
                        return new FunctionValue(new SymbolValue(name.getValue()), body, Utils.mapList(args, t -> new SymbolValue(t.getValue())));
                    }),
                    concatAny(
                            s(Token.Types.LPAR),
                            s("expr", "expr"),
                            s(Token.Types.RPAR)
                    ).map(ctx -> {
                        return ctx.getNamedValue("expr", ICalcValue.class);
                    }),
                    concatAny(
                            s(Token.Types.LBRA),
                            concatAny(
                                s("expr", "items"),
                                concatAny(
                                        s(Token.Types.COMMA),
                                        s("expr", "items")
                                ).repeat(),
                                s(Token.Types.COMMA).maybe()
                            ).maybe(),
                            s(Token.Types.RBRA)
                    ).map(ctx -> {
                        return CalcValue.list(
                                BuiltinCalcValue.LIST,
                                ctx.getNamedValues("items", ICalcValue.class)
                        );
                    }),
                    nt("real"),
                    nt("var")
            ));
            
            nt("real", s(Token.Types.FLOAT).map(
                    t -> CalcValue.make(Float.parseFloat(t.getValue()))
            ));
            
            nt("var", s(Token.Types.IDENTIFIER).map(
                    t -> CalcValue.makeSymbol(t.getValue())
            ));
        }
    };
    
    public ParseResult<ASTNode> parse(ITokenSequence input) {
        return parse(input, new IPegContext.PegContext(null, null, 0));
    }
    
    public ParseResult<ASTNode> parse(String input) {
        Scanner scanner = new Scanner();
        scanner.reset(new StringReader(input), "<string>");
        
        ITokenSequence tokens = new TokenList(scanner.readTokens());
        
        return parse(tokens);
    }
    
    public ParseResult<ICalcValue> parseList(ITokenSequence input) {
        return listGrammar.getSumbol("expr").end().parse(input, new IPegContext.PegContext(null, null, 0));
    }
    
    public ParseResult<ICalcValue> parseList(String input) {
        Scanner scanner = new Scanner();
        scanner.reset(new StringReader(input), "<string>");
        
        ITokenSequence tokens = new TokenList(scanner.readTokens());
        
        return parseList(tokens);
    }
    
    
    @Override
    public ParseResult<ASTNode> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
        return grammar.getSumbol("expr").end().parse(input, ctx);
    }
}

