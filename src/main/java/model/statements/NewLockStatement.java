package model.statements;

import exceptions.*;
import model.ADT.MyIMap;
import model.ADT.MyILockTable;
import model.state.PrgState;
import model.types.IntType;
import model.types.IType;
import model.values.IntValue;
import model.values.IValue;

public class NewLockStatement implements IStatement {
    private String var;

    public NewLockStatement(String var) {
        this.var = var;
    }

    @Override
    public PrgState execute(PrgState state) throws Exception {
        MyIMap<String, IValue> symTable = state.getSymTable();
        if (!symTable.containsKey(var))
            throw new Exception("Variable " + var + " is not defined.");
        IValue val = symTable.getValue(var);
        if (!val.getType().equals(new IntType()))
            throw new Exception("Variable " + var + " is not of type int.");

        // Get the global LockTable from the program state.
        MyILockTable lockTable = state.getLockTable();

        // Allocate a new lock (this call must be synchronized inside MyLockTable.allocate())
        int newLockAddress = lockTable.allocate();

        // Update the symbol table so that var now stores the new lock address.
        symTable.put(var, new IntValue(newLockAddress));
        return null;
    }

    @Override
    public IStatement deepCopy() {
        return new NewLockStatement(var);
    }

    @Override
    public MyIMap<String, IType> typeCheck(MyIMap<String, IType> typeEnv) throws StatementException {
        if (!typeEnv.getValue(var).equals(new IntType())) {
            throw new StatementException("Variable must be of type int.");
        }
        return typeEnv;
    }

    @Override
    public String toString() {
        return "newLock(" + var + ")";
    }
}
