package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.IType;
import model.values.IValue;

public interface IExpression {
    IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ExpressionException, ADTException;
    IExpression deepCopy();
    IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException;
}
