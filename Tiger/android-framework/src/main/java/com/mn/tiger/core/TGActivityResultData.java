package com.mn.tiger.core;

import com.mn.tiger.utility.Commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Activity之间传值的数据类
 * Created by peng on 15/9/16.
 */
public class TGActivityResultData implements Serializable
{
    protected DoubleKeyValueMap<Integer, Boolean, ArrayList<Long>> longToggleListMap;

    protected DoubleKeyValueMap<Integer, Boolean, ArrayList<String>> stringToggleListMap;

    protected DoubleKeyValueMap<Integer, Long, Long> longValueMap;

    protected List<Long> getLongList(int type, boolean selected)
    {
        return longToggleListMap.get(type, selected);
    }

    protected List<Long> getLongList(int type)
    {
        return longToggleListMap.get(type, true);
    }

    protected List<String> getStringList(int type, boolean selected)
    {
        return stringToggleListMap.get(type, selected);
    }

    protected List<String> getStringList(int type)
    {
        return stringToggleListMap.get(type, true);
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
        if(null != longToggleListMap && longToggleListMap.size() > 0)
        {
            return false;
        }

        if(null != longValueMap && longValueMap.size() > 0)
        {
            return false;
        }

        if(null != stringToggleListMap && stringToggleListMap.size() > 0)
        {
            return false;
        }

        return true;
    }

    protected static class Builder<T extends TGActivityResultData>
    {
        private LongToggleMap longToggleMap;

        private StringToggleMap stringToggleMap;

        private LongMap longValueMap;

        public Builder()
        {
            longToggleMap = new LongToggleMap();
            stringToggleMap = new StringToggleMap();
            longValueMap = new LongMap();
        }

        public Builder append(int type, long data)
        {
            appendToggleData(type, data, true);
            return this;
        }

        public Builder appendLongList(int type, List<Long> data)
        {
            appendLongToggleArray(type, data, true);
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

        public Builder appendLongToggleArray(int type, List<Long> array, boolean selected)
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

        public Builder append(int type, String data)
        {
            appendToggleData(type, data, true);
            return this;
        }

        public Builder appendStringList(int type, List<String> data)
        {
            appendStringToggleArray(type, data, true);
            return this;
        }

        public Builder appendToggleData(int type, String data, boolean selected)
        {
            ConcurrentHashMap<String,Toggle> toggleMap = stringToggleMap.get(type);
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

        public Builder appendStringToggleArray(int type, List<String> array, boolean selected)
        {
            if(null != array)
            {
                ConcurrentHashMap<String,Toggle> toggleMap = stringToggleMap.get(type);
                Toggle toggle;
                for (String data : array)
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
            //追加longToggle数据
            if(null != data.longToggleListMap && data.longToggleListMap.size() > 0)
            {
                Set<Integer> typeKeys = data.longToggleListMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    List<Long> longToggleList = data.longToggleListMap.get(type, false);
                    this.appendLongToggleArray(type, longToggleList, false);

                    List<Long> longReverseToggleList = data.longToggleListMap.get(type, true);
                    this.appendLongToggleArray(type, longReverseToggleList, true);
                }
            }

            //追加stringToggle数据
            if(null != data.stringToggleListMap && data.stringToggleListMap.size() > 0)
            {
                Set<Integer> typeKeys = data.stringToggleListMap.getFirstKeys();
                Iterator<Integer> iterator = typeKeys.iterator();
                while (iterator.hasNext())
                {
                    int type = iterator.next();
                    List<String> stringToggleList = data.stringToggleListMap.get(type, false);
                    this.appendStringToggleArray(type, stringToggleList, false);

                    List<String> stringReverseToggleList = data.stringToggleListMap.get(type, true);
                    this.appendStringToggleArray(type, stringReverseToggleList, true);
                }
            }

            //追加long数值数据
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
                resultData.longToggleListMap = buildLongArrayMap(longToggleMap);
                resultData.stringToggleListMap = buildStringArrayMap(stringToggleMap);
                resultData.longValueMap = longValueMap;
                return resultData;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        private DoubleKeyValueMap<Integer,Boolean, ArrayList<Long>> buildLongArrayMap(LongToggleMap longToggleMap)
        {
            DoubleKeyValueMap<Integer, Boolean, ArrayList<Long>> longArrayMap = new DoubleKeyValueMap<>();
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

                    ArrayList<Long>[] arrayLists = buildLongListArray(toggleIterator);
                    if(null != arrayLists[0] && arrayLists[0].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), true, arrayLists[0]);
                    }

                    if(null != arrayLists[1] && arrayLists[1].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), false, arrayLists[1]);
                    }
                }
            }

            return longArrayMap;
        }

        private ArrayList<Long>[] buildLongListArray(Iterator<Map.Entry<Long,Toggle>> toggleIterator)
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
            ArrayList<Long>[] listArray = new ArrayList[2];
            listArray[0] = longList;
            listArray[1] = longReverseList;
            return listArray;
        }

        private DoubleKeyValueMap<Integer,Boolean, ArrayList<String>> buildStringArrayMap(StringToggleMap stringToggleMap)
        {
            DoubleKeyValueMap<Integer, Boolean, ArrayList<String>> longArrayMap = new DoubleKeyValueMap<>();
            if(stringToggleMap.size() > 0)
            {
                Iterator<Map.Entry<Integer, ConcurrentHashMap<String, Toggle>>> iterator = stringToggleMap.entrySet().iterator();
                Map.Entry<Integer, ConcurrentHashMap<String, Toggle>> entry;
                ConcurrentHashMap<String, Toggle> toggleMap;
                Iterator<Map.Entry<String,Toggle>> toggleIterator;
                while (iterator.hasNext())
                {
                    entry = iterator.next();
                    toggleMap = entry.getValue();
                    toggleIterator = toggleMap.entrySet().iterator();

                    ArrayList<String>[] listArray = buildStringListArray(toggleIterator);
                    if(null != listArray[0] && listArray[0].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), true, listArray[0]);
                    }

                    if(null != listArray[1] && listArray[1].size() > 0)
                    {
                        longArrayMap.put(entry.getKey(), false, listArray[1]);
                    }
                }
            }

            return longArrayMap;
        }

        private ArrayList<String>[] buildStringListArray(Iterator<Map.Entry<String,Toggle>> toggleIterator)
        {
            ArrayList<String> stringList = new ArrayList<>();
            ArrayList<String> stringReverseList = new ArrayList<>();
            Map.Entry<String, Toggle> toggleEntry;
            Toggle toggle;
            while (toggleIterator.hasNext())
            {
                toggleEntry = toggleIterator.next();
                toggle = toggleEntry.getValue();
                if(toggle.isChanged())
                {
                    if(toggle.getState())
                    {
                        stringList.add(toggleEntry.getKey());
                    }
                    else
                    {
                        stringReverseList.add(toggleEntry.getKey());
                    }
                }
            }
            ArrayList<String>[] listArray = new ArrayList[2];
            listArray[0] = stringList;
            listArray[1] = stringReverseList;
            return listArray;
        }
    }

    private static class LongToggleMap extends DoubleKeyValueMap<Integer, Long, Toggle>
    {
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

    private static class StringToggleMap extends DoubleKeyValueMap<Integer,String, Toggle>
    {
        @Override
        public ConcurrentHashMap<String, Toggle> get(Integer key1)
        {
            ConcurrentHashMap<String, Toggle> value = super.get(key1);
            if(null == value)
            {
                value = new ConcurrentHashMap<>();
                super.put(key1, value);
            }
            return value;
        }
    }
}
