package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Andre on 07/09/2016.
 */
public interface GestureEvent {
    ReturnType onTouch(View v, MotionEvent event);

    void reset();

    enum ReturnType {
        COMPLETED, FAILED, SAVE, PROCESSING, TAP
    }
}


