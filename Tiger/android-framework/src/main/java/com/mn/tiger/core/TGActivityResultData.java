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
    private DoubleKeyValueMap<Integer,Boolean, int[]> intToggleArrayMap;

    private DoubleKeyValueMap<Integer, Boolean, long[]> longToggleArrayMap;

    private DoubleKeyValueMap<Integer, Integer, Integer> intValueMap;

    public int[] getIntArray(int type, boolean selected)
    {
        return intToggleArrayMap.get(type, selected);
    }

    public int[] getIntArray(int type)
    {
        return intToggleArrayMap.get(type, true);
    }

    public long[] getLongArray(int type, boolean selected)
    {
        return longToggleArrayMap.get(type, selected);
    }

    public long[] getLongArray(int type)
    {
        return longToggleArrayMap.get(type, true);
    }

    public Map<Integer, Integer> getIntValueMap(int type)
    {
        return intValueMap.get(type);
    }

    /**
     * 是否为空
     * @return
     */
    public boolean isEmpty()
    {
        if(null != intToggleArrayMap && intToggleArrayMap.size() > 0)
        {
            return false;
        }

        if(null != longToggleArrayMap && longToggleArrayMap.size() > 0)
        {
            return false;
        }
        return true;
    }

    public static class Builder
    {
        private LongToggleMap longToggleMap;

        private IntegerToggleMap intToggleMap;

        private IntegerMap integerValueMap;

        public Builder()
        {
            intToggleMap = new IntegerToggleMap();
            longToggleMap = new LongToggleMap();
            integerValueMap = new IntegerMap();
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

        public Builder append(int type, int data)
        {
            appendToggleData(type, data, false);
            return this;
        }

        public Builder append(int type, int[] data)
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

        public Builder appendToggleData(int type, int data, boolean selected)
        {
            ConcurrentHashMap<Integer,Toggle> toggleMap = intToggleMap.get(type);
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

        public Builder appendToggleArray(int type, int[] array, boolean selected)
        {
            if(null != array)
            {
                ConcurrentHashMap<Integer,Toggle> toggleMap = intToggleMap.get(type);
                Toggle toggle;
                for (int data : array)
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

        public Builder append(int type, int key, int value)
        {
            integerValueMap.put(type, key, value);
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

            if(null != data.intToggleArrayMap && data.intToggleArrayMap.size() > 0)
            {
                Set<Integer> typeKeys = data.intToggleArrayMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    int[] intToggleArray = data.intToggleArrayMap.get(type, false);
                    this.appendToggleArray(type, intToggleArray, false);

                    int[] intReverseToggleArray = data.intToggleArrayMap.get(type, true);
                    this.appendToggleArray(type, intReverseToggleArray, true);
                }
            }

            if(null != data.intValueMap && data.intValueMap.size() > 0)
            {
                Set<Integer> typeKeys = data.intValueMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    this.integerValueMap.get(type).putAll(data.intValueMap.get(type));
                }
            }

            return this;
        }

        public TGActivityResultData build()
        {
            TGActivityResultData resultData = new TGActivityResultData();
            resultData.intToggleArrayMap = buildIntArrayMap(intToggleMap);
            resultData.longToggleArrayMap = buildLongArrayMap(longToggleMap);
            resultData.intValueMap = integerValueMap;

            return resultData;
        }

        private DoubleKeyValueMap<Integer,Boolean, int[]> buildIntArrayMap(IntegerToggleMap integerToggleMap)
        {
            DoubleKeyValueMap<Integer, Boolean, int[]> intArrayMap = new DoubleKeyValueMap<Integer,Boolean, int[]>();
            if(integerToggleMap.size() > 0)
            {
                Iterator<Map.Entry<Integer, ConcurrentHashMap<Integer, Toggle>>> iterator = integerToggleMap.entrySet().iterator();
                Map.Entry<Integer, ConcurrentHashMap<Integer, Toggle>> entry;
                ConcurrentHashMap<Integer, Toggle> toggleMap;
                Iterator<Map.Entry<Integer,Toggle>> toggleIterator;
                while (iterator.hasNext())
                {
                    entry = iterator.next();
                    toggleMap = entry.getValue();
                    toggleIterator = toggleMap.entrySet().iterator();

                    ArrayList<Integer>[] arrayLists = buildIntList(toggleIterator);
                    if(null != arrayLists[0] && arrayLists[0].size() > 0)
                    {
                        intArrayMap.put(entry.getKey(),true, Commons.convertIntegerListToIntArray(arrayLists[0]));
                    }

                    if(null != arrayLists[1] && arrayLists[1].size() > 0)
                    {
                        intArrayMap.put(entry.getKey(),false, Commons.convertIntegerListToIntArray(arrayLists[1]));
                    }
                }
            }

            return intArrayMap;
        }

        private ArrayList<Integer>[] buildIntList(Iterator<Map.Entry<Integer,Toggle>> toggleIterator)
        {
            ArrayList<Integer> integerList = new ArrayList<Integer>();
            ArrayList<Integer> integerReverseList = new ArrayList<Integer>();
            Map.Entry<Integer, Toggle> toggleEntry;
            Toggle toggle;
            while (toggleIterator.hasNext())
            {
                toggleEntry = toggleIterator.next();
                toggle = toggleEntry.getValue();
                if(toggle.isChanged())
                {
                    if(toggle.getState())
                    {
                        integerList.add(toggleEntry.getKey());
                    }
                    else
                    {
                        integerReverseList.add(toggleEntry.getKey());
                    }
                }
            }
            ArrayList<Integer>[] arrayLists = new ArrayList[2];
            arrayLists[0] = integerList;
            arrayLists[1] = integerReverseList;
            return arrayLists;
        }

        private DoubleKeyValueMap<Integer,Boolean, long[]> buildLongArrayMap(LongToggleMap longToggleMap)
        {
            DoubleKeyValueMap<Integer, Boolean, long[]> longArrayMap = new DoubleKeyValueMap<Integer,Boolean, long[]>();
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
                value = new ConcurrentHashMap<Long, Toggle>();
                super.put(key, value);
            }
            return value;
        }
    }

    private static class IntegerToggleMap extends DoubleKeyValueMap<Integer, Integer, Toggle>
    {
        @Override
        public ConcurrentHashMap<Integer, Toggle> get(Integer key)
        {
            ConcurrentHashMap<Integer, Toggle> value = super.get(key);
            if(null == value)
            {
                value = new ConcurrentHashMap<Integer, Toggle>();
                this.put(key, value);
            }
            return value;
        }
    }

    private static class IntegerMap extends DoubleKeyValueMap<Integer, Integer, Integer>
    {
        @Override
        public ConcurrentHashMap<Integer, Integer> get(Integer key1)
        {
            ConcurrentHashMap<Integer, Integer> value = super.get(key1);
            if(null == value)
            {
                value = new ConcurrentHashMap<Integer, Integer>();
                this.put(key1, value);
            }
            return value;
        }
    }
}
