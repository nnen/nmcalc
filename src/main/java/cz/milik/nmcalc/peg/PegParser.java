/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.peg;

import cz.milik.nmcalc.parser.Token;
import cz.milik.nmcalc.utils.Pair;
import cz.milik.nmcalc.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author jan
 */
public abstract class PegParser<T> {

    @Override
    public String toString() {
        String name = getName();
        if (name != null) {
            return String.format(
                    "%s{ name=%s }",
                    getClass().getSimpleName(),
                    name
            );
        }
        return getClass().getSimpleName();
    }
    
    public String getName() {
        return null;
    }
    
    
    public boolean isIgnored() {
        return false;
    }
    
    
    private String shortDescription;
    
    public String getShortDescription() {
        if (shortDescription != null) {
            return shortDescription;
        }
        return toString();
    }
    
    public void setShortDescription(String value) {
        shortDescription = value;
    }
    
    
    public ParseResult<T> parse(ITokenSequence input, IPegContext ctx) {
        IPegContext childContext;
        
        if (ctx.isDebugModeOn()) {
            try {
                childContext = ctx.createChild(this);
            } catch (PegException e) {
                throw new RuntimeException(e);
            }
            
            try {
                ParseResult<T> r = parseInContext(input, childContext);
                childContext.setResult(r);
                return r;
            } catch (PegException e) {
                throw new RuntimeException(e);
            }
        }
        
        try {
            childContext = ctx.createChild(this);
        } catch (PegException e) {
            return new ParseResult(
                ctx,
                true,
                input,
                e,
                input.get(0)
            );
        }
        
        try {
            ParseResult<T> r = parseInContext(input, childContext);
            childContext.setResult(r);
            return r;
        } catch (PegSyntaxError e) {
            e.printStackTrace();
            return new ParseResult(
                childContext,
                false,
                input,
                e,
                input.get(0)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ParseResult(
                childContext,
                true,
                input,
                e,
                input.get(0)
            );
        }
    }
    
    public abstract ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException;

    
    public <U> PegParser<U> map(Function<T, U> fn) {
        return new MapParser(this, fn);
    }

    public <U> PegParser<U> map(BiFunction<T, IPegContext, U> fn) {
        return new MapParser(this, fn);
    }
    
    public PegParser<T> maybe() {
        return maybe(null);
    }
    
    public PegParser<T> maybe(T aDefaultValue) {
        return new MaybeParser(this, aDefaultValue);
    }
    
    public PegParser<List<T>> repeat() {
        return new RepeatParser(this);
    }
    
    public PegParser<List<T>> repeat(PegParser<?> glue, Class<T> cls) {
        return concatAny(
                concatAny(
                    this.named("__items"),
                    concat(
                            glue.ignore(),
                            this.named("__items")
                    ).repeat()
                ).maybe()
        ).map(ctx -> {
            return ctx.getNamedValues("__items", cls);
        });
    }
    
    public PegParser<List<T>> repeatPlus(PegParser<?> glue, Class<T> cls) {
        return concatAny(
                this.named("__items"),
                concat(
                        glue.ignore(),
                        this.named("__items")
                ).repeat()
        ).map(ctx -> {
            return ctx.getNamedValues("__items", cls);
        });
    }
    
    public PegParser<List<T>> repeat(PegParser<?> prefix, PegParser<?> glue, PegParser<?> postfix, Class<T> cls) {
        return flatten(concat(
                prefix.ignore(),
                this.repeat(glue, cls),
                postfix.ignore()
        ));
    }
    
    public static <T> PegParser<List<T>> flatten(PegParser<List<List<T>>> parser) {
        return new FlattenParser(parser);
    }
    
    public PegParser<T> test(Predicate<T> condition, String description) {
        ConditionalParser p = new ConditionalParser(this, condition);
        p.setShortDescription(description);
        return p;
    }

    public PegParser<T> named(String name) {
        return new NamedParser(this, name);
    }
    
    public PegParser<T> named(String name, Class<?> aClass) {
        return new NamedParser(this, name, aClass);
    }
    
    public static <T> PegParser<T> or(PegParser<T>... parsers) {
        return new ChoiceParser<>(Arrays.asList(parsers));
    }
    
    public PegParser<T> or(PegParser<T> other) {
        return new ChoiceParser<>(this, other);
    }
    
    public static <T> PegParser<List<T>> concat(PegParser<T>... parsers) {
        return new SequenceParser<>(parsers);
    }
    
    public PegParser<List<T>> concat(PegParser<T> other) {
        return concat(this, other);
    }
    
    public static PegParser<IPegContext> concatAny(PegParser<?>... parsers) {
        return new SequenceCtxParser(parsers);
    }
    
    public static <U, V> PegParser<Pair<U, V>> pair(PegParser<U> first, PegParser<V> second) {
        return new PairParser(first, second);
    }
    
    public <U> PegParser<Pair<T, U>> pair(PegParser<U> second) {
        return new PairParser(this, second);
    }
    
    public static <U> PegParser<U> value(U val) {
        return new ValueParser(val);
    }
    
    public PegParser<T> end() {
        return new EndParser(this);
    }
    
    public <U> PegParser<U> ignore() {
        return new IgnoreParser(this);
    }
    
    
    public static class ValueParser<T> extends PegParser<T> {
        private final T value;
        
        public ValueParser(T value) {
            this.value = value;
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            return new ParseResult(ctx, value, input);
        }
    }
    
    
    public static abstract class UnaryParser<T, U> extends PegParser<T> {

        protected PegParser<U> innerParser;

        public UnaryParser(PegParser<U> anInnerParser) {
            innerParser = anInnerParser;
        }
        
        protected ParseResult<U> innerParse(ITokenSequence input, IPegContext ctx) throws PegException {
            return innerParser.parse(input, ctx);
        }

    }
    
    public static class MapParser<T, U> extends UnaryParser<T, U> {

        protected BiFunction<U, IPegContext, T> mapFunction;
        
        public MapParser(PegParser<U> anInnerParser, BiFunction<U, IPegContext, T> aMapFunction) {
            super(anInnerParser);
            mapFunction = aMapFunction;
        }
        
        public MapParser(PegParser<U> anInnerParser, final Function<U, T> aMapFunction) {
            this(anInnerParser, (value, ctx) -> aMapFunction.apply(value));
        }
        
        @Override
        public ParseResult<T> parseInContext(final ITokenSequence input, final IPegContext ctx) throws PegException {
            ctx.startNewScope();
            
            ParseResult<U> result = innerParse(input, ctx);
            if (!result.isSuccess()) {
                return result.castFailure();
            }
            T value = mapFunction.apply(result.getValue(), ctx);
            if (value == null) {
                return new ParseResult(
                        ctx,
                        true,
                        input,
                        "Expected non-null result.",
                        input.get(0)
                );
            }
            return new ParseResult(ctx, value, result.getRest());
        }

    }

    public static class MaybeParser<T> extends UnaryParser<T, T> {

        private final T defaultValue;

        public MaybeParser(PegParser<T> anInnerParser, T aDefaultValue) {
            super(anInnerParser);
            defaultValue = aDefaultValue;
        }
        
        public MaybeParser(PegParser<T> anInnerParser) {
            this(anInnerParser, null);
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<T> result = innerParse(input, ctx);
            if (result.isError() || result.isSuccess()) {
                return result;
            }
            return new ParseResult<>(ctx, defaultValue, input);
        }
        
    }
    
    public static class RepeatParser<T> extends UnaryParser<List<T>, T> {

        public RepeatParser(PegParser<T> anInnerParser) {
            super(anInnerParser);
        }
        
        @Override
        public ParseResult<List<T>> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            List<T> result = new ArrayList<>();
            ITokenSequence rest = input;
            
            while (true) {
                ctx.startNewScope();
                ParseResult<T> item = innerParse(rest, ctx);
                if (item.isError()) {
                    return item.castFailure();
                } else if (!item.isSuccess()) {
                    break;
                }
                result.add(item.getValue());
                rest = item.getRest();
                ctx.endScope();
            }

            return new ParseResult(ctx, result, rest);
        }

    }
    
