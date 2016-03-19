package com.mn.tiger.fresco;

/**
 * Created by peng on 16/3/12.
 */
public class Variable<T>
{
    private T value;

    private boolean changed = false;

    public Variable()
    {

    }

    public Variable(T defaultValue)
    {
        this.value = defaultValue;
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void resetChangeStatus()
    {
        this.changed = false;
    }

    public void setValue(T value)
    {
        if(null == this.value && null == value)
        {
            changed = false;
            return;
        }
        else if(null == this.value && null != value)
        {
            changed = true;
            this.value = value;
        }
        else if(null != this.value && !this.value.equals(value))
        {
            changed = true;
            this.value = value;
        }
        else
        {
            changed = false;
        }
    }

    public T getValue()
    {
        return value;
    }

    public boolean isNull()
    {
        return null == value;
    }
}
