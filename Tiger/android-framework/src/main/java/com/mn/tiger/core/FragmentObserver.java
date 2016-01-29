package com.mn.tiger.core;

import android.content.Intent;

/**
 * Created by peng on 16/1/29.
 */
public interface FragmentObserver
{
    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
