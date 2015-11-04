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

    /**
     * 绑定Data类型，ViewType的Map，Key为数据class的名称，Value为ViewType
     */
    private HashMap<String, Integer> dataViewTypeMap;

    /**
     * 绑定ViewType，ViewHolder的Map，Key为ViewType，Value为ViewHolder的类型
     */
    private HashMap<Integer, Class<?>> viewTypeHolderMap;

    /**
     * 绑定Data类型，ViewHolder的Map，Key为数据class的名称，Value为ViewHolder的类型
     */
    private HashMap<String, Class<?>> dataTypeHolderMap;

    public TGViewTypeBinder(TGRecyclerViewAdapter adapter, Class<? extends TGRecyclerViewHolder>[] viewHolderClasses)
    {
        this.adapter = adapter;
        this.dataViewTypeMap = new HashMap<String, Integer>();
        this.viewTypeHolderMap = new HashMap<Integer, Class<?>>();

        //初始化绑定Data类型，ViewHolder的Map
        dataTypeHolderMap = new HashMap<String, Class<?>>();
        int count = viewHolderClasses.length;
        for(int i = 0; i < count; i++)
        {
            dataTypeHolderMap.put(Commons.getClassNameOfGenericType(viewHolderClasses[i], 0), viewHolderClasses[i]);
        }
    }

    /**
     * 获取指定位置的ViewType
     * @param position
     * @return
     */
    public int getItemViewType(int position)
    {
        String dataClass = adapter.getItem(position).getClass().getCanonicalName();
        Integer viewType = dataViewTypeMap.get(dataClass);

        //如果当前的ViewType不存在，自动生成一个ViewType，并绑定DataClass、ViewHolderClass
        if(null == viewType || viewType.intValue() == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            viewType = generateViewType();

            //绑定DataClass
            dataViewTypeMap.put(dataClass, Integer.valueOf(viewType));

            //绑定ViewHolderClass
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

    /**
     * 根据ViewType，新建一个TGRecyclerViewHolder
     * @param viewType
     * @return
     */
    public TGRecyclerViewHolder newViewHolderByType(int viewType)
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

    /**
     * 判断viewHolder中持有的ViewType，与DataClass绑定的ViewType是否一致
     * @param viewHolder
     * @param dataClass
     * @return
     */
    boolean isSameDataType(TGRecyclerViewHolder viewHolder, Class<?> dataClass)
    {
        Class<?> viewHolderClass = dataTypeHolderMap.get(dataClass.getCanonicalName());
        if(null != viewHolderClass && viewHolderClass.isInstance(viewHolder))
        {
            return true;
        }
        return false;
    }

    /**
     * 自动生成ViewType
     * @return
     */
    public static int generateViewType()
    {
        VIEW_TYPE += 1;

        return VIEW_TYPE;
    }
}
