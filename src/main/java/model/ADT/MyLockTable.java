package model.ADT;

import java.util.concurrent.ConcurrentHashMap;

public class MyLockTable implements MyILockTable {
    private final ConcurrentHashMap<Integer, Integer> lockTable;
    private int nextAddress;

    public MyLockTable() {
        this.lockTable = new ConcurrentHashMap<>();
        this.nextAddress = 1;
    }

    @Override
    public synchronized int allocate() {
        int address = nextAddress++;
        lockTable.put(address, -1); // initialize the lock as free (i.e. -1)
        return address;
    }

    @Override
    public synchronized void update(int address, int value) {
        lockTable.put(address, value);
    }

    @Override
    public ConcurrentHashMap<Integer, Integer> getLockTable() {
        return lockTable;
    }

    @Override
    public boolean contains(int address) {
        return lockTable.containsKey(address);
    }

    @Override
    public int get(int address) {
        return lockTable.get(address);
    }

    @Override
    public void setLockTable(ConcurrentHashMap<Integer, Integer> newLockTable) {
        lockTable.clear();
        lockTable.putAll(newLockTable);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer key : lockTable.keySet()) {
            sb.append(key).append(" -> ").append(lockTable.get(key)).append("\n");
        }
        return sb.toString();
    }
}
