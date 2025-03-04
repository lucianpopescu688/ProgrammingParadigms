package model.expressions;

import exceptions.ADTException;
import exceptions.ExpressionException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.types.IType;
import model.types.RefType;
import model.values.IValue;
import model.values.RefValue;

public class ReadHeapExpression implements IExpression {
    private IExpression expression;
    public ReadHeapExpression(IExpression expression) {
        this.expression = expression;
    }

    public IExpression deepCopy() {
        return new ReadHeapExpression(expression.deepCopy());
    }

    public String toString() {
        return "Read heap (" + expression.toString() + ")";
    }

    public IValue eval(MyIMap<String, IValue> symTable, MyIHeap heap) throws ExpressionException, ADTException
    {
        IValue v = expression.eval(symTable, heap);
        if (!(v instanceof RefValue))
        {
            throw new ExpressionException("Expression does not evaluate to a RefValue");
        }
        RefValue refV = (RefValue)v;
        if (!heap.containsKey(refV.getAddress()))
        {
            throw new ExpressionException("Heap does not contain that address");
        }
        return heap.getValue(refV.getAddress());
    }
    public IType typeCheck(MyIMap<String, IType> typeEnvironment) throws ExpressionException, ADTException
    {
        IType type = expression.typeCheck(typeEnvironment);
        if (type instanceof RefType)
        {
            return ((RefType)type).getInnerType();
        }
        throw new ExpressionException("The ReadHeap's argument is not a RefType");
    }
}
