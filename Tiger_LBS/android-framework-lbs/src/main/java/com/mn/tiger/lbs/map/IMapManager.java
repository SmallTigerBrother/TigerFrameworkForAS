package com.mn.tiger.lbs.map;

import android.graphics.Bitmap;
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
     * 禁止滚动父视图接收事件
     * @param scrollParent
     */
    void disallowScrollParentInterceptTouchEvent(ViewGroup scrollParent);

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
     * @param langitude 经度
     * @param title 标题
     */
    IMarker addMarker(double latitude, double langitude, String title);

    /**
     * 添加地标
     * @param latitude 纬度
     * @param longitude 经度
     * @param title 标题
     * @param snippet 简述
     * @return
     */
    IMarker addMarker(double latitude, double longitude, String title, String snippet);

    IMarker addMarker(double latitude, double longitude, String title,String snippet, int iconRes);

    /**
     * 添加地标
     * @param latitude 维度
     * @param longitude 经度
     * @param title 标题
     * @param snippet 简述
     * @param iconRes 图标
     * @param params 参数
     * @return
     */
    IMarker addMarker(double latitude, double longitude, String title, String snippet,int iconRes, Object params);

    /**
     * 将地图中心点转至指定点
     * @param latitude 纬度
     * @param longitude 经度
     */
    void centerTo(double latitude, double longitude);

    void centerZoomTo(double latitude, double longitude, float zoom);

    void zoomTo(float zoom);

    float getZoom();

    /**
     * 显示我的位置
     */
    void showMyLocation();

    /**
     * 清空地图上所有的地物
     */
    void clear();

    void setOnMapLongClickListener(OnMapLongClickListener listener);

    interface OnMapLongClickListener
    {
        void onLongClick(double latitude, double longitude);
    }

    interface OnMapScreenShotListener
    {
        void onMapScreenShot(Bitmap bitmap, String filePath);
    }
}
