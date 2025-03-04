package model.statements;

import exceptions.*;
import model.ADT.MyIMap;
import model.ADT.MyILockTable;
import model.state.PrgState;
import model.types.IntType;
import model.types.IType;
import model.values.IntValue;
import model.values.IValue;

public class LockStatement implements IStatement {
    private String var;

    public LockStatement(String var) {
        this.var = var;
    }

    @Override
    public PrgState execute(PrgState state) throws StatementException, ADTException, ExpressionException {
        MyIMap<String, IValue> symTable = state.getSymTable();
        MyILockTable lockTable = state.getLockTable();

        if (!symTable.containsKey(var) || !symTable.getValue(var).getType().equals(new IntType())) {
            throw new StatementException("Variable not found or not of type int.");
        }

        int foundIndex = ((IntValue) symTable.getValue(var)).getValue();
        synchronized (lockTable) {
            if (!lockTable.contains(foundIndex)) {
                throw new StatementException("Lock index not found.");
            }

            int currentValue = lockTable.get(foundIndex);
            if (currentValue == -1) {
                lockTable.update(foundIndex, state.getId());
            } else {
                state.getExeStack().push(this);
            }
        }
        return null;
    }

    @Override
    public IStatement deepCopy() {
        return new LockStatement(var);
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
        return "lock(" + var + ")";
    }
}
