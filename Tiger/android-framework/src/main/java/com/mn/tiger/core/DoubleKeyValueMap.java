package com.mn.tiger.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DoubleKeyValueMap<K1, K2, V> implements Serializable
{
    private ConcurrentHashMap<K1, ConcurrentHashMap<K2, V>> k1_k2V_map;

    public DoubleKeyValueMap()
    {
        this.k1_k2V_map = new ConcurrentHashMap<K1, ConcurrentHashMap<K2, V>>();
    }

    protected void put(K1 key1, ConcurrentHashMap<K2, V> values)
    {
        this.k1_k2V_map.put(key1, values);
    }

    public void put(K1 key1, K2 key2, V value)
    {
        if (k1_k2V_map.containsKey(key1))
        {
            ConcurrentHashMap<K2, V> k2V_map = k1_k2V_map.get(key1);
            k2V_map.put(key2, value);
        }
        else
        {
            ConcurrentHashMap<K2, V> k2V_map = new ConcurrentHashMap<K2, V>();
            k2V_map.put(key2, value);
            k1_k2V_map.put(key1, k2V_map);
        }
    }

    public Set<K1> getFirstKeys()
    {
        return k1_k2V_map.keySet();
    }

    public V get(K1 key1, K2 key2)
    {
        ConcurrentHashMap<K2, V> k2_v = k1_k2V_map.get(key1);
        return k2_v == null ? null : k2_v.get(key2);
    }

    public ConcurrentHashMap<K2, V> get(K1 key1)
    {
        return k1_k2V_map.get(key1);
    }

    public boolean containsKey(K1 key1, K2 key2)
    {
        if (k1_k2V_map.containsKey(key1))
        {
            return k1_k2V_map.get(key1).containsKey(key2);
        }
        return false;
    }

    public boolean containsKey(K1 key1)
    {
        return k1_k2V_map.containsKey(key1);
    }

    public int size()
    {
        return k1_k2V_map.size();
    }

    public Set<Map.Entry<K1, ConcurrentHashMap<K2, V>>> entrySet()
    {
        return k1_k2V_map.entrySet();
    }

    public void clear()
    {
        if (k1_k2V_map.size() > 0)
        {
            for (ConcurrentHashMap<K2, V> k2V_map : k1_k2V_map.values())
            {
                k2V_map.clear();
            }
            k1_k2V_map.clear();
        }
    }
}
