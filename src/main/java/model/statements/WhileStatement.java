package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIHeap;
import model.ADT.MyIMap;
import model.ADT.MyIStack;
import model.expressions.IExpression;
import model.state.PrgState;
import model.types.BoolType;
import model.types.IType;
import model.values.BoolValue;
import model.values.IValue;

public class WhileStatement implements IStatement {
    private IStatement statement;
    private IExpression condition;
    public WhileStatement(IExpression condition, IStatement statement) {
        this.statement = statement;
        this.condition = condition;
    }
    public IStatement deepCopy()
    {
        return new WhileStatement(condition.deepCopy(), statement.deepCopy());
    }
    public String toString()
    {
        return "while (" + condition.toString() + ") {" + statement + "}";
    }
    public PrgState execute(PrgState state) throws ExpressionException, ADTException, StatementException
    {
        MyIMap<String, IValue> symTable = state.getSymTable();
        MyIHeap heap = state.getHeap();
        MyIStack<IStatement> exeStack = state.getExeStack();
        IValue value = condition.eval(symTable, heap);
        if (!value.getType().equals(new BoolType()))
        {
            throw new StatementException("The condition is not a boolean");
        }
        BoolValue boolValue = (BoolValue) value;

        if (boolValue.getValue())
        {
            exeStack.push(this.deepCopy());
            exeStack.push(statement.deepCopy());
        }

        return null;
    }
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        if (this.condition.typeCheck(typeEnvironment).equals(new BoolType()))
        {
            statement.typeCheck(typeEnvironment.deepCopy());
            return typeEnvironment;
        }
        throw new StatementException("The condition of the while is not a boolean");
    }
}
