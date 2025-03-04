package model.expressions;


import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.BoolType;
import model.types.IType;
import model.types.IntType;
import model.values.BoolValue;
import model.values.IValue;

public class LogicalExpression implements IExpression {
    private IExpression left;
    private IExpression right;
    private LogicalOperator operator;

    public LogicalExpression(IExpression left, LogicalOperator operator, IExpression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ExpressionException, ADTException
    {
        IValue leftresult = this.left.eval(symTable, heap);
        IValue rightresult = this.right.eval(symTable, heap);
        if (!leftresult.getType().equals(rightresult.getType()))
        {
            throw new ExpressionException("Right doesn't have the same type as left");
        }
        BoolValue rightBool = (BoolValue) rightresult;
        BoolValue leftBool = (BoolValue) leftresult;
        if (operator == LogicalOperator.OR)
        {
            return new BoolValue(rightBool.getValue() || leftBool.getValue());
        }
        if (operator == LogicalOperator.AND)
        {
            return new BoolValue(leftBool.getValue() && rightBool.getValue());
        }
        throw new ExpressionException("Unknown operator");
    }
    public String toString()
    {
        return left.toString() + " " + operator.toString() + " " + right.toString();
    }
    public IExpression deepCopy()
    {
        return new LogicalExpression(left.deepCopy(), operator, right.deepCopy());
    }
    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        IType typeLeft = left.typeCheck(typeEnvironment);
        IType typeRight = right.typeCheck(typeEnvironment);
        if (typeLeft.equals(new BoolType()))
        {
            if (typeRight.equals(new BoolType()))
            {
                return new BoolType();
            }
            throw new ExpressionException("The right is not an boolean.");
        }
        throw new ExpressionException("The left is not an boolean.");
    }
}
