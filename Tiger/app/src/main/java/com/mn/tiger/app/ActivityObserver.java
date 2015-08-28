package com.mn.tiger.app;

import android.content.Intent;

/**
 * Created by peng on 15/8/22.
 * Activity生命周期观察者
 */
public interface ActivityObserver
{
    void onResume();

    void onDestroy();

    void onBackPressed();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
