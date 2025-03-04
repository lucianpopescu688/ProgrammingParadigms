package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.state.PrgState;
import model.types.IType;

public interface IStatement {
    PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException, Exception;
    IStatement deepCopy();
    MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException;
}
