package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.ADT.MyStack;
import model.state.PrgState;
import model.types.IType;

public class ForkStatement implements IStatement {
    private IStatement statement;
    public ForkStatement(IStatement stmt) {
        this.statement = stmt;
    }
    public String toString(){
        return "Fork ( "+ statement.toString()+" )";
    }
    public IStatement deepCopy() {
        return new ForkStatement(statement.deepCopy());
    }
    public PrgState execute(PrgState state) {
        MyStack<IStatement> stack = new MyStack<>();
        return new PrgState(this.statement, stack, state.getSymTable().deepCopy(), state.getOutList(), state.getFileTable(), state.getHeap(), state.getLockTable());
    }
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        statement.typeCheck(typeEnvironment.deepCopy());
        return typeEnvironment;
    }
}
