package com.mn.tiger.request;

/**
 * Created by peng on 16/3/22.
 */
public class TGResult
{
    private String rawData;

    void setRawData(String rawData)
    {
        this.rawData = rawData;
    }

    public String getRawData()
    {
        return rawData;
    }

    @Override
    public String toString()
    {
        return rawData;
    }
}
