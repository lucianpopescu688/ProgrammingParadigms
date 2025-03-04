package model.values;

import model.types.IType;
import model.types.IntType;

public class IntValue implements IValue {
    private int value;
    public IntValue(int value) {
        this.value = value;
    }
    public IType getType() {
        return new IntType();
    }
    public int getValue() {
        return this.value;
    }
    public boolean equals(IValue v) {
        if (this.getType().equals(v.getType())) {
            return ((IntValue) v).getValue() == this.getValue();
        }
        return false;
    }
    public String toString() {
        return Integer.toString(this.value);
    }
}
