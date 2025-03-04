package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.state.PrgState;
import model.types.IType;
import model.values.IValue;

public class PrintStatement implements IStatement {
    private IExpression expression;
    public PrintStatement(IExpression expression) {
        this.expression = expression;
    }
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException
    {
        IValue v = expression.eval(state.getSymTable(), state.getHeap());
        state.getOutList().add(v);
        return null;
    }
    public String toString() {
        return "print(" + expression + ")";
    }
    public IStatement deepCopy() {
        return new PrintStatement(expression.deepCopy());
    }
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        expression.typeCheck(typeEnvironment);
        return typeEnvironment;
    }
}
