package model.types;

import model.types.IType;
import model.values.BoolValue;
import model.values.IValue;

public class BoolType implements IType {
    public boolean equals(IType type)
    {
        return type instanceof BoolType;
    }
    public IValue getDefaultValue()
    {
        return new BoolValue(false);
    }
    public BoolType()
    {

    }

    @Override
    public String toString() {
        return "Bool";
    }
}
