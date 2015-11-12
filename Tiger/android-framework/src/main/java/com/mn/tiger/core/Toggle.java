package com.mn.tiger.core;

/**
 * Created by peng on 15/9/16.
 */
class Toggle
{
    private boolean originalState;

    private boolean state;

    public Toggle(boolean state)
    {
        this.originalState = !state;
        this.state = state;
    }

    public void toggle()
    {
        this.state = !this.state;
    }

    public boolean isChanged()
    {
        return originalState != state;
    }

    public boolean getState()
    {
        return state;
    }
}
