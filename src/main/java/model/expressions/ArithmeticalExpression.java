package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.IType;
import model.types.IntType;
import model.values.IValue;
import model.values.IntValue;

public class ArithmeticalExpression implements IExpression {
    private IExpression left;
    private IExpression right;
    private ArithmeticalOperator operator;

    public ArithmeticalExpression(IExpression left, ArithmeticalOperator operator, IExpression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ExpressionException, ADTException {
        IValue leftValue = left.eval(symTable, heap);
        IValue rightValue = right.eval(symTable, heap);

        if (!leftValue.getType().equals(new IntType())) {
            throw new ExpressionException("Value " + leftValue + " is not an integer.");
        }

        if (!rightValue.getType().equals(new IntType())) {
            throw new ExpressionException("Value " + rightValue + " is not an integer.");
        }

        IntValue leftInt = (IntValue) leftValue;
        IntValue rightInt = (IntValue) rightValue;
        switch (operator) {
            case PLUS:
                return new IntValue(leftInt.getValue() + rightInt.getValue());
            case MINUS:
                return new IntValue(leftInt.getValue() - rightInt.getValue());
            case MULTIPLY:
                return new IntValue(leftInt.getValue() * rightInt.getValue());
            case DIVIDE:
                if (rightInt.getValue() == 0) throw new ExpressionException("Divide by zero.");
                return new IntValue(leftInt.getValue() / rightInt.getValue());
        }
        throw new ExpressionException("Operator " + operator + " is not supported.");
    }

    public IExpression deepCopy() {
        return new ArithmeticalExpression(left.deepCopy(), operator, right.deepCopy());
    }

    public String toString()
    {
        return left.toString() + " " + operator + " " + right.toString();
    }

    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        IType typeLeft = left.typeCheck(typeEnvironment);
        IType typeRight = right.typeCheck(typeEnvironment);
        if (typeLeft.equals(new IntType()))
        {
            if (typeRight.equals(new IntType()))
            {
                return new IntType();
            }
            throw new ExpressionException("The right is not an integer.");
        }
        throw new ExpressionException("The left is not an integer.");
    }
}
