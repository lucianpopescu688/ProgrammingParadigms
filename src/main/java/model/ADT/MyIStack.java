package model.ADT;

import exceptions.ADTException;
import exceptions.EmptyStackException;
import model.statements.IStatement;

import java.util.List;

public interface MyIStack <T>{
    public T pop() throws ADTException;
    public void push(T value);
    public boolean isEmpty();
    public List<T> toList();
}
