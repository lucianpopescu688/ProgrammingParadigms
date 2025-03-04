package model.types;

import model.types.IType;
import model.values.IValue;
import model.values.IntValue;

public class IntType implements IType {
    public boolean equals(IType type)
    {
        return type instanceof IntType;
    }
    public IValue getDefaultValue()
    {
        return new IntValue(0);
    }
    public IntType()
    {

    }

    @Override
    public String toString() {
        return "Int";
    }
}