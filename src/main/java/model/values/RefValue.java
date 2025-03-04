package model.values;

import model.types.IType;
import model.types.RefType;

public class RefValue implements IValue {
    private int address;
    private IType locationType;
    public RefValue(int address, IType locationType) {
        this.address = address;
        this.locationType = locationType;
    }

    public IType getType()
    {
        return new RefType(locationType);
    }

    public int getAddress()
    {
        return address;
    }
    public IType getLocationType() {
        return this.locationType;
    }

    public boolean equals(IValue other) {
        if (!other.getType().equals(this.getType()))
        {
            return false;
        }
        return address == ((RefValue)other).getAddress();
    }

    public String toString() {
        return "Address: " + address + ", RefValue(" + locationType.toString() + ")";
    }
}
