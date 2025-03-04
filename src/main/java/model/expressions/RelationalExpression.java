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
import model.values.IntValue;

public class RelationalExpression implements IExpression {
    private IExpression left;
    private IExpression right;
    private RelationalOperator operator;
    public RelationalExpression(IExpression left, RelationalOperator operator, IExpression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
    public IExpression deepCopy() {
        return new RelationalExpression(left.deepCopy(), operator, right.deepCopy());
    }

    public String toString()
    {
        return left.toString() + " " + operator.toString() + " " + right.toString();
    }

    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ADTException, ExpressionException {
        IValue leftVal = left.eval(symTable, heap);
        IValue rightVal = right.eval(symTable, heap);
        if (!leftVal.getType().equals(new IntType()) || !rightVal.getType().equals(new IntType())) {
            throw new ExpressionException("Only int values can be compared");
        }
        IntValue leftInt = (IntValue) leftVal;
        IntValue rightInt = (IntValue) rightVal;
        if (operator == RelationalOperator.EQUAL)
        {
            return new BoolValue(leftInt.equals(rightInt));
        }
        if (operator == RelationalOperator.NOT_EQUAL)
        {
            return new BoolValue(!leftInt.equals(rightInt));
        }
        if (operator == RelationalOperator.LESS_THAN)
        {
            return new BoolValue(leftInt.getValue() < rightInt.getValue());
        }
        if (operator == RelationalOperator.GREATER_THAN)
        {
            return new BoolValue(leftInt.getValue() > rightInt.getValue());
        }
        if (operator == RelationalOperator.GREATER_EQUAL)
        {
            return new BoolValue(leftInt.getValue() >= rightInt.getValue());
        }
        if (operator == RelationalOperator.LESS_EQUAL)
        {
            return new BoolValue(leftInt.getValue() <= rightInt.getValue());
        }
        throw new ExpressionException("Unknown operator");
    }
    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        IType typeLeft = left.typeCheck(typeEnvironment);
        IType typeRight = right.typeCheck(typeEnvironment);
        if (typeLeft.equals(new IntType()))
        {
            if (typeRight.equals(new IntType()))
            {
                return new BoolType();
            }
            throw new ExpressionException("The right is not an integer.");
        }
        throw new ExpressionException("The left is not an integer.");
    }

}
