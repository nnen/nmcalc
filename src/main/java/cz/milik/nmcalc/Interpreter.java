/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.milik.nmcalc;

import cz.milik.nmcalc.ast.ASTNode;
import cz.milik.nmcalc.ast.ASTNodeTypes;
import cz.milik.nmcalc.peg.CalcParser;
import cz.milik.nmcalc.peg.ParseResult;
import cz.milik.nmcalc.utils.IMonad;
import java.util.List;

/**
 *
 * @author jan
 */
public class Interpreter {
    
    private final CalcParser parser = new CalcParser();
    
    private Context defaultContext = Context.createRoot();
    private Environment defaultEnvironment = new Environment();
    
    
    private ListBuilder listBuilder = new ListBuilder();
    
    public ListBuilder getListBuilder() { return listBuilder; }
    
    
    public ICalcValue evaluate(String input) {
        ParseResult<ICalcValue> node = parser.parseList(input);
        if (node.isSuccess()) {
            return eval(node.getValue());
        }
        return new ErrorValue("Syntax error: " + node.toString());
    }
    
    public ICalcValue evaluate(ASTNode node)
    {
        return evaluate(node, defaultContext);
    }
    
    public ICalcValue evaluate(ASTNode node, Context context)
    {
        switch (node.getType())
        {
            case UNKNOWN:
                break;
            case REAL_LITERAL:
                return evaluateRealLiteral(node);
            case VARIABLE:
                return evaluateVariable(node, context);
            case ASSIGNMENT:
                return evaluateAssignment(node, context);
            case ADDITION:
            case SUBTRACTION:
            case MULTIPLICATION:
            case DIVISION:
                return evaluateBinaryOp(node, context);
            default:
                throw new AssertionError(node.getType().name());
        }
        
        throw new AssertionError(node.getType().name());
    }
    
    private ICalcValue evaluateRealLiteral(ASTNode node)
    {
        return CalcValue.make(node.getFloatValue());
    }
    
    private ICalcValue evaluateVariable(ASTNode node, Context context) {
        String varName = node.getLiteralValue().getValue();
        IMonad<ICalcValue> value = context.getVariable(varName);
        return value.unwrap(() -> {
           return new ErrorValue(
                   String.format("Undefined variable: %s.", varName),
                   context
           );
        });
    }
        
    private ICalcValue evaluateBinaryOp(final ASTNode node, Context context) {
        final ASTNodeTypes nodeType = node.getType();
        ICalcValue result = evaluate(node.getChildren().get(0), context);
        for (int i = 1; i < node.getChildren().size(); i++)
        {
            final ICalcValue other = evaluate(node.getChildren().get(i), context);
            
            switch (nodeType) {
                case ADDITION:
                    result = result.add(other, context);
                    break;
                case SUBTRACTION:
                    result = result.subtract(other, context);
                    break;
                case MULTIPLICATION:
                    result = result.multiply(other, context);
                    break;
                case DIVISION:
                    result = result.divide(other, context);
                    break;
                default:
                    result = new ErrorValue();
                    break;
            }
        }
        return result;
    }
    
    private ICalcValue evaluateAssignment(ASTNode node, Context context) {
        ASTNode lhs = node.getChildren().get(0);
        ASTNode rhs = node.getChildren().get(1);
        
        if (lhs.getType() != ASTNodeTypes.VARIABLE) {
            return ErrorValue.formatted(
                    context,
                    "Internal error: cannot assign to %s.",
                    lhs.getType().toString()
            );
        }
        
        ICalcValue value = evaluate(rhs, context);
        context.setVariable(lhs.getLiteralValue().getValue(), value);
        return value;
    }
    
    
    private ExecResult execute(Context aContext) {
        try {
            Context current = aContext;
            Context parent;
            ExecResult result;
            ICalcValue method;
            List<? extends ICalcValue> arguments;

            while (true) {
                result = current.execute(this);

                if (result.getNewContext() != null) {
                    current = result.getNewContext();
                }

                switch (result.getExitCode()) {
                    case CONTINUE:
                        break;

                    case ERROR:
                    case EXIT:
                    case YIELD:
                        return result;

                    case RETURN:
                        parent = current.getParent();
                        if (parent == null) {
                            return result;
                        }
                        parent.setReturnedValue(result.getReturnValue());
                        current = parent;
                        break;

                    case CALL:
                        method = result.getReturnValue();
                        arguments = result.getArguments();
                        current = method.apply(current, arguments);
                        break;

                    default:
                        throw new AssertionError(String.format(
                                "Unknown exit code: %s.",
                                result.getExitCode().toString()
                        ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ExecResult(
                    ExecResult.ExitCodes.ERROR,
                    aContext,
                    new ErrorValue(
                            String.format(
                                    "Internal error (%s: %s).",
                                    e.getClass().getSimpleName(),
                                    e.getMessage()
                            ),
                            aContext,
                            e
                    )
            );
        }
    }
    
    public ICalcValue eval(ICalcValue value) {
        Context current = value.eval(Context.createRoot(defaultEnvironment));
        ICalcValue returnValue = null;
        boolean continueEval = true;
        
        while (continueEval) {
            ExecResult result = execute(current);
            
            if (result.getNewContext() != null) {
                current = result.getNewContext();
            }
            
            switch (result.getExitCode()) {
                case EXIT:
                case ERROR:
                    returnValue = result.getReturnValue();
                    continueEval = false;
                    break;
            }
        }
        
        return returnValue;
    }
 
    
    public Context eval(ICalcValue value, Context ctx) {
        return value.eval(ctx);
    }
    
    public Context apply(ICalcValue value, Context ctx, List<? extends ICalcValue> arguments) {
        return value.apply(ctx, arguments);
    }
    
    public Context applySpecial(ICalcValue value, Context ctx, List<? extends ICalcValue> arguments) {
        return value.applySpecial(ctx, arguments);
    }
}
