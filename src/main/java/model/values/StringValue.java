package model.values;

import model.types.IType;
import model.types.StringType;

public class StringValue implements IValue{
    private String value;
    public StringValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public IType getType()
    {
        return new StringType();
    }
    public boolean equals(IValue value)
    {
        if (this.getType().equals(value.getType()))
        {
            return this.value.equals(((StringValue)value).getValue());
        }
        return false;
    }
    public String toString()
    {
        return value;
    }
}
