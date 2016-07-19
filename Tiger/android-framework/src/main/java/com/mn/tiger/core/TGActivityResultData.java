package com.mn.tiger.core;

import com.mn.tiger.utility.Commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Activity之间传值的数据类
 * Created by peng on 15/9/16.
 */
public class TGActivityResultData implements Serializable
{
    protected DoubleKeyValueMap<Integer, Boolean, long[]> longToggleArrayMap;

    protected DoubleKeyValueMap<Integer, Long, Long> longValueMap;

    protected long[] getLongArray(int type, boolean selected)
    {
        return longToggleArrayMap.get(type, selected);
    }

    protected long[] getLongArray(int type)
    {
        return longToggleArrayMap.get(type, true);
    }

    protected Map<Long, Long> getLongValueMap(int type)
    {
        return longValueMap.get(type);
    }

    /**
     * 是否为空
     * @return
     */
    public boolean isEmpty()
    {
        if(null != longToggleArrayMap && longToggleArrayMap.size() > 0)
        {
            return false;
        }

        if(null != longValueMap && longValueMap.size() > 0)
        {
            return false;
        }

        return true;
    }

    protected static class Builder<T extends TGActivityResultData>
    {
        private LongToggleMap longToggleMap;

        private LongMap longValueMap;

        public Builder()
        {
            longToggleMap = new LongToggleMap();
            longValueMap = new LongMap();
        }

        public Builder append(int type, long data)
        {
            appendToggleData(type, data, false);
            return this;
        }

        public Builder append(int type, long[] data)
        {
            appendToggleArray(type, data, false);
            return this;
        }

        public Builder appendToggleData(int type, long data, boolean selected)
        {
            ConcurrentHashMap<Long,Toggle> toggleMap = longToggleMap.get(type);
            Toggle toggle = toggleMap.get(data);
            if(null == toggle)
            {
                toggle = new Toggle(selected);
                toggleMap.put(data, toggle);
            }
            else
            {
                toggle.toggle();
            }

            return this;
        }

        public Builder appendToggleArray(int type, long[] array, boolean selected)
        {
            if(null != array)
            {
                ConcurrentHashMap<Long,Toggle> toggleMap = longToggleMap.get(type);
                Toggle toggle;
                for (long data : array)
                {
                    toggle = toggleMap.get(data);
                    if(null == toggle)
                    {
                        toggle = new Toggle(selected);
                        toggleMap.put(data, toggle);
                    }
                    else
                    {
                        toggle.toggle();
                    }
                }

            }
            return this;
        }


        public Builder append(int type, long key, long value)
        {
            Long oldValue = longValueMap.get(type,key);
            if(null != oldValue)
            {
                value = value + oldValue.longValue();
            }
            longValueMap.put(type, key, value);
            return this;
        }

        /**
         * 追加其他的返回值
         * @param data
         * @return
         */
        public Builder append(TGActivityResultData data)
        {
            if(null != data.longToggleArrayMap && data.longToggleArrayMap.size() > 0)
            {
                Set<Integer> typeKeys = data.longToggleArrayMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    long[] longToggleArray = data.longToggleArrayMap.get(type, false);
                    this.appendToggleArray(type, longToggleArray, false);

                    long[] longReverseToggleArray = data.longToggleArrayMap.get(type, true);
                    this.appendToggleArray(type, longReverseToggleArray, true);
                }
            }

            if(null != data.longValueMap && data.longValueMap.size() > 0)
            {
                Set<Integer> typeKeys = data.longValueMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    this.longValueMap.get(type).putAll(data.longValueMap.get(type));
                }
            }

            return this;
        }

        public T build()
        {
            try
            {
                T resultData = (T)Class.forName(Commons.getClassNameOfGenericType(this.getClass(),0)).newInstance();
                resultData.longToggleArrayMap = buildLongArrayMap(longToggleMap);
                resultData.longValueMap = longValueMap;
                return resultData;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private DoubleKeyValueMap<Integer,Boolean, long[]> buildLongArrayMap(LongToggleMap longToggleMap)
        {
            DoubleKeyValueMap<Integer, Boolean, long[]> longArrayMap = new DoubleKeyValueMap<>();
            if(longToggleMap.size() > 0)
            {
                Iterator<Map.Entry<Integer, ConcurrentHashMap<Long, Toggle>>> iterator = longToggleMap.entrySet().iterator();
                Map.Entry<Integer, ConcurrentHashMap<Long, Toggle>> entry;
                ConcurrentHashMap<Long, Toggle> toggleMap;
                Iterator<Map.Entry<Long,Toggle>> toggleIterator;
                while (iterator.hasNext())
                {
                    entry = iterator.next();
                    toggleMap = entry.getValue();
                    toggleIterator = toggleMap.entrySet().iterator();

                    ArrayList<Long>[] arrayLists = buildLongList(toggleIterator);
                    if(null != arrayLists[0] && arrayLists[0].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), true, Commons.convertLongListToLongArray(arrayLists[0]));
                    }

                    if(null != arrayLists[1] && arrayLists[1].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), false, Commons.convertLongListToLongArray(arrayLists[1]));
                    }
                }
            }

            return longArrayMap;
        }

        private ArrayList<Long>[] buildLongList(Iterator<Map.Entry<Long,Toggle>> toggleIterator)
        {
            ArrayList<Long> longList = new ArrayList<Long>();
            ArrayList<Long> longReverseList = new ArrayList<Long>();
            Map.Entry<Long, Toggle> toggleEntry;
            Toggle toggle;
            while (toggleIterator.hasNext())
            {
                toggleEntry = toggleIterator.next();
                toggle = toggleEntry.getValue();
                if(toggle.isChanged())
                {
                    if(toggle.getState())
                    {
                        longList.add(toggleEntry.getKey());
                    }
                    else
                    {
                        longReverseList.add(toggleEntry.getKey());
                    }
                }
            }
            ArrayList<Long>[] arrayLists = new ArrayList[2];
            arrayLists[0] = longList;
            arrayLists[1] = longReverseList;
            return arrayLists;
        }

    }

    private static class LongToggleMap extends DoubleKeyValueMap<Integer, Long, Toggle>
    {
        @Override
        public Toggle get(Integer key1, Long key2)
        {
            return super.get(key1, key2);
        }

        @Override
        public ConcurrentHashMap<Long, Toggle> get(Integer key)
        {
            ConcurrentHashMap<Long, Toggle> value = super.get(key);
            if(null == value)
            {
                value = new ConcurrentHashMap<>();
                super.put(key, value);
            }
            return value;
        }
    }

    private static class LongMap extends DoubleKeyValueMap<Integer, Long, Long>
    {
        @Override
        public ConcurrentHashMap<Long, Long> get(Integer key1)
        {
            ConcurrentHashMap<Long, Long> value = super.get(key1);
            if(null == value)
            {
                value = new ConcurrentHashMap<>();
                this.put(key1, value);
            }
            return value;
        }
    }
}
