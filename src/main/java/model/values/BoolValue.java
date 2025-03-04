package model.values;

import model.types.BoolType;
import model.types.IType;

public class BoolValue implements IValue{
    private boolean value;
    public BoolValue(boolean value) {
        this.value = value;
    }
    public IType getType() {
        return new BoolType();
    }
    public boolean getValue() {
        return this.value;
    }
    public boolean equals(IValue v) {
        if (this.getType().equals(v.getType())) {
            return ((BoolValue) v).getValue() == this.getValue();
        }
        return false;
    }
    public String toString() {
        return Boolean.toString(this.value);
    }
}
