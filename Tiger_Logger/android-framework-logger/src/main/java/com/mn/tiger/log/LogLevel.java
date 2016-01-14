package com.mn.tiger.log;

public enum LogLevel
{
    LOG_LEVEL_OFF(0), LOG_LEVEL_ERROR(1), LOG_LEVEL_WARN(2), LOG_LEVEL_INFO(3), LOG_LEVEL_DEBUG(4);

    private int logLevel;

    LogLevel(int value)
    {
        this.logLevel = value;
    }

    public int getValue()
    {
        return logLevel;
    }
}
