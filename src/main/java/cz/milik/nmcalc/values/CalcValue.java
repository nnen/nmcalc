/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc.values;

import cz.milik.nmcalc.BuiltinCalcValue.QuoteValue;
import cz.milik.nmcalc.CalcAnnotation;
import cz.milik.nmcalc.Context;
import cz.milik.nmcalc.ICalcValueAnnotation;
import cz.milik.nmcalc.ListBuilder;
import cz.milik.nmcalc.NMCalcException;
import cz.milik.nmcalc.ReprContext;
import cz.milik.nmcalc.SerializationContext;
import cz.milik.nmcalc.TextLoc;
import cz.milik.nmcalc.text.TextWriter;
import cz.milik.nmcalc.utils.LinkedList;
import cz.milik.nmcalc.utils.Pair;
import cz.milik.nmcalc.utils.StringUtils;
import cz.milik.nmcalc.utils.Utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jan
 */
public abstract class CalcValue implements ICalcValue {
    
    private static final Logger LOGGER = Logger.getLogger(CalcValue.class.getName());
    
    
    public static ICalcValue make(float value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue make(double value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue make(BigDecimal value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue makeFloat(String value) {
        return make(new BigDecimal(value));
    }
    
    public static ICalcValue make(BigInteger value) {
        return new FloatValue(value);
    }
    
    public static ICalcValue make(ICalcValueAnnotation src, BigDecimal value) {
        return new FloatValue(value).addAnnotation(src);
    }
    
    public static ICalcValue make(String value) {
        return new StringValue(value);
    }
    
    public static ICalcValue make(ICalcValueAnnotation src, String value) {
        return new StringValue(value).addAnnotation(src);
    }
    
    public static ICalcValue make(boolean value) {
        if (value) {
            return BoolValue.TRUE;
        }
        return BoolValue.FALSE;
    }
    
    public static ICalcValue make(byte[] value) {
        return new BytesValue(value);
    }
    
    public static ICalcValue makeSymbol(String name) {
        return new SymbolValue(name);
    }
    
    public static ICalcValue list(ICalcValue... items) {
        return new ListValue(items);
    }
    
    public static ICalcValue list(Collection<? extends ICalcValue> items) {
        return new ListValue(items);
    }
    
    public static ICalcValue list(Iterable<? extends ICalcValue> iterable) {
        ListBuilder lb = new ListBuilder();
        for (ICalcValue item : iterable) {
            lb.add(item);
        }
        return lb.makeList();
    }
    
    public static ICalcValue list(ICalcValue head, Collection<? extends ICalcValue> tail) {
        return new ListValue(head, tail);
    }
    
    public static ICalcValue list(ICalcValue head, Iterable<? extends ICalcValue> tail) {
        ListBuilder lb = new ListBuilder();
        lb.add(head);
        lb.addAll(tail);
        return lb.makeList();
    }
    
    public static ICalcValue list(ICalcValue head, ICalcValue tailFirst, Collection<? extends ICalcValue> tailRest) {
        ListBuilder lb = new ListBuilder();
        lb.add(head, tailFirst);
        lb.addAll(tailRest);
        return lb.makeList();
    }

    public static ICalcValue dict() {
        return new MapValue();
    }
    
    public static ICalcValue dict(Iterable<Pair<ICalcValue, ICalcValue>> items) {
        MapValue result = new MapValue();
        for (Pair<ICalcValue, ICalcValue> item : items) {
            try {
                result.setItem(item.getFirst(), item.getSecond());
            } catch (NMCalcException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
        return result;
    }
    
    public static ICalcValue dict(Pair<ICalcValue, ICalcValue>... items) {
        return dict(Arrays.asList(items));
    }
    
    public static ICalcValue quote(ICalcValue value) {
        return new QuoteValue(value);
    }
    
    public static ICalcValue error(Context ctx, Exception e) {
        return error(ctx, e, e.getMessage());
    }
    
    public static ICalcValue error(Context ctx, String fmt, Object... args) {
        return error(ctx, null, fmt, args);
    }
    
    public static ICalcValue error(Context ctx, Exception e, String fmt, Object... args) {
        return new ErrorValue(
                String.format(fmt, args),
                ctx,
                e
        );
    }
    
    public static ICalcValue some(ICalcValue value) {
        return new SomeValue(value);
    }
    
    public static ICalcValue nothing() { return NothingValue.INSTANCE; }
    
    public static ICalcValue wrap(Object obj) {
        return new JavaObjectValue(obj);
    }
    
    
    public static boolean areValuesEqual(ICalcValue a, ICalcValue b, Context ctx) {
        if (a.isError()) {
            return false;
        }
        if (b.isError()) {
            return false;
        }
        return a.isValueEqual(b, ctx);
    }
    
    public static ICalcValue binaryOp(ICalcValue a, ICalcValue b, BiFunction<ICalcValue, ICalcValue, ICalcValue> fn) {
        if (a.isError()) { 
            return a;
        }
        if (b.isError()) {
            return b;
        }
        return fn.apply(a, b);
    }
    
    
    @Override
    public String getRepr(ReprContext ctx) {
        return getClass().getSimpleName();
    }
    
    @Override
    public String getExprRepr(ReprContext ctx) {
        return getRepr(ctx);
    }

    @Override
    public String getApplyRepr(List<? extends ICalcValue> arguments, ReprContext ctx) {
        return getExprRepr(ctx) + "(" + StringUtils.join(", ", arguments.stream().map(arg -> arg.getExprRepr(ctx))) + ")";
    }

    
    @Override
    public void print(TextWriter out, ReprContext ctx) {
        printDebug(out, ctx);
    }

    @Override
    public void printApplyExpr(TextWriter out, List<? extends ICalcValue> arguments, ReprContext ctx) {
        print(out, ctx);
        out.plain("(");
        Utils.forEach(arguments, arg -> {
            arg.print(out, ctx);
        }, () -> {
            out.plain(", ");
        });
        out.plain(")");
    }
    
    @Override
    public void printDebug(TextWriter out, ReprContext ctx) {
        out.plain(getRepr(ctx));
        //out.monospace(getRepr(ctx));
    }
    
    
    @Override
    public Optional<TextLoc> getTextLoc() {
        return getAnnotation(TextLoc.class);
    }
    
    @Override
    public void setTextLoc(TextLoc loc) {
        addAnnotation(loc);
    }
    
    
    @Override
    public int getFlags() {
        return ICalcValue.FLAGS_NONE;
    }
    
    
    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public boolean isSpecialForm() {
        return false;
    }

    @Override
    public boolean isSymbol() {
        return false;
    }
    
    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean isSome() { return false; }
    
    @Override
    public boolean isNothing() { return false; }

    @Override
    public boolean isObject() { return false; }
    
    
    private LinkedList<ICalcValueAnnotation> annotations = EMPTY;
    
    private static final LinkedList<ICalcValueAnnotation> EMPTY = LinkedList.empty();
    
    @Override
    public LinkedList<ICalcValueAnnotation> getAnnotations() {
        return annotations;
    }
    
    @Override
    public ICalcValue addAnnotation(ICalcValueAnnotation value) {
        annotations = annotations.add(value);
        return this;
    }
    
    @Override
    public <T extends ICalcValueAnnotation> Optional<T> getAnnotation(Class<T> cls) {
        return annotations.mapFirst(item -> {
            if (cls.isInstance(item)) {
                return Optional.of(cls.cast(item));
            }
            return Optional.empty();
        });
    }

    
    @Override
    public Optional<String> getHelp() {
        Optional<CalcAnnotation.HelpAnnotation> help = getAnnotation(CalcAnnotation.HelpAnnotation.class);
        if (help.isPresent()) {
            return Optional.of(help.get().getHelp());
        }
        return getHelpInner();
    }
    
    protected Optional<String> getHelpInner() {
        return Optional.empty();
    }
    
    @Override
    public void setHelp(String help) {
        Optional<CalcAnnotation.HelpAnnotation> annotation = getAnnotation(CalcAnnotation.HelpAnnotation.class);
        if (!annotation.isPresent()) {
            annotation = Optional.of(CalcAnnotation.help(help));
            addAnnotation(annotation.get());
        }
        annotation.get().setHelp(help);
    }
    
    @Override
    public boolean isHelp() {
        return false;
        //return getAnnotation(CalcAnnotation.IsHelp.class).isPresent();
    }
    
    protected Optional<String> makeHelp(String signature, String... paragraphs) {
        StringBuilder sb = new StringBuilder();
        sb.append("**`");
        sb.append(signature);
        sb.append("`**");
        for (String par : paragraphs) {
            sb.append("\n\n");
            sb.append(par);
        }
        return Optional.of(sb.toString());
    }
    
    
    @Override
    public ICalcValue unwrap(Context ctx) {
        return this;
    }
    
    
    @Override
    public Context getAttribute(SymbolValue attrName, Context ctx) {
        ctx.setReturnedValue(ErrorValue.formatted(
                ctx,
                "%s doesn't have attribute '%s'.",
                getRepr(ctx.getReprContext()),
                attrName.getRepr(ctx.getReprContext())
        ));
        return ctx;
    }
    
    @Override
    public Context setAttribute(SymbolValue attrName, ICalcValue value, Context ctx) {
        ctx.setReturnedValue(ErrorValue.formatted(
                ctx,
                "Cannot assign attribute '%s' to %s.",
                attrName.getRepr(ctx.getReprContext()),
                getRepr(ctx.getReprContext())
        ));
        return ctx;
    }
    
    
    @Override
    public boolean getBooleanValue() { return true; }
    
    
    @Override
    public ICalcValue toFloat(Context ctx) {
        return ErrorValue.formatted("Cannot convert %s to float.", getRepr(ctx.getReprContext()));
    }
    
    @Override
    public BigDecimal getDecimalValue() {
        return BigDecimal.ZERO;
    }
    
    
    @Override
    public ICalcValue toStringValue(Context ctx) {
        return new StringValue(getRepr(ctx.getReprContext()));
    }
    
    @Override
    public String getStringValue(Context ctx) {
        return getRepr(ctx.getReprContext());
    }

    
    @Override
    public ICalcValue toSymbolValue(Context ctx) {
        return ErrorValue.formatted(
                "%s cannot be converted to a symbol.",
                getRepr(ctx.getReprContext())
        );
    }
    
    
    @Override
    public boolean isValueEqual(ICalcValue other, Context ctx) {
        return Objects.equals(this, other);
    }
    
    @Override
    public int compareValue(ICalcValue other, Context ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    @Override
    public ICalcValue negate(Context ctx) {
        return CalcValue.error(
                ctx,
                "Cannot negate %s.",
                getRepr(ctx.getReprContext())
        );
    }
    
    @Override
    public ICalcValue add(ICalcValue other, Context ctx) {
        if (other.isError()) {
            return other;
        }
        return CalcValue.error(
                ctx,
                "%s cannot be added to %s.",
                getRepr(ctx.getReprContext()),
                other.getRepr(ctx.getReprContext())
        );
    }
    
    @Override
    public ICalcValue subtract(ICalcValue other, Context ctx) {
        return CalcValue.error(
                ctx,
                "Cannot subtract from %s.",
                getRepr(ctx.getReprContext())
        );
    }
    
    @Override
    public ICalcValue multiply(ICalcValue other, Context ctx) {
        return CalcValue.error(
                ctx,
                "Cannot multiply %s.",
                getRepr(ctx.getReprContext())
        );
    }
    
    @Override
    public ICalcValue divide(ICalcValue other, Context ctx) {
        return CalcValue.error(
                ctx,
                "Cannot divide %s.",
                getRepr(ctx.getReprContext())
        );
    }

    
    @Override
    public Context bitwiseAnd(ICalcValue other, Context ctx) {
        ctx.setReturnedError(
                "Cannot perform bitwise AND on %s and %s.",
                getRepr(ctx.getReprContext()),
                other.getRepr(ctx.getReprContext())
        );
        return ctx;
    }

    @Override
    public Context bitwiseOr(ICalcValue other, Context ctx) {
        ctx.setReturnedError(
                "Cannot perform bitwise OR on %s and %s.",
                getRepr(ctx.getReprContext()),
                other.getRepr(ctx.getReprContext())
        );
        return ctx;
    }

    @Override
    public Context bitwiseXor(ICalcValue other, Context ctx) {
        ctx.setReturnedError(
                "Cannot perform bitwise XOR on %s and %s.",
                getRepr(ctx.getReprContext()),
                other.getRepr(ctx.getReprContext())
        );
        return ctx;
    }
    
    
    @Override
    public boolean hasLength() {
        return false;
    }

    @Override
    public int length() {
        return 1;
    }
    
    @Override
    public ICalcValue getItem(int index) {
        return new ErrorValue(String.format(
                "%s value doesn't support indexing.",
                getClass().getSimpleName()
        ));
    }
    
    @Override
    public Context getItem(Context ctx, ICalcValue index) {
        ctx.setReturnedError(
                "%s value doesn't support indexing.",
                getRepr(ctx.getReprContext())
        );
        return ctx;
    }

    @Override
    public Context setItem(Context ctx, ICalcValue index, ICalcValue value) {
        ctx.setReturnedError(
                "%s value doesn't support indexing.",
                getRepr(ctx.getReprContext())
        );
        return ctx;
    }
    
    @Override
    public void setItem(ICalcValue index, ICalcValue value) throws NMCalcException {
        throw new NMCalcException(String.format(
                "%s doesn't support item assignment.",
                getClass().getSimpleName()
        ));
    }
    
    @Override
    public Context getHead(Context ctx) {
        if (!hasLength()) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    "Cannot get the head of %s.",
                    getRepr(ctx.getReprContext())
            ));
            return ctx;
        }
        
        if (length() < 1) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    "Cannot get the head of %s, because it is empty.",
                    getRepr(ctx.getReprContext())
            ));
            return ctx;
        }
        
        ctx.setReturnedValue(getItem(0));
        return ctx;
    }
    
