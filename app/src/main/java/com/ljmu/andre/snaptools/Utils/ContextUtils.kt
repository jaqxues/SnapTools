package com.ljmu.andre.snaptools.Utils

import android.content.Context
import android.view.LayoutInflater


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 31.05.20 - Time 14:15.
 */
inline val Context.layoutInflater: LayoutInflater
    get() {
        return LayoutInflater.from(this)
    }