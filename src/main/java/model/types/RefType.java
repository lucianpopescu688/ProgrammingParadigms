package model.types;

import model.values.IValue;
import model.values.RefValue;

public class RefType implements IType{
    private IType innerType;
    public RefType(IType innerType) {
        this.innerType = innerType;
    }
    public IType getInnerType() {
        return innerType;
    }

    @Override
    public boolean equals(IType type) {
        if (type instanceof RefType) {
            RefType refType = (RefType) type;
            return innerType.equals(refType.getInnerType());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Ref(" + innerType.toString() + ")";
    }

    @Override
    public IValue getDefaultValue() {
        return new RefValue(0, innerType);
    }
}
