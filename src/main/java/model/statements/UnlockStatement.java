package model.statements;

import exceptions.ADTException;
import exceptions.StatementException;
import model.ADT.MyIMap;
import model.state.PrgState;
import model.types.IType;
import model.types.IntType;
import model.values.IValue;
import model.values.IntValue;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import exceptions.*;
import model.ADT.MyIMap;
import model.ADT.MyILockTable;
import model.state.PrgState;
import model.types.IntType;
import model.types.IType;
import model.values.IntValue;
import model.values.IValue;

public class UnlockStatement implements IStatement {
    private String var;

    public UnlockStatement(String var) {
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
            if (currentValue == state.getId()) {
                lockTable.update(foundIndex, -1);
            }
        }
        return null;
    }

    @Override
    public IStatement deepCopy() {
        return new UnlockStatement(var);
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
        return "unlock(" + var + ")";
    }
}