/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.BuiltinCalcValue;
import cz.milik.nmcalc.values.CalcValue;
import cz.milik.nmcalc.values.FloatValue;
import cz.milik.nmcalc.values.ICalcValue;
import cz.milik.nmcalc.ListBuilder;
import cz.milik.nmcalc.MathBuiltins;
import cz.milik.nmcalc.values.NothingValue;
import cz.milik.nmcalc.values.SymbolValue;
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
            nt("expr", or(s("def"), s("ifElse"), s("match"), s("comparison")));
            
            nt("def", concatAny(
                    //s(Token.Types.KW_DEF),
                    keyword("def"),
                    s(Token.Types.IDENTIFIER, "name").maybe(),
                    concatAny(
                            s(Token.Types.LPAR),
                            concatAny(
                                s(Token.Types.IDENTIFIER, "args"),
                                concatAny(
                                        s(Token.Types.COMMA),
                                        s(Token.Types.IDENTIFIER, "args")
                                ).repeat()
                            ).maybe(),
                            s(Token.Types.RPAR)
                    ).maybe(),
                    or (
                            concatAny(
                                    s("str", "help"),
                                    s("expr", "body").maybe()
                            ),
                            concatAny(
                                s("expr", "body")
                            )
                    )
                    //s("str", "help"),
                    //s("expr", "body").maybe()
            ).map(ctx -> {
                Token name = ctx.getNamedValue("name", Token.class);
                List<Token> args = ctx.getNamedValues("args", Token.class);
                ICalcValue help = ctx.getNamedValue("help", ICalcValue.class);
                ICalcValue body = ctx.getNamedValue("body", ICalcValue.class);
                
                String nameStr = (name == null) ? "" : name.getValue();
                
                if (body == null) {
                    body = help;
                    help = null;
                }
                
                SymbolValue symbol = new SymbolValue(nameStr);
                ICalcValue argList = CalcValue.list(Utils.mapList(args, t -> new SymbolValue(t.getValue())));
                
                if (help != null) {
                    return CalcValue.list(
                        BuiltinCalcValue.DEF,
                        symbol,
                        argList,
                        help,
                        body
                    );
                }
                
                return CalcValue.list(
                        BuiltinCalcValue.DEF,
                        symbol,
                        argList,
                        body
                );
            }));
            
            nt("ifElse",
                    concatAny(
                            //s(Token.Types.KW_IF),
                            keyword("if"),
                            s("expr", "cond"),
                            //s(Token.Types.KW_THEN),
                            keyword("then"),
                            s("expr", "true"),
                            //s(Token.Types.KW_ELSE),
                            keyword("else"),
                            s("expr", "false")
                    ).map(ctx -> {
                        return CalcValue.list(
                                BuiltinCalcValue.IF_ELSE,
                                ctx.getNamedValue("cond", ICalcValue.class),
                                ctx.getNamedValue("true", ICalcValue.class),
                                ctx.getNamedValue("false", ICalcValue.class)
                        );
                    })
            );
            
            nt("match",
                    concatAny(
                            //s(Token.Types.KW_MATCH).ignore(),
                            keyword("match").ignore(),
                            s("expr", "value"),
                            s(Token.Types.LBRACE).ignore(),
                            concatAny(
                                    //s(Token.Types.KW_CASE).ignore(),
                                    keyword("case").ignore(),
                                    s("expr", "patterns"),
                                    s(Token.Types.ARROW).ignore(),
                                    s("expr", "bodies")
                            ).repeat(),
                            s(Token.Types.RBRACE).ignore()
                    ).map(ctx -> {
                        ICalcValue value = ctx.getNamedValue("value", ICalcValue.class);
                        List<ICalcValue> patterns = ctx.getNamedValues("patterns", ICalcValue.class);
                        List<ICalcValue> bodies = ctx.getNamedValues("bodies", ICalcValue.class);
                        assert(patterns.size() == bodies.size());
                        ListBuilder lb = new ListBuilder();
                        for (int i = 0; i < patterns.size(); i++) {
                            lb.add(CalcValue.list(
                                    patterns.get(i),
                                    bodies.get(i)
                            ));
                        }
                        return CalcValue.list(
                                BuiltinCalcValue.MATCH,
                                value,
                                CalcValue.list(
                                        BuiltinCalcValue.QUOTE,
                                        lb.makeList()
                                )
                        );
                    })
            );
            
            nt("comparison",
                    concatAny(
                            s("assignment", "first"),
                            concatAny(
                                    or(
                                        s(Token.Types.EQUALS_COMP),
                                        s(Token.Types.LT_COMP),
                                        s(Token.Types.GT_COMP)
                                    ).named("operators"),    
                                    s("comparison", "rest")
                            ).repeat()
                    ).map(ctx -> {
                        ICalcValue result = ctx.getNamedValue("first", ICalcValue.class);
                        List<Token> operators = ctx.getNamedValues("operators", Token.class);
                        List<ICalcValue> rest = ctx.getNamedValues("rest", ICalcValue.class);
                        for (int i = 0; i < rest.size(); i++) {
                            Token op = operators.get(i);
                            ICalcValue rhs = rest.get(i);
                            BuiltinCalcValue primitive;
                            switch (op.getType()) {
                                case LT_COMP:
                                    primitive = BuiltinCalcValue.LT;
                                    break;
                                case GT_COMP:
                                    primitive = BuiltinCalcValue.GT;
                                    break;
                                default:
                                    primitive = BuiltinCalcValue.EQUALS;
                                    break;
                            }
                            result = CalcValue.list(
                                    primitive,
                                    result,
                                    rhs
                            );
                        }
                        return result;
                    })
            );
            
            nt("assignment",
                concatAny(
                        concatAny(
                                s(Token.Types.IDENTIFIER, "lhs"),
                                s(Token.Types.EQUALS, "op")
                        ).maybe(),
                        s("cons", "rhs")
                ).map(ctx -> {
                        return ctx.withNamedValue("op", Token.class, op -> {
                            Token lhs = ctx.getNamedValue("lhs", Token.class);
                            return CalcValue.list(
                                    BuiltinCalcValue.LET,
                                    CalcValue.quote(CalcValue.makeSymbol(lhs.getValue())),
                                    //ctx.getNamedValue("lhs", ICalcValue.class),
                                    ctx.getNamedValue("rhs", ICalcValue.class)
                            );
                        }, () -> {
                            return ctx.getNamedValue("rhs", ICalcValue.class);
                        });
                })
            );
            
            nt("cons", concatAny(
                    s("shift", "left"),
                    concatAny(
                            s(Token.Types.CONS).ignore(),
                            s("shift", "right")
                    ).maybe()
            ).map(ctx -> {
                ICalcValue left = ctx.getNamedValue("left", ICalcValue.class);
                ICalcValue right = ctx.getNamedValue("right", ICalcValue.class);
                if (right == null) {
                    return left;
                } else {
                    return CalcValue.list(
                            BuiltinCalcValue.CONS,
                            left,
                            right
                    );
                }
            }));
            
            nt("shift", concatAny(
                    s("addition", "first"),
                    concatAny(
                            s(
                                    Token.Types.LSHIFT,
                                    Token.Types.RSHIFT
                            ).named("operators"),
                            s("addition", "rest")
                    ).repeat()
            ).map(ctx -> {
                ICalcValue result = ctx.getNamedValue("first", ICalcValue.class);
                List<Token> operators = ctx.getNamedValues("operators", Token.class);
                List<ICalcValue> rest = ctx.getNamedValues("rest", ICalcValue.class);
                
                for (int i = 0; i < operators.size(); i++) {
                    result = CalcValue.list(
                            BuiltinCalcValue.forOperator(
                                    operators.get(i).getType()).unwrap(),
                            result,
                            rest.get(i)
                    );
                }
                
                return result;
            }));
            
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
                    or(
                        concatAny(
                                s(Token.Types.LPAR).ignore(),
                                concatAny(
                                        s("expr", "args"),
                                        concatAny(
                                                s(Token.Types.COMMA).ignore(),
                                                s("expr", "args")
                                        ).repeat()
                                ).maybe(),
                                s(Token.Types.RPAR).ignore()
                        ).named("call"),
                        concatAny(
                                s(Token.Types.LBRA),
                                s("expr", "key"),
                                s(Token.Types.RBRA)
                        ).named("index")
                    ).repeat(),
                    concatAny(
                            s(Token.Types.DOUBLE_ASTERISK).ignore(),
                            s("factor", "exponent")
                    ).named("power").maybe()
            ).map(ctx -> {
                ICalcValue result = ctx.getNamedValue("primary", ICalcValue.class);
                
                IPegContext callCtx = ctx.getNamedValue("call", IPegContext.class);
                if (callCtx != null) {
                    result = CalcValue.list(
                            result,
                            ctx.getNamedValues("args", ICalcValue.class)
                    );
                }
                
                IPegContext powCtx = ctx.getNamedValue("power", IPegContext.class);
                if (powCtx != null) {
                    result = CalcValue.list(
                            MathBuiltins.POW,
                            result,
                            ctx.getNamedValue("exponent", ICalcValue.class)
                    );
                }
                
                return result;
            }));
            
            nt("primary", or(
                    concatAny(
                            s(Token.Types.QUOTE),
                            s("primary", "primary")
                    ).map(ctx -> {
                        return CalcValue.list(
                                BuiltinCalcValue.QUOTE,
                                ctx.getNamedValue("primary", ICalcValue.class)
                        );
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
                    nt("hex"),
                    nt("oct"),
                    nt("bin"),
                    nt("var"),
                    nt("symbol"),
                    nt("str"),
                    nt("bool_literal"),
                    nt("nothing"),
                    nt("dict_literal")
            ));
            
            nt("real", s(Token.Types.FLOAT).map(
                    //t -> CalcValue.make(Float.parseFloat(t.getValue()))
                    t -> FloatValue.parse(t.getValue())
            ));
            
            nt("hex", s(Token.Types.HEX_LITERAL).map(
                    t -> FloatValue.parseHex(t.getValue())
            ));
            
            nt("oct", s(Token.Types.OCT_LITERAL).map(
                    t -> FloatValue.parseOct(t.getValue())
            ));
            
            nt("bin", s(Token.Types.BIN_LITERAL).map(
                    t -> FloatValue.parseBin(t.getValue())
            ));
            
            nt("var", s(Token.Types.IDENTIFIER).map(
                    t -> CalcValue.makeSymbol(t.getValue())
            ));
            
            nt("symbol", s(Token.Types.SYMBOL).map(
                    t -> CalcValue.makeSymbol(t.getValue().substring(1))
            ));
            
            nt("str", s(Token.Types.STRING).map(
                    t -> CalcValue.make(t.parseStringLiteral())
            ));
            
            nt("bool_literal", or(
                    s(Token.Types.KW_TRUE).map(t -> CalcValue.make(true)),
                    s(Token.Types.KW_FALSE).map(t -> CalcValue.make(false))
            ));
            
            nt("nothing", s(Token.Types.KW_NOTHING).map(
                    t -> NothingValue.INSTANCE
            ));
            
            nt("dict_literal", concatAny(
                    s(Token.Types.LBRACE).ignore(),
                    concatAny(
                        s("dict_literal_item", "first"),
                        concatAny(
                                s(Token.Types.COMMA).ignore(),
                                s("dict_literal_item", "rest")
                        ).repeat()
                    ).maybe(),
                    s(Token.Types.RBRACE).ignore()
            ).map(ctx -> {
                ICalcValue first = ctx.getNamedValue("first", ICalcValue.class);
                if (first == null) {
                    return CalcValue.list(
                            BuiltinCalcValue.DICT,
                            CalcValue.list(
                                    BuiltinCalcValue.LIST
                            )
                    );
                }
                return CalcValue.list(
                        BuiltinCalcValue.DICT,
                        CalcValue.list(
                                BuiltinCalcValue.LIST,
                                ctx.getNamedValue("first", ICalcValue.class),
                                ctx.getNamedValues("rest", ICalcValue.class)
                        )
                );
            }));
            
            nt("dict_literal_item", concatAny(
                    or(
                            concatAny(
                                    s(Token.Types.IDENTIFIER, "ident"),
                                    s(Token.Types.COLON)
                            ),
                            concatAny(
                                    s("expr", "key"),
                                    s(Token.Types.COLON)
                            )
                    ),
                    s("expr", "value")
            ).map(ctx -> {
                ICalcValue key = ctx.getNamedValue("ident", ICalcValue.class);
                if (key == null) {
                    key = ctx.getNamedValue("key", ICalcValue.class);
                } else {
                    key = CalcValue.list(
                            BuiltinCalcValue.QUOTE,
                            key
                    );
                }
                return CalcValue.list(
                        BuiltinCalcValue.LIST,
                        key,
                        ctx.getNamedValue("value", ICalcValue.class)
                );
            }));
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
        IScanner scanner = createScanner(input);
        
        ITokenSequence tokens = new TokenList(scanner.readTokens());
        
        return parseList(tokens);
    }
    
    
    protected IScanner createScanner(String input) {
        IScanner scanner = new CalcScanner();
        scanner.reset(input, "<string>");
        return scanner;
    }
    
    
    @Override
    public ParseResult<ASTNode> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
        return grammar.getSumbol("expr").end().parse(input, ctx);
    }
}

