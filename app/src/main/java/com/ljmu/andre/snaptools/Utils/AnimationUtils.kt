package com.ljmu.andre.snaptools.Utils

import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 26.05.20 - Time 14:29.
 */

fun RecyclerView.animateSequentGroup() {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            //At this point the layout is complete and the
            //dimensions of recyclerView and any child views are known.
            AnimationUtils.sequentGroup(this@animateSequentGroup)
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}