    @Override
    public Context getTail(Context ctx) {
        if (!hasLength()) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    "Cannot get the head of %s.",
                    getRepr(ctx.getReprContext())
            ));
            return ctx;
        }
        
        /*
        if (length() < 1) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    "Cannot get the tail of %s, because it is empty.",
                    getRepr(ctx.getReprContext())
            ));
            return ctx;
        }
        */
        
        ListBuilder lb = new ListBuilder();
        for (int i = 1; i < length(); i++) {
            lb.add(getItem(i));
        }
        
        ctx.setReturnedValue(lb.makeList());
        return ctx;
    }
    
    @Override
    public Context unpack(Context ctx) {
        ctx.setReturnedValue(CalcValue.error(
                ctx,
                "Cannot unpack %s.",
                getRepr(ctx.getReprContext())
        ));
        return ctx;
    }
    
    
    @Override
    public Context eval(Context ctx) {
        ctx.setReturnedValue(this);
        return ctx;
    }
    
    @Override
    public Context apply(Context ctx, List<? extends ICalcValue> arguments) {
        try {
            return applyInner(ctx, arguments);
        } catch (NMCalcException e) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    e.getContext(),
                    "%s: %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            return ctx;
        }
    }
    
    protected Context applyInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        ctx.setReturnedValue(ErrorValue.formatted("%s %s cannot be applied.", getClass().getSimpleName(), getRepr(ctx.getReprContext())));
        return ctx;
    }
    
    @Override
    public Context applySpecial(Context ctx, List<? extends ICalcValue> arguments) {
        try {
            return applySpecialInner(ctx, arguments);
        } catch (NMCalcException e) {
            ctx.setReturnedValue(ErrorValue.formatted(
                    e.getContext(),
                    "%s: %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            ));
            return ctx;
        }
    }
    
    protected Context applySpecialInner(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        return applyInner(ctx, arguments);
    }

    @Override
    public Context unapply(Context ctx, ICalcValue value) {
        try {
            return unapplyInner(ctx, value);
        } catch (NMCalcException e) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    e,
                    e.getMessage()
            ));
            return ctx;
        } catch (Exception e) {
            ctx.setReturnedValue(CalcValue.error(
                    ctx,
                    e,
                    "Internal error: " + e.getMessage()
            ));
            return ctx;
        }
    }
    
    protected Context unapplyInner(Context ctx, ICalcValue value) throws NMCalcException {
        if (isValueEqual(value, ctx)) {
            ctx.setReturnedValue(CalcValue.some(CalcValue.list()));
        } else {
            ctx.setReturnedValue(CalcValue.nothing());
        }
        return ctx;
    }
    
    
    @Override
    public Context bind(Context ctx, ICalcValue obj) {
        ctx.setReturnedValue(this);
        return ctx;
    }
    
    
    @Override
    public Context substitute(Context ctx, ICalcValue value, ICalcValue replacement) {
        if (isError()) {
            ctx.setReturnedValue(this);
        } else if (value.isError()) {
            ctx.setReturnedValue(value);
        } else if (replacement.isError()) {
            ctx.setReturnedValue(replacement);
        } else if (isValueEqual(value, ctx)) {
            ctx.setReturnedValue(replacement);
        } else {
            ctx.setReturnedValue(this);
        }
        return ctx;
    }
    
    
    @Override
    public ICalcValue withNonError(Function<ICalcValue, ICalcValue> function) {
        return function.apply(this);
    }
    
    @Override
    public ICalcValue withNonError(ICalcValue other, BiFunction<ICalcValue, ICalcValue, ICalcValue> function) {
        return other.withNonError(otherNonError -> {
           return function.apply(this, otherNonError);
        });
    }

    
    @Override
    public Optional<UUID> getId() {
        return Optional.empty();
    }
    
    
    @Override
    public boolean serialize(SerializationContext ctx) throws NMCalcException {
        return false;
    }
    
    @Override
    public Context serialize(Context ctx, SerializationContext serCtx) {
        try {
            if (serialize(serCtx)) {
                ctx.setReturnedValue(this);
                return ctx;
            }
        } catch (NMCalcException ex) {
            ctx.setReturnedValue(CalcValue.error(ctx, ex));
            return ctx;
        }
        
        try {
            serializeInner(ctx, serCtx);
            return ctx;
        } catch (NMCalcException ex) {
            ctx.setReturnedValue(CalcValue.error(ctx, ex));
            return ctx;
        }
    }
    
    protected Context serializeInner(Context ctx, SerializationContext serCtx) throws NMCalcException {
        ctx.setReturnedValue(ErrorValue.formatted(
                "%s %s cannot be serialized.",
                getClass().getSimpleName(),
                getRepr(ctx.getReprContext())));
        return ctx;
    }
    
    
    protected void invalidArgumentCount(Context ctx, List<? extends ICalcValue> arguments) throws NMCalcException {
        throw new NMCalcException(String.format("Invalid argument count: %d.", arguments.size()), ctx);
    }
    
    protected void assertArgCount(Context ctx, List<? extends ICalcValue> args, int expectedCount, int... expectedCountRest) throws NMCalcException {
        if (args.size() == expectedCount) {
            return;
        }
        
        for (int count : expectedCountRest) {
            if (args.size() == count) {
                return;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(expectedCount));
        if (expectedCountRest.length > 0) {
            for (int i = 0; i< expectedCountRest.length - 1; i++) {
                sb.append(", ");
                sb.append(Integer.toString(expectedCountRest[i]));
            }
            sb.append(" or ");
            sb.append(Integer.toString(expectedCountRest[expectedCountRest.length - 1]));
            
            throw NMCalcException.format(
                    ctx,
                    "%s cannot be applied to %d argument(s). %s argument(s) expected.",
                    getRepr(ctx.getReprContext()),
                    args.size(),
                    sb.toString()
            );
        }
    }
    
    protected boolean checkArguments(Context ctx, List<? extends ICalcValue> arguments, int expectedCount, int... expectedCountRest) {
        if (arguments.size() == expectedCount) {
            return true;
        }
        
        for (int count : expectedCountRest) {
            if (arguments.size() == count) {
                return true;
            }
        }
        
        String countStr = Integer.toString(expectedCount);
        
        if (expectedCountRest.length > 0) {
            for (int i = 0; i < expectedCountRest.length - 1; i++) {
                countStr += ", " + Integer.toString(expectedCountRest[i]);
            }
            countStr += " or " + Integer.toString(expectedCountRest[expectedCountRest.length - 1]);
        }
        
        ctx.setReturnedValue(ErrorValue.formatted(
                "%s cannot be applied to %d argument(s). Exactly %s argument(s) are expected.",
                getRepr(ctx.getReprContext()),
                arguments.size(),
                countStr
        ));
        
        return false;
    }
    
    protected boolean checkArguments(Context ctx, List<? extends ICalcValue> arguments, List<FunctionValue.ArgumentInfo> argInfo) {
        if (arguments.size() == argInfo.size()) {
            return true;
        }
        
        if (argInfo.size() > 0) {
            FunctionValue.ArgumentInfo lastArg = argInfo.get(argInfo.size() - 1);
            if (lastArg.isVarArg() && (arguments.size() >= (argInfo.size() - 1))) {
                return true;
            } else {
                ctx.setReturnedValue(ErrorValue.formatted(
                    "%s cannot be applied to %d argument(s). At least %d argument(s) are expected.",
                    getRepr(ctx.getReprContext()),
                    arguments.size(),
                    argInfo.size() - 1
                ));
                return false;
            }
        }
        
        ctx.setReturnedValue(ErrorValue.formatted(
                "%s cannot be applied to %d argument(s). %d argument(s) are expected.",
                getRepr(ctx.getReprContext()),
                arguments.size(),
                argInfo.size()
        ));
        
        return false;
    }
    
    protected void requireLength(Context ctx, ICalcValue value) throws NMCalcException {
        if (!value.hasLength()) {
            throw new NMCalcException("Expected a value with length.");
        }
    }
    
    protected void requireLength(Context ctx, ICalcValue value, int length) throws NMCalcException {
        requireLength(ctx, value);
        if (value.length() != length) {
            throw new NMCalcException(String.format(
                    "Expected a value of length %d. Got a value of length %d.",
                    length,
                    value.length()
            ));
        }
    }
    
    public static SymbolValue asSymbol(ICalcValue value, Context ctx) throws NMCalcException {
        if (value == null) {
            throw new NMCalcException("Expected a symbol, got null.");
        }
        
        if (value instanceof SymbolValue) {
            return (SymbolValue)value;
        }
        
        throw new NMCalcException(String.format(
                "Expected a symbol, got: %s.",
                value.getRepr(ctx.getReprContext())
        ));
    }
    
    public static List<? extends SymbolValue> asSymbolList(ICalcValue value, Context ctx) throws NMCalcException {
        if (value == null) {
            throw new NMCalcException("Expected a list of symbols, got null.");
        }
        
        if (value instanceof ListValue) {
            ListValue listValue = (ListValue)value;
            List<SymbolValue> result = new ArrayList();
            for (ICalcValue item : listValue.getValues()) {
                result.add(asSymbol(item, ctx));
            }
            return result;
        }
        
        throw new NMCalcException(String.format(
                "Expected a list of symbols, got: %s.",
                value.getRepr(ctx.getReprContext())
        ));
    }
    
    
    public static SymbolValue asSymbol(ICalcValue value, Context ctx, int argPos) throws NMCalcException {
        if (value == null) {
            throw NMCalcException.format(
                    ctx,
                    "Expected a symbol as %dth argument, got null.",
                    argPos);
        }
        
        if (value instanceof SymbolValue) {
            return (SymbolValue)value;
        }
        
        throw NMCalcException.format(
                ctx,
                "Expected a symbol as %dth argument, got: %s.",
                argPos, value.getRepr(ctx.getReprContext()));
    }
    
    public static List<ICalcValue> asList(ICalcValue value, Context ctx, int argPos) throws NMCalcException {
        if (value == null) {
            throw NMCalcException.format(
                    ctx,
                    "Expected a list of symbols as %dth argument, got null.",
                    argPos);
        }
        
        if (value.hasLength()) {
            List<ICalcValue> result = new ArrayList();
            for (int i = 0; i < value.length(); i++) {
                result.add(value.getItem(i));
            }
            return result;
        }
        
        throw NMCalcException.format(
                ctx,
                "Expected a list of symbols as %dth argument, got: %s.",
                argPos, value.getRepr(ctx.getReprContext()));
    }
}
