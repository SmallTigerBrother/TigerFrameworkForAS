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
     * 绑定ViewHolder类型、ViewHolder实例的Map，Key为ViewHolder类型，Value为ViewHolder的实例，多类型ViewHolder的简单实例
     */
    private HashMap<Class<?>, TGRecyclerViewHolder> viewHolderInstances;

    /**
     * 绑定ViewType，不支持重用的ViewHolder的Map，Key为ViewType，Value为ViewHolder的实例，仅存储不支持重用的ViewHolder
     */
    private HashMap<Integer, TGRecyclerViewHolder> unRecyclableViewHolders;

    public TGViewTypeBinder(TGRecyclerViewAdapter adapter, Class<? extends TGRecyclerViewHolder>[] viewHolderClasses)
    {
        this.adapter = adapter;
        this.viewTypeHolderMap = new HashMap<>();
        this.positionViewTypeMap = new HashMap<>();
        //初始化各个类型ViewHolder的代理实例
        this.viewHolderInstances = new HashMap<>(viewHolderClasses.length);
        //初始化绑定Data类型，ViewHolder的Map
        this.dataTypeHolderMap = new HashMap<>(viewHolderClasses.length);
        int count = viewHolderClasses.length;
        try
        {
            for(int i = 0; i < count; i++)
            {
                TGRecyclerViewHolder viewHolder = viewHolderClasses[i].newInstance();
                Class<?> viewHolderClass = viewHolder.getClass();
                viewHolder.setAdapter(adapter);
                if(!viewHolder.recycleAble())
                {
                    if(null == unRecyclableViewHolders)
                    {
                        unRecyclableViewHolders = new HashMap<>();
                    }
                }
                viewHolderInstances.put(viewHolderClass, viewHolder);
                dataTypeHolderMap.put(Commons.getClassNameOfGenericType(viewHolderClass, 0), viewHolderClass);
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
    public int generateItemViewType(int position)
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

        //若该ViewHolder不支持重用，即刻初始化
        if(this.adapter.enableUnRecycleViewHolder)
        {
            TGRecyclerViewHolder viewHolderInstance = viewHolderInstances.get(viewTypeHolderMap.get(viewType));
            if(!viewHolderInstance.recycleAble())
            {
                //判断是否已初始化过该ViewType
                if(!unRecyclableViewHolders.containsKey(viewType))
                {
                    try
                    {
                        TGRecyclerViewHolder viewHolder = viewHolderInstance.getClass().newInstance();
                        this.adapter.initViewHolder(viewHolder);
                        viewHolder.convertView = viewHolder.initView(adapter.parent, viewType);
                        viewHolder.fillData(adapter.parent, viewHolder.convertView,
                                this.adapter.getItem(position), position, viewType);
                        unRecyclableViewHolders.put(viewType, viewHolder);
                    }
                    catch (Exception e)
                    {
                        LOG.e(e);
                    }
                }
            }
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
            for (TGRecyclerViewHolder viewHolderInstance : viewHolderInstances.values())
            {
                viewHolderInstance.setPosition(position);
                viewType = viewHolderInstance.getItemViewType(position);
                //缓存ViewType
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
            viewType = createViewType();
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
            //检查ViewHolder是否支持重用
            TGRecyclerViewHolder viewHolder = getUnRecyclableViewHolder(viewType);
            return null == viewHolder ? (TGRecyclerViewHolder)viewTypeHolderMap.get(viewType).newInstance() : viewHolder;
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
    public int createViewType()
    {
        VIEW_TYPE -= 1;
        return VIEW_TYPE;
    }

    /**
     * 获取不支持重用的ViewHolder实例
     * @param viewType
     * @return
     */
    TGRecyclerViewHolder getUnRecyclableViewHolder(int viewType)
    {
        if(null == unRecyclableViewHolders)
        {
            return null;
        }
        return unRecyclableViewHolders.get(viewType);
    }
}
