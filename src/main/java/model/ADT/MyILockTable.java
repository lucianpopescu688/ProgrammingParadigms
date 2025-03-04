package model.ADT;

import java.util.concurrent.ConcurrentHashMap;

public interface MyILockTable {
    int allocate();

    void update(int address, int value);

    ConcurrentHashMap<Integer, Integer> getLockTable();

    boolean contains(int address);

    int get(int address);

    void setLockTable(ConcurrentHashMap<Integer, Integer> newLockTable);
}