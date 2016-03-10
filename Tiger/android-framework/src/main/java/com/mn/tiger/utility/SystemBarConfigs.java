package com.mn.tiger.utility;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mn.tiger.app.TGActionBarActivity;

/**
 * Created by peng on 16/3/10.
 */
public class SystemBarConfigs
{
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";

    protected int statusBarHeight;

    protected TGActionBarActivity activity;

    private boolean translucentStatusBar = false;

    private Mode mode = Mode.NON_FIT_WINDOW;

    private boolean statusBarVisible = true;

    private int statusBarColor = Color.TRANSPARENT;

    private SystemBarManager manager;

    private SystemBarConfigs(TGActionBarActivity activity)
    {
        this.activity = activity;
        statusBarHeight = getInternalDimensionSize(activity.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    public static SystemBarConfigs newSystemBarManagerConfigs(TGActionBarActivity activity)
    {
        return new SystemBarConfigs(activity);
    }

    public int getActivityLayoutId()
    {
        switch (mode)
        {
            case FIT_WINDOW:
                return CR.getLayoutId(activity,"tiger_main_systembar_tint");
            case NON_FIT_WINDOW:
                return CR.getLayoutId(activity,"tiger_main_systembar_tint_nonfit");
            default:
                return CR.getLayoutId(activity,"tiger_main_systembar_tint");
        }
    }

    public SystemBarConfigs setTranslucentStatusBar(boolean translucentStatusBar)
    {
        this.translucentStatusBar = translucentStatusBar;
        return this;
    }

    public boolean isTranslucentStatusBar()
    {
        return translucentStatusBar;
    }

    public Mode getSystemBarMode()
    {
        return mode;
    }

    public SystemBarConfigs setSystemBarMode(Mode mode)
    {
        this.mode = mode;
        return this;
    }

    public SystemBarConfigs setStatusBarColor(int color)
    {
        this.statusBarColor = color;
        return this;
    }

    public SystemBarConfigs setStatusBarVisible(boolean visible)
    {
        this.statusBarVisible = visible;
        return this;
    }

    private void initSystemBarManager()
    {
        if(null == manager)
        {
            switch (mode)
            {
                case FIT_WINDOW:
                    manager = new FitWindowSystemBarManager(activity, this);
                    break;
                case NON_FIT_WINDOW:
                    manager = new NonFitWindowSystemBarManager(activity,this);
                    break;
                default:
                    manager = new FitWindowSystemBarManager(activity,this);
                    break;
            }
        }
    }

    public void notifyConfigChanged()
    {
        initSystemBarManager();
        manager.setTranslucentStatusBar(translucentStatusBar);
        if(translucentStatusBar)
        {
            manager.setStatusBarColor(statusBarColor);
            if(!statusBarVisible)
            {
                manager.hideStatusBar();
            }
            else
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    manager.showStatusBar();
                }
                else
                {
                    manager.hideStatusBar();
                }
            }
        }
    }

    private int getInternalDimensionSize(Resources res, String key)
    {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0)
        {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static abstract class SystemBarManager
    {
        protected TGActionBarActivity activity;

        public SystemBarManager(TGActionBarActivity activity)
        {
            this.activity = activity;
        }

        public void setTranslucentStatusBar(boolean on)
        {
            Window window = activity.getWindow();
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            {
                WindowManager.LayoutParams winParams = window.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                if(on)
                {
                    winParams.flags |= bits;
                }
                else
                {
                    winParams.flags &= ~bits;
                }
                window.setAttributes(winParams);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && on)
            {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }

        public abstract void setStatusBarColor(int color);

        public abstract void hideStatusBar();

        public abstract void showStatusBar();
    }

    private static class NonFitWindowSystemBarManager extends SystemBarManager
    {
        private SystemBarConfigs configs;

        private View statusBar;

        public NonFitWindowSystemBarManager(TGActionBarActivity activity,SystemBarConfigs configs)
        {
            super(activity);
            this.configs = configs;
            statusBar = activity.findViewById(CR.getViewId(activity,"tiger_status_bar"));
            statusBar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, configs.statusBarHeight));
        }

        @Override
        public void setTranslucentStatusBar(boolean on)
        {
            super.setTranslucentStatusBar(on);
            if(!on)
            {
                statusBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void setStatusBarColor(int color)
        {
            statusBar.setBackgroundColor(color);
        }

        @Override
        public void hideStatusBar()
        {
            statusBar.setVisibility(View.GONE);
        }

        @Override
        public void showStatusBar()
        {
            statusBar.setVisibility(View.VISIBLE);
        }
    }

    private static class FitWindowSystemBarManager extends SystemBarManager
    {
        private SystemBarConfigs configs;

        private View mainLayout;

        public FitWindowSystemBarManager(TGActionBarActivity activity, SystemBarConfigs configs)
        {
            super(activity);
            this.configs = configs;
            mainLayout = activity.findViewById(CR.getViewId(activity,"tiger_main_layout"));
        }

        @Override
        public void setTranslucentStatusBar(boolean on)
        {
            super.setTranslucentStatusBar(on);
            if(!on)
            {
                showStatusBar();
            }
        }

        @Override
        public void setStatusBarColor(int color)
        {
            mainLayout.setBackgroundColor(color);
        }

        @Override
        public void hideStatusBar()
        {
        }

        @Override
        public void showStatusBar()
        {
        }
    }

    public enum Mode
    {
        FIT_WINDOW,
        NON_FIT_WINDOW
    }
}
