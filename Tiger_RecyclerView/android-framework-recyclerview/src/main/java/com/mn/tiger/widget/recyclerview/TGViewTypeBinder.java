package com.mn.tiger.widget.recyclerview;

import android.util.SparseArray;

import com.mn.tiger.log.Logger;
import com.mn.tiger.utility.Commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

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

    private RecycleArray recycleArray;

    private HashMap<Integer,TGRecyclerViewHolder> allViewHolders;

    public TGViewTypeBinder(TGRecyclerViewAdapter adapter, Class<? extends TGRecyclerViewHolder>[] viewHolderClasses)
    {
        this.adapter = adapter;
        this.allViewHolders = new HashMap<>();
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
                    if(null == recycleArray)
                    {
                        recycleArray = new RecycleArray();
                    }
                }
                viewHolderInstances.put(viewHolderClass, viewHolder);
                String dataClass = Commons.getClassNameOfGenericType(viewHolderClass, 0);
                LOG.i("[Method:TGViewTypeBinder] dataClass == " + dataClass);
                if(dataClass.equals(Object.class.getCanonicalName()))
                {
                    LOG.e("[Method:TGViewTypeBinder]the dataClass can not be Object, or you can return a constant value in 'getViewType' of TGRecyclerViewHolder");
                }

                dataTypeHolderMap.put(dataClass, viewHolderClass);
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

        //若该ViewHolder不支持重用，即刻初始化
        if(this.adapter.enableUnRecycleViewHolder)
        {
            initUnRecyclableViewHolder(position,viewType);
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
        }

        return TGRecyclerViewAdapter.NONE_VIEW_TYPE;
    }

    /**
     * 根据ViewType，新建一个TGRecyclerViewHolder
     * @param viewType
     * @return
     */
    TGRecyclerViewHolder newViewHolderByType(int viewType)
    {
        try
        {
            //检查ViewHolder是否支持重用
            TGRecyclerViewHolder viewHolder = null;
            if(null != recycleArray)
            {
                viewHolder = recycleArray.getScrapView(viewType);
                if(null == viewHolder && recycleArray.getScrapViewArrayByType(viewType).size() > 0)
                {
                    recycleArray.recycleUnRecyclableViewHolders(viewType);
                    return recycleArray.getScrapView(viewType);
                }
            }
            return null == viewHolder ? (TGRecyclerViewHolder)viewTypeHolderMap.get(viewType).newInstance() : viewHolder;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void initUnRecyclableViewHolder(int position, int viewType)
    {
        TGRecyclerViewHolder viewHolderInstance = viewHolderInstances.get(viewTypeHolderMap.get(viewType));
        if(!viewHolderInstance.recycleAble())
        {
            //判断是否已初始化过该ViewType
            if(!recycleArray.getScrapViewArrayByType(viewType).containsKey(position))
            {
                try
                {
                    //初始化ViewHolder
                    TGRecyclerViewHolder viewHolder = viewHolderInstance.getClass().newInstance();
                    this.adapter.initViewHolder(viewHolder);
                    viewHolder.convertView = viewHolder.initView(adapter.parent, viewType);
                    viewHolder.attachOnItemClickListener(viewHolder.convertView);
                    viewHolder.updateViewDimension(adapter.parent, viewHolder.convertView, adapter.getItem(position), position, viewType);
                    viewHolder.fillData(adapter.parent, viewHolder.convertView, adapter.getItem(position), position, viewType);
                    recycleArray.addScrapView(viewHolder,position,viewType);
                }
                catch (Exception e)
                {
                    LOG.e(e);
                }
            }
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

    public TGRecyclerViewHolder getRecyclerViewHolderAtPosition(int position)
    {
        TGRecyclerViewHolder viewHolder = null;
        if(null != recycleArray)
        {
            viewHolder = recycleArray.getScrapViewArrayByType(generateItemViewType(position)).get(position);
        }

        if(null == viewHolder)
        {
            viewHolder = allViewHolders.get(position);
        }

        return viewHolder;
    }

    void putViewHolder(int position, TGRecyclerViewHolder viewHolder)
    {
        viewHolder.setRecycled(false);
        allViewHolders.put(position, viewHolder);
    }

    void recycleViewHolder(int position, TGRecyclerViewHolder viewHolder)
    {
        viewHolder.setRecycled(true);
        allViewHolders.remove(position);
    }

    void recycleUnRecyclableViewHolders()
    {
        if(null != recycleArray)
        {
            recycleArray.recycleUnRecyclableViewHolders();
        }
    }

    /**
     * 保存重用视图的数组
     */
    private class RecycleArray
    {
        /**
         * 保存多种ViewType重用视图的数组
         */
        private SparseArray<LinkedHashMap<Integer, TGRecyclerViewHolder>> allScrapViewArrays;

        public RecycleArray()
        {
            allScrapViewArrays = new SparseArray<>();
        }

        /**
         * 添加弃用的视图
         * @param viewHolder
         * @param viewType
         */
        public void addScrapView(TGRecyclerViewHolder viewHolder, int position, int viewType)
        {
            //根据ViewType获取对应的数组
            LinkedHashMap<Integer, TGRecyclerViewHolder> scrapArray = allScrapViewArrays.get(viewType);
            //初始化对应类型的view数组
            if(null == scrapArray)
            {
                scrapArray = new LinkedHashMap<>();
                allScrapViewArrays.put(viewType, scrapArray);
            }
            scrapArray.put(position, viewHolder);
        }

        /**
         * 获取弃用的视图
         * @param viewType
         * @return
         */
        public TGRecyclerViewHolder getScrapView(int viewType)
        {
            //根据viewType获取view列表
            HashMap<Integer,TGRecyclerViewHolder> viewArray = getScrapViewArrayByType(viewType);
            TGRecyclerViewHolder viewHolder;
            Iterator<TGRecyclerViewHolder> iterator = viewArray.values().iterator();
            while (iterator.hasNext())
            {
                viewHolder = iterator.next();
                if(viewHolder.isRecycled())
                {
                    viewHolder.setRecycled(false);
                    return viewHolder;
                }
            }
            return null;
        }

        /**
         * 根据ViewType获取弃用View的数组
         */
        private HashMap<Integer,TGRecyclerViewHolder> getScrapViewArrayByType(int viewType)
        {
            LinkedHashMap<Integer,TGRecyclerViewHolder> scrapArray = allScrapViewArrays.get(viewType);
            if(null == scrapArray)
            {
                scrapArray = new LinkedHashMap<>();
                allScrapViewArrays.put(viewType, scrapArray);
            }
            return scrapArray;
        }

        public void recycleUnRecyclableViewHolders(int viewType)
        {
            LinkedHashMap<Integer,TGRecyclerViewHolder> viewHolders = recycleArray.allScrapViewArrays.get(viewType);
            Iterator<TGRecyclerViewHolder> iterator = viewHolders.values().iterator();
            while (iterator.hasNext())
            {
                iterator.next().setRecycled(true);
            }
        }

        public void recycleUnRecyclableViewHolders()
        {
            if(null != recycleArray)
            {
                int count = recycleArray.allScrapViewArrays.size();
                for (int i = 0; i < count; i++)
                {
                    int viewType = recycleArray.allScrapViewArrays.keyAt(i);
                    recycleUnRecyclableViewHolders(viewType);
                }
            }
        }
    }
}
