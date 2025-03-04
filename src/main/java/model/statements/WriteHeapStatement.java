package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.state.PrgState;
import model.types.IType;
import model.types.RefType;
import model.values.IValue;
import model.values.RefValue;

public class WriteHeapStatement implements IStatement{
    private String variable;
    private IExpression expression;
    public WriteHeapStatement(String variable, IExpression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public WriteHeapStatement deepCopy() {
        return new WriteHeapStatement(variable, expression.deepCopy());
    }

    public String toString() {
        return "Write to heap (" + variable + "=" + expression + ")";
    }

    public PrgState execute(PrgState state) throws StatementException, ExpressionException, ADTException {
        MyIMap<String, IValue> symTable = state.getSymTable();
        if (!symTable.containsKey(variable)) {
            throw new StatementException("Variable is not declared!");
        }
        IValue v = symTable.getValue(variable);
        if (!(v instanceof RefValue)) {
            throw new StatementException("Variable is not a RefValue!");
        }
        RefValue ref = (RefValue) v;
        MyIHeap heap = state.getHeap();
        if (!heap.containsKey(ref.getAddress()))
        {
            throw new StatementException("Heap does not contain reference!");
        }
        IValue result = expression.eval(symTable, heap);
        if (!result.getType().equals(ref.getLocationType())) {
            throw new StatementException("Wrong expression type!");
        }
        heap.set(ref.getAddress(), result);
        return null;
    }
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        IType variableType = typeEnvironment.getValue(variable);
        IType expressionType = expression.typeCheck(typeEnvironment);
        if (variableType.equals(new RefType(expressionType))) {
            return typeEnvironment;
        }//maybe problem
        throw new StatementException("Variable " + variable + " is not of type " + expressionType);
    }
}
