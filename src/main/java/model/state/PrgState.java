package model.state;

import exceptions.ADTException;
import exceptions.EmptyStackException;
import exceptions.ExpressionException;
import exceptions.StatementException;
import model.ADT.*;
import model.statements.IStatement;
import model.values.IValue;
import model.values.StringValue;

import java.io.BufferedReader;

public class PrgState {
    private MyIStack<IStatement> exeStack;
    private MyIMap<String, IValue> symTable;
    private MyIList<IValue> outList;
    private MyIMap<StringValue, BufferedReader> fileTable;
    private MyILockTable lockTable;
    private IStatement originalState;
    private MyIHeap heap;
    private final int id;
    public static int nextId;

    public PrgState(IStatement OGStatement) {
        this.exeStack = new MyStack<IStatement>();
        this.symTable = new MyMap<String, IValue>();
        this.outList = new MyList<IValue>();
        this.fileTable = new MyMap<StringValue, BufferedReader>();
        this.lockTable = new MyLockTable();
        this.originalState = OGStatement.deepCopy();
        this.exeStack.push(this.originalState);
        this.heap = new MyHeap();
        this.id = this.getNextId();
    }

    public PrgState(IStatement originalState, MyIStack<IStatement> exeStack, MyIMap<String, IValue> symTable, MyIList<IValue> outList, MyIMap<StringValue, BufferedReader> fileTable, MyIHeap heap, MyILockTable lockTable) {
        this.exeStack = exeStack;
        this.symTable = symTable;
        this.outList = outList;
        this.fileTable = fileTable;
        this.originalState = originalState.deepCopy();
        this.heap = heap;
        this.lockTable = lockTable;
        this.exeStack.push(this.originalState);
        this.id = this.getNextId();
    }

    //Getters
    public int getId()
    {
        return this.id;
    }

    public MyIHeap getHeap() {
        return heap;
    }

    public synchronized int getNextId() {
        return nextId++;
    }

    public MyIList<IValue> getOutList() {
        return outList;
    }

    public MyIStack<IStatement> getExeStack() {
        return exeStack;
    }

    public MyIMap<String, IValue> getSymTable() {
        return symTable;
    }

    public MyIMap<StringValue, BufferedReader> getFileTable() {
        return this.fileTable;
    }

    public MyILockTable getLockTable() {
        return lockTable;
    }

    //Setters
    public void setHeap(MyIHeap heap) {
        this.heap = heap;
    }

    public void setLockTable(MyILockTable lockTable) {
        this.lockTable = lockTable;
    }

    public boolean isNotCompleted() {
        return !exeStack.isEmpty();
    }

    public PrgState oneStep() throws Exception {
        MyIStack<IStatement> exeStack = this.getExeStack();
        if (exeStack.isEmpty()) throw new EmptyStackException("exeStack is empty");
        IStatement statement = exeStack.pop();
        return statement.execute(this);
    }

    public String fileTableToString() {
        StringBuilder sb = new StringBuilder();
        for (StringValue path : fileTable.getKeys()) {
            sb.append(path).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRGState with id: ").append(this.id).append("\n");
        sb.append("Execution Stack").append(this.exeStack.toString()).append("\n");
        sb.append("SymTable: \n").append(this.symTable.toString()).append("\n");
        sb.append("Output List").append(this.outList.toString()).append("\n");
        sb.append("FileTable: \n").append(this.fileTableToString()).append("\n");
        sb.append("Heap: \n").append(this.heap.toString()).append("\n");
        sb.append("LockTable: \n").append(this.lockTable.toString()).append("\n");
        return sb.toString();
    }
}
