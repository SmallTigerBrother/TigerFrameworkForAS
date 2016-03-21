package com.mn.tiger.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;

import com.mn.tiger.core.ActivityObserver;
import com.mn.tiger.task.TGTaskManager;
import com.mn.tiger.task.TaskType;
import com.mn.tiger.utility.CR;
import com.mn.tiger.utility.SystemBarConfigs;
import com.mn.tiger.widget.TGImageButton;
import com.mn.tiger.widget.TGNavigationBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * 带导航条的Activity基类
 */
public class TGActionBarActivity extends Activity
{
    /**
     * 导航条
     */
    private TGNavigationBar navigationBar;

    /**
     * 主视图
     */
    private FrameLayout panelLayout;

    /**
     * 加载框
     */
    private DialogFragment loadingDialog;

    /**
     * laoding对话框显示计数器
     */
    private int dialogShowCount = 0;

    /**
     * Activity声明周期观察者
     */
    private Vector<ActivityObserver> observers;

    private ArrayList<Integer> httpTaskIDList = new ArrayList<Integer>();

    private SystemBarConfigs systemBarConfigs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        systemBarConfigs = SystemBarConfigs.newSystemBarManagerConfigs(this);
        initSystemBarConfigs(systemBarConfigs);
        super.setContentView(systemBarConfigs.getActivityLayoutId());


        panelLayout = (FrameLayout) findViewById(CR.getViewId(this, "tiger_panel"));
        navigationBar = (TGNavigationBar) findViewById(CR.getViewId(this, "tiger_navigationbar"));
        navigationBar.getLeftNaviButton().setVisibility(View.VISIBLE);
        initNavigationResource(navigationBar);
        initPanelLayout(panelLayout);

