package model.ADT;

import model.values.IValue;

public interface MyIHeap {
    public int allocate(IValue v);
    public IValue getValue(int address);
    public void set(int address, IValue v);
    public MyIMap<Integer,IValue> getHeap();
    public void setHeap(MyIMap<Integer,IValue> heap);
    public boolean containsKey(int address);
}
