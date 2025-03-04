package model.ADT;

import exceptions.KeyNotFoundException;
import model.values.IValue;

public class MyHeap implements MyIHeap {
    private MyIMap<Integer, IValue> heapTable;
    private static int currentAddress = 1;

    public MyHeap() {
        heapTable = new MyMap<Integer, IValue>();
    }

    @Override
    public int allocate(IValue v) {
        heapTable.put(currentAddress++, v);
        return currentAddress-1;
    }

    @Override
    public IValue getValue(int address) throws KeyNotFoundException {
        return heapTable.getValue(address);
    }

    @Override
    public boolean containsKey(int address)
    {
        return heapTable.containsKey(address);
    }

    @Override
    public void set(int address, IValue v) {
        heapTable.put(address, v);
    }

    @Override
    public MyIMap<Integer, IValue> getHeap() {
        return heapTable;
    }

    @Override
    public void setHeap(MyIMap<Integer, IValue> heap) {
        this.heapTable = heap;
    }

    @Override
    public String toString()
    {
        return heapTable.toString();
    }
}