        //添加到Application中
        TGApplicationProxy.getInstance().addActivityToStack(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params)
    {
        panelLayout.addView(view, params);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(layoutResID, null);

        this.setContentView(contentView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view)
    {
        if (null == view.getLayoutParams())
        {
            this.setContentView(view, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        else
        {
            this.setContentView(view, view.getLayoutParams());
        }
    }

    /**
     * 初始化导航条设置项
     * @param configs
     */
    protected void initSystemBarConfigs(SystemBarConfigs configs)
    {

    }

    /**
     * 该方法的作用: 初始化导航条资源
     *
     * @param navigationBar
     * @date 2013-11-8
     */
    protected void initNavigationResource(TGNavigationBar navigationBar)
    {
        navigationBar.setBackgroundResource(CR.getDrawableId(this,
                "tiger_header_background"));

        navigationBar.getLeftNaviButton().setBackgroundResource(
                CR.getDrawableId(this, "tiger_nav_back_button_selector"));

        showLeftBarButton(true);
        navigationBar.getLeftNaviButton().setOnClickListener(
                new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                });
        navigationBar.getRightNaviButton().setBackgroundResource(
                CR.getDrawableId(this, "tiger_nav_refresh_button_selector"));
    }

    /**
     * 初始化PanelLayout
     *
     * @param panelLayout
     */
    protected void initPanelLayout(FrameLayout panelLayout)
    {

    }

    /**
     * 该方法的作用: 获取导航条左按钮
     *
     * @return
     * @date 2013-11-18
     */
    public TGImageButton getLeftBarButton()
    {
        return navigationBar.getLeftNaviButton();
    }

    /**
     * 该方法的作用: 获取导航条右按钮
     *
     * @return
     * @date 2013-11-18
     */
    public TGImageButton getRightBarButton()
    {
        return navigationBar.getRightNaviButton();
    }

    /**
     * 该方法的作用: 获取导航条
     *
     * @return
     * @date 2013-11-8
     */
    public TGNavigationBar getNavigationBar()
    {
        return navigationBar;
    }

    /**
     * 该方法的作用: 获取导航条中间TextView
     *
     * @return
     * @date 2013-11-18
     */
    public TGImageButton getMiddleTextView()
    {
        return navigationBar.getMiddleTextView();
    }

    /**
     * 该方法的作用: 设置导航条标题文本
     *
     * @param titleText
     * @return
     * @date 2013-11-18
     */
    public boolean setBarTitleText(String titleText)
    {
        return navigationBar.setMiddleText(titleText);
    }

    /**
     * 该方法的作用: 显示导航条左按钮
     *
     * @param show
     * @date 2013-11-18
     */
    public void showLeftBarButton(boolean show)
    {
        TGImageButton leftButton = getLeftBarButton();
        if (null != leftButton)
        {
            if (show)
            {
                leftButton.setVisibility(View.VISIBLE);
            }
            else
            {
                leftButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 该方法的作用: 显示导航条右按钮
     *
     * @param show
     * @date 2013-11-18
     */
    public void showRightBarButton(boolean show)
    {
        TGImageButton rightButton = getRightBarButton();
        if (null != rightButton)
        {
            if (show)
            {
                rightButton.setVisibility(View.VISIBLE);
            }
            else
            {
                rightButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        TGApplicationProxy.getInstance().removeActivityFromStack(this);
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().onDestroy();
            }
            //清空观察者，防止内存泄露
            observers.clear();
        }

        onCancelHttpLoader();

        super.onDestroy();
    }

    /**
     * 将HttpLoader的taskID注册到Activity中
     *
     * @param taskID
     */
    public void registerHttpLoader(int taskID)
    {
        httpTaskIDList.add(taskID);
    }

    /**
     * 取消注册HttpLoader
     *
     * @param taskID
     */
    public void unRegisterHttpLoader(int taskID)
    {
        httpTaskIDList.remove(Integer.valueOf(taskID));
    }

    /**
     * 取消Http请求
     */
    protected void onCancelHttpLoader()
    {
        for (Integer taskID : httpTaskIDList)
        {
            TGTaskManager.getInstance().cancelTask(taskID, TaskType.TASK_TYPE_HTTP);
        }
        httpTaskIDList.clear();
    }

    /**
     * 设置导航条是否可见
     *
     * @param navigationBarVisible
     */
    public void setNavigationBarVisible(boolean navigationBarVisible)
    {
        if (navigationBarVisible)
        {
            navigationBar.setVisibility(View.VISIBLE);
        }
        else
        {
            navigationBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu)
    {
        //方式flyme系统中弹出清理内存工具条时，应用崩溃
        return false;
    }

    /**
     * 显示进度对话框
     */
    public void showLoadingDialog()
    {
        if (null == loadingDialog)
        {
            loadingDialog = initLoadingDialog();
        }

        if (null != loadingDialog)
        {
            if (dialogShowCount <= 0)
            {
                dialogShowCount = 0;
                loadingDialog.show(getFragmentManager(), "loadingDialog");
            }
            dialogShowCount++;
        }
    }

    /**
     * 隐藏进度对话框
     */
    public void dismissLoadingDialog()
    {
        dialogShowCount--;
        if (loadingDialog != null && dialogShowCount <= 0)
        {
            loadingDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * 初始化进度对话框
     *
     * @return
     */
    protected DialogFragment initLoadingDialog()
    {
        return null;
    }

    /**
     * 启动指定的Activity
     *
     * @param clazz
     */
    protected void startActivity(Class clazz)
    {
        startActivity(new Intent(this, clazz));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().onResume();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().onPause();
            }
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().onStop();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * 注册Activity观察者
     *
     * @param activityObserver
     */
    public void registerActivityObserver(ActivityObserver activityObserver)
    {
        if (null == observers)
        {
            observers = new Vector<ActivityObserver>();
        }
        observers.add(activityObserver);
    }

    /**
     * 注销Activity观察者
     *
     * @param activityObserver
     */
    public void unregisterActivityObserver(ActivityObserver activityObserver)
    {
        if (null != observers && null != activityObserver && observers.contains(activityObserver))
        {
            observers.remove(activityObserver);
        }
    }

    @Override
    public void onBackPressed()
    {
        boolean canBack = true;
        if (null != observers)
        {
            Iterator<ActivityObserver> iterator = observers.iterator();
            while (iterator.hasNext())
            {
                boolean interceptByObserver = iterator.next().onBackPressed();
                canBack = canBack ? !interceptByObserver : false;
            }
        }

        if (canBack)
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN)
        {
            onTrimMemoryUIHidden();
        }
    }

    /**
     * 当UI不再显示时回调，用于清理内存
     */
    protected void onTrimMemoryUIHidden()
    {

    }

    protected SystemBarConfigs getSystemBarConfigs()
    {
        return systemBarConfigs;
    }

    protected int getResultCode()
    {
        try
        {
            Field resultCodeField = Activity.class.getDeclaredField("mResultCode");
            resultCodeField.setAccessible(true);
            return resultCodeField.getInt(this);
        }
        catch (Exception e)
        {
            Log.e(this.getClass().getSimpleName(), "[Method:getResultCode]" + e);
            return RESULT_CANCELED;
        }
    }
}
