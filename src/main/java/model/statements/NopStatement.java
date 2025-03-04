package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.state.PrgState;
import model.types.IType;

public class NopStatement implements IStatement{
    public PrgState execute(PrgState state) {
        return null;
    }

    public IStatement deepCopy() {
        return new NopStatement();
    }

    public String toString() {
        return "NopStatement";
    }

    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        return typeEnvironment;
    }
}
