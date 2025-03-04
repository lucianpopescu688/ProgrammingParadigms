package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.IType;
import model.values.IValue;

public class ValueEvalExpression implements IExpression {
    private IValue value;
    public ValueEvalExpression(IValue value) {
        this.value = value;
    }
    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ADTException {
        return value;
    }
    public IExpression deepCopy() {
        return new ValueEvalExpression(value);
    }
    public String toString() {
        return value.toString();
    }
    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        return value.getType();
    }
}
