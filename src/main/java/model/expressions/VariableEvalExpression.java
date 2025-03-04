package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.IType;
import model.values.IValue;

public class VariableEvalExpression implements IExpression {
    private String variable;
    public VariableEvalExpression(String variable) {
        this.variable = variable;
    }
    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ADTException
    {
        if (!symTable.containsKey(variable)) throw new ADTException("Variable " + variable + " not found");
        return symTable.getValue(variable);
    }
    public IExpression deepCopy() {
        return new VariableEvalExpression(variable);
    }
    public String toString() {
        return variable;
    }
    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        return typeEnvironment.getValue(variable);
    }
}
