package com.mn.tiger.widget.recyclerview;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.Commons;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by peng on 15/11/4.
 */
class TGViewTypeBinder
{
    private static final Logger LOG = Logger.getLogger(TGViewTypeBinder.class);

    private TGRecyclerViewAdapter adapter;

    private int VIEW_TYPE = -10000;

    /**
     * 绑定ViewType，ViewHolder的Map，Key为ViewType，Value为ViewHolder的类型
     */
    private HashMap<Integer, Class<?>> viewTypeHolderMap;

    /**
     * 绑定Data类型，ViewHolder的Map，Key为数据class的名称，Value为ViewHolder的类型
     */
    private HashMap<String, Class<?>> dataTypeHolderMap;

    /**
     * 绑定position，ViewType的Map，Key为position，Value为ViewType
     */
    private HashMap<Integer,Integer> positionViewTypeMap;

    /**
     * 多类型ViewHolder的简单实例
     */
    private ArrayList<TGRecyclerViewHolder> viewHolderInstances;

    public TGViewTypeBinder(TGRecyclerViewAdapter adapter, Class<? extends TGRecyclerViewHolder>[] viewHolderClasses)
    {
        this.adapter = adapter;
        this.viewTypeHolderMap = new HashMap<>();
        this.positionViewTypeMap = new HashMap<>();
        //初始化各个类型ViewHolder的代理实例
        this.viewHolderInstances = new ArrayList<>(viewHolderClasses.length);
        //初始化绑定Data类型，ViewHolder的Map
        this.dataTypeHolderMap = new HashMap<>();
        int count = viewHolderClasses.length;
        try
        {
            for(int i = 0; i < count; i++)
            {
                TGRecyclerViewHolder viewHolder = viewHolderClasses[i].newInstance();
                viewHolder.setAdapter(adapter);
                viewHolderInstances.add(viewHolder);
                dataTypeHolderMap.put(Commons.getClassNameOfGenericType(viewHolderClasses[i], 0), viewHolderClasses[i]);
            }
        }
        catch (Exception e)
        {
            LOG.e(e);
        }
    }

    /**
     * 获取指定位置的ViewType
     * @param position
     * @return
     */
    public int getItemViewType(int position)
    {
        //首先根据position生成ViewType
        int viewType = generateViewTypeByPosition(position);
        //若根据position生成的ViewType不合法，则通过泛型参数类型生成ViewType
        if (viewType == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            viewType = generateViewTypeByGenericParamType(position);
        }

        if(viewType == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            throw new RuntimeException("Illegal viewType of position " + position);
        }

        return viewType;
    }

    /**
     * 根据位置生成ViewType
     * @param position
     * @return
     */
    private int generateViewTypeByPosition(int position)
    {
        Integer viewType = positionViewTypeMap.get(position);
        if(null == viewType || viewType == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            for (TGRecyclerViewHolder viewHolderInstance : viewHolderInstances)
            {
                viewHolderInstance.setPosition(position);
                viewType = viewHolderInstance.getItemViewType(position);
                if(viewType != TGRecyclerViewAdapter.NONE_VIEW_TYPE)
                {
                    positionViewTypeMap.put(position,viewType);
                    viewTypeHolderMap.put(viewType, viewHolderInstance.getClass());
                    return viewType;
                }
            }
        }

        return null != viewType ? viewType : TGRecyclerViewAdapter.NONE_VIEW_TYPE;
    }

    /**
     * 根据泛型参数类型生成ViewType
     * @param position
     * @return
     */
    private int generateViewTypeByGenericParamType(int position)
    {
        Integer viewType = positionViewTypeMap.get(position);
        if(null == viewType || viewType == TGRecyclerViewAdapter.NONE_VIEW_TYPE)
        {
            String dataClass = adapter.getItem(position).getClass().getCanonicalName();
            viewType = generateViewType();
            //绑定ViewType
            Class<?> viewHolderClass = dataTypeHolderMap.get(dataClass);
            if(null != viewHolderClass)
            {
                positionViewTypeMap.put(position, viewType);
                viewTypeHolderMap.put(viewType,viewHolderClass);
                return viewType;
            }
            else
            {
                throw new IllegalArgumentException("can not find ViewHolder bind with " + dataClass);
            }
        }

        return TGRecyclerViewAdapter.NONE_VIEW_TYPE;
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
     * 自动生成ViewType
     * @return
     */
    public int generateViewType()
    {
        VIEW_TYPE -= 1;
        return VIEW_TYPE;
    }
}
