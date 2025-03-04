package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.BoolType;
import model.types.IType;
import model.values.BoolValue;
import model.values.IValue;

public class NotExpression implements IExpression {
    private IExpression expression;

    public NotExpression(IExpression expression) {
        this.expression = expression;
    }

    @Override
    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ExpressionException, ADTException {
        IValue value = expression.eval(symTable, heap);
        if (!value.getType().equals(new BoolType()))
            throw new ExpressionException("Expression is not a boolean.");
        return new BoolValue(!((BoolValue) value).getValue());
    }

    @Override
    public IExpression deepCopy() {
        return new NotExpression(expression.deepCopy());
    }

    @Override
    public String toString() {
        return "!(" + expression + ")";
    }

    @Override
    public IType typeCheck(MyIMap<String, IType> typeEnv) throws ExpressionException, ADTException {
        IType type = expression.typeCheck(typeEnv);
        if (type.equals(new BoolType()))
            return new BoolType();
        throw new ExpressionException("Negation requires a boolean expression.");
    }
}