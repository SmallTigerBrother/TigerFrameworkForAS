package com.mn.tiger.lbs.location;

import android.os.Bundle;
import android.view.ViewGroup;

/**
 * 地图管理类
 * Created by Dalang on 2015/8/23.
 */
public interface IMapManager
{
    /**
     * 初始化地图
     * @param mapContainer 地图MapView的父视图
     * @param savedInstanceState
     */
    void init(ViewGroup mapContainer, Bundle savedInstanceState);

    /**
     * 保存状态
     * @param outState
     */
    void onSaveInstanceState(Bundle outState);

    /**
     * 在Activity的onDestroy中调用
     */
    void onDestroy();

    /**
     * 在Activity的onResume中调用
     */
    void onResume();

    /**
     * 在Activity的onPause中调用
     */
    void onPause();

    /**
     * 添加地标
     * @param latitude 纬度
     * @param langitude 精度
     * @param title 标题
     */
    void addMarker(double latitude, double langitude, String title);

    /**
     * 将地图中心点转至指定点
     * @param latitude 纬度
     * @param longitude 经度
     */
    void centerTo(double latitude, double longitude);

    /**
     * 显示我的位置
     */
    void showMyLocation();

    /**
     * 清空地图上所有的地物
     */
    void clear();
}