    public static class RepeatJoinedParser<T> extends UnaryParser<List<T>, T> {

        private final PegParser<?> prefix;
        private final PegParser<?> postfix;
        private final PegParser<?> glueParser;
        
        public RepeatJoinedParser(PegParser<T> anInnerParser, PegParser<?> glue) {
            super(anInnerParser);
            this.glueParser = glue;
            this.prefix = null;
            this.postfix = null;
        }
        
        public RepeatJoinedParser(PegParser<T> anInnerParser, PegParser<?> aPrefix, PegParser<?> glue, PegParser<?> aPostfix) {
            super(anInnerParser);
            this.glueParser = glue;
            this.prefix = aPrefix;
            this.postfix = aPostfix;
        }
        
        @Override
        public ParseResult<List<T>> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            List<T> result = new ArrayList();
            ITokenSequence rest = input;
            
            ParseResult<?> delimiter = null;
            if (prefix != null) {
                delimiter = prefix.parse(rest, ctx);
                if (!delimiter.isSuccess()) {
                    return delimiter.castFailure();
                }
                rest = delimiter.getRest();
            }
            
            ParseResult<T> item = innerParse(rest, ctx);
            if (item.isError()) {
                return item.castFailure();
            } else if (!item.isSuccess()) {
                return new ParseResult(ctx, result, rest);
            }
            result.add(item.getValue());
            
