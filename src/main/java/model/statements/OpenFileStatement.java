package model.statements;

import exceptions.ADTException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.expressions.IExpression;
import model.state.PrgState;
import model.types.IType;
import model.types.StringType;
import model.values.IValue;
import model.values.StringValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OpenFileStatement implements IStatement{
    private IExpression fileName;
    public OpenFileStatement(IExpression fileName) {
        this.fileName = fileName;
    }
    public IStatement deepCopy() {
        return new OpenFileStatement(fileName.deepCopy());
    }
    public String toString() {
        return "opening file (" + fileName + ")";
    }
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
        MyIMap<String, IValue> symTable = state.getSymTable();
        IValue result = fileName.eval(symTable, state.getHeap());
        if (!result.getType().equals(new StringType()))
        {
            throw new StatementException("The expression must be a string");
        }
        StringValue s = (StringValue) result;
        MyIMap<StringValue, BufferedReader> fileTable = state.getFileTable();
        if (fileTable.containsKey(s))
        {
            throw new StatementException("File already open");
        }
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(s.getValue()));
            fileTable.put(s, reader);
        }
        catch (IOException e)
        {
            throw new StatementException(e.getMessage());
        }
        return null;
    }
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnvironment) throws StatementException, ADTException, ExpressionException
    {
        if (fileName.typeCheck(typeEnvironment).equals(new StringType()))
        {
            return typeEnvironment;
        }
        throw new StatementException("The file name must be a string");
    }
}
