package com.mn.tiger.widget.recyclerview;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.Commons;

import java.util.HashMap;

/**
 * Created by peng on 15/11/4.
 */
class TGViewTypeBinder
{
    private static final Logger LOG = Logger.getLogger(TGViewTypeBinder.class);

    private TGRecyclerViewAdapter adapter;

    private static int VIEW_TYPE = 1;

    private HashMap<String, Integer> viewTypeMap;

    private HashMap<Integer, Class<?>> viewTypeHolderMap;

    private HashMap<String, Class<?>> dataTypeHolderMap;

    public TGViewTypeBinder(TGRecyclerViewAdapter adapter, Class<? extends TGRecyclerViewHolder>[] viewHolderClasses)
    {
        this.adapter = adapter;
        this.viewTypeMap = new HashMap<String, Integer>();
        this.viewTypeHolderMap = new HashMap<Integer, Class<?>>();

        dataTypeHolderMap = new HashMap<String, Class<?>>();
        int count = viewHolderClasses.length;
        for(int i = 0; i < count; i++)
        {
            dataTypeHolderMap.put(Commons.getClassNameOfGenericType(viewHolderClasses[i], 0), viewHolderClasses[i]);
        }
    }

    public int getItemViewType(int position)
    {
        String dataClass = adapter.getItem(position).getClass().getCanonicalName();
        Integer viewType = viewTypeMap.get(dataClass);
        if(null == viewType || viewType.intValue() == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            viewType = generateViewType();
            viewTypeMap.put(dataClass, Integer.valueOf(viewType));
            Class<?> viewHolderClass = dataTypeHolderMap.get(dataClass);
            if(null != viewHolderClass)
            {
                viewTypeHolderMap.put(viewType, dataTypeHolderMap.get(dataClass));
            }
            else
            {
                LOG.e("[Method:getItemViewType] position == " + position + "  dataClass == " + dataClass);
            }
        }
        return viewType;
    }

    public TGRecyclerViewHolder initViewHolderByType(int viewType)
    {
        try
        {
            return (TGRecyclerViewHolder)viewTypeHolderMap.get(viewType).newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isSameDataType(TGRecyclerViewHolder viewHolder, Class<?> dataClass)
    {
        Class<?> viewHolderClass = dataTypeHolderMap.get(dataClass.getCanonicalName());
        if(null != viewHolderClass && viewHolderClass.isInstance(viewHolder))
        {
            return true;
        }
        return false;
    }

    public static int generateViewType()
    {
        VIEW_TYPE += 1;

        return VIEW_TYPE;
    }
}