            while (true) {
                ParseResult<?> glue = glueParser.parse(rest, ctx);
                if (glue.isError()) {
                    return glue.castFailure();
                } else if (!glue.isSuccess()) {
                    break;
                }
                
                item = innerParse(glue.getRest(), ctx);
                if (item.isError()) {
                    return item.castFailure();
                } else if (!item.isSuccess()) {
                    return item.makeError();
                }
                
                result.add(item.getValue());
                rest = item.getRest();
            }
            
            if (postfix != null) {
                delimiter = postfix.parse(rest, ctx);
                if (delimiter.isError()) {
                    return delimiter.castFailure();
                } else if (!delimiter.isSuccess()) {
                    return delimiter.makeError();
                }
                rest = delimiter.getRest();
            }
            
            return new ParseResult(ctx, result, rest);
        }
        
    }
    
    public static class FlattenParser<T> extends UnaryParser<List<T>, List<List<T>>> {

        public FlattenParser(PegParser<List<List<T>>> anInnerParser) {
            super(anInnerParser);
        }
        
        @Override
        public ParseResult<List<T>> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<List<List<T>>> parsed = innerParse(input, ctx);
            if (!parsed.isSuccess()) {
                return parsed.castFailure();
            }
            
            List<T> result = new ArrayList();
            for (List<T> part : parsed.getValue()) {
                result.addAll(part);
            }
            
            return new ParseResult(ctx, result, parsed.getRest());
        }
        
    }
    

    public static class ConditionalParser<T> extends UnaryParser<T, T> {

        private final Predicate<T> condition;

        public ConditionalParser(PegParser<T> anInnerParser, Predicate<T> condition) {
            super(anInnerParser);
            this.condition = condition;
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<T> result = innerParse(input, ctx);
            if (!result.isSuccess()) {
                return result;
            }
            if (condition.test(result.getValue())) {
                return result;
            }
            return new ParseResult(ctx, false, input, "Condition failed.", input.get(0));
        }

    }

    public static class NamedParser<T> extends UnaryParser<T, T> {

        private String name;
        private Class<?> cls;

        @Override
        public String getName() {
            return name;
        }
        
        public NamedParser(PegParser<T> anInnerParser, String aName) {
            super(anInnerParser);
            name = aName;
        }
        
        public NamedParser(PegParser<T> anInnerParser, String aName, Class<?> aClass) {
            this(anInnerParser, aName);
            cls = aClass;
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<T> result = innerParse(input, ctx);
            if (result.isSuccess()) {
                if (cls == null) {
                    cls = result.getValue().getClass();
                }
                ctx.setNamedValue(name, cls, result.getValue());
            }
            return result;
        }
        
    }
    
    public static class EndParser<T> extends UnaryParser<T, T> {

        public EndParser(PegParser<T> anInnerParser) {
            super(anInnerParser);
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<T> result = innerParse(input, ctx);
            if (!result.isSuccess()) {
                return result;
            }
            if (!result.getRest().isEmpty()) {
                Token t = result.getRest().get(0);
                return new ParseResult(
                        ctx,
                        false,
                        input,
                        String.format("Expected end of input, but found token %s.", t.toString()),
                        t
                );
            }
            return result;
        }
        
    }
    
    public static class IgnoreParser<T, U> extends UnaryParser<T, U> {
        public IgnoreParser(PegParser<U> anInnerParser) {
            super(anInnerParser);
        }
        
        @Override
        public boolean isIgnored() {
            return true;
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            return innerParse(input, ctx).ignore();
        }
    }
    
    
    public static abstract class BinaryParser<T, U, V> extends PegParser<T> {
        
        protected final PegParser<U> firstParser;
        protected final PegParser<V> secondParser;
        
        public BinaryParser(PegParser<U> firstParser, PegParser<V> secondParser) {
            this.firstParser = firstParser;
            this.secondParser = secondParser;
        }
        
    }
    
    public static class PairParser<U, V> extends BinaryParser<Pair<U, V>, U, V> {

        public PairParser(PegParser<U> firstParser, PegParser<V> secondParser) {
            super(firstParser, secondParser);
        }

        @Override
        public ParseResult<Pair<U, V>> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<U> first = firstParser.parse(input, ctx);
            if (!first.isSuccess()) {
                return first.castFailure();
            }
            
            ParseResult<V> second = secondParser.parse(first.getRest(), ctx);
            if (!second.isSuccess()) {
                return second.makeError();
            }
            
            return new ParseResult(
                    ctx,
                    Pair.of(first.getValue(), second.getValue()),
                    second.getRest());
        }
        
    }
    
    
    public static abstract class NaryParser<T, U> extends PegParser<T> {

        protected final List<PegParser<U>> innerParsers;
        
        public NaryParser(List<PegParser<U>> innerParsers) {
            this.innerParsers = innerParsers;
        }
        
        public NaryParser(PegParser<U>... parsers) {
            innerParsers = Arrays.asList(parsers);
        }

    }

    public static class ChoiceParser<T> extends NaryParser<T, T> {

        public ChoiceParser(List<PegParser<T>> innerParsers) {
            super(innerParsers);
        }

        public ChoiceParser(PegParser<T>... parsers) {
            super(parsers);
        }
        
        @Override
        public ParseResult<T> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            for (PegParser<T> parser : innerParsers) {
                ParseResult<T> result = parser.parse(input, ctx);
                if (result.isSuccess()) {
                    return result;
                } else if (result.isError()) {
                    return result;
                }
            }
            return new ParseResult<>(
                    ctx,
                    false,
                    input,
                    String.format(
                            "Expected one of: %s.",
                            StringUtils.join(", ", innerParsers.stream().map(parser -> parser.toString()).iterator())
                    ),
                    input.get(0)
            );
        }
        
    }
    
    public static class SequenceParser<T> extends NaryParser<List<T>, T> {
        
        public SequenceParser(List<PegParser<T>> innerParsers) {
            super(innerParsers);
        }

        public SequenceParser(PegParser<T>... parsers) {
            super(parsers);
        }
        
        @Override
        public ParseResult<List<T>> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<T> item;
            List<T> result = new ArrayList<>();
            ITokenSequence rest = input;
            
            for (PegParser<T> child : innerParsers) {
                item = child.parse(rest, ctx);
                if (!item.isSuccess()) {
                    return item.castFailure();
                }
                if (!item.isIgnored()) {
                    result.add(item.getValue());
                }
                rest = item.getRest();
            }
            
            return new ParseResult(ctx, result, rest);
        }

    }
    
    public static class SequenceCtxParser extends PegParser<IPegContext> {

        private final List<PegParser<?>> innerParsers;
        
        public SequenceCtxParser(List<PegParser<?>> parsers) {
            innerParsers = parsers;
        }

        public SequenceCtxParser(PegParser<?>... parsers) {
            innerParsers = Arrays.asList(parsers);
        }
        
        @Override
        public ParseResult<IPegContext> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<?> item;
            ITokenSequence rest = input;
            
            for (PegParser<?> parser : innerParsers) {
                item = parser.parse(rest, ctx);
                if (!item.isSuccess()) {
                    return item.castFailure();
                }
                rest = item.getRest();
            }
            
            return new ParseResult(ctx, ctx, rest);
        }
        
    }

    
    public static class TokenParser extends PegParser<Token> {

        //private Token.Types tokenType;

        @Override
        public ParseResult<Token> parseInContext(ITokenSequence input, IPegContext ctx) {
            //if (input.isEmpty() || input.get(0).getType() != tokenType) {
            if (input.isEmpty()) {
                return new ParseResult(
                        ctx,
                        false,
                        input,
                        "Expected a token, found EOF.",
                        input.get(0));
            }
            return new ParseResult(ctx, input.get(0), input.advance(1));
        }

    }
    
    public static TokenParser TOKEN_PARSER = new TokenParser();
    
    
    public static class TokenTypeParser extends UnaryParser<Token, Token> {

        private final Token.Types tokenType;
        
        public TokenTypeParser(Token.Types tokenType, PegParser<Token> anInnerParser) {
            super(anInnerParser);
            this.tokenType = tokenType;
        }
        
        @Override
        public ParseResult<Token> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<Token> result = innerParse(input, ctx);
            if (!result.isSuccess()) {
                return result;
            }
            if (result.getValue().getType() != tokenType) {
                return new ParseResult(
                        ctx,
                        false,
                        input,
                        String.format("Expected token type %s. Got %s.", tokenType.toString(), result.getValue().getType().toString()),
                        result.getValue());
            }
            return result;
        }
        
    }
    
    public static class TokenTypesParser extends UnaryParser<Token, Token> {
        private final EnumSet<Token.Types> tokenTypes;
        
        public TokenTypesParser(PegParser<Token> anInnerParser, Token.Types first, Token.Types... rest) {
            super(anInnerParser);
            tokenTypes = EnumSet.of(first, rest);
        }
        
        @Override
        public ParseResult<Token> parseInContext(ITokenSequence input, IPegContext ctx) throws PegException {
            ParseResult<Token> result = innerParse(input, ctx);
            if (!result.isSuccess()) {
                return result;
            }
            if (!tokenTypes.contains(result.getValue().getType())) {
                return new ParseResult(
                        ctx,
                        false,
                        input,
                        String.format(
                                "Expected on of %s. Got %s.",
                                StringUtils.join(", ", tokenTypes.stream().map(tt -> tt.name())),
                                result.getValue().getType().name()
                        ),
                        result.getValue()
                );
            }
            return result;
        }
    }

}


