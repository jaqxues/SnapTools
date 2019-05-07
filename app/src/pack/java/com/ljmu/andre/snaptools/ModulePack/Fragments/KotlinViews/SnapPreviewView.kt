package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class SnapPreviewView {
    @SuppressLint("ResourceType")
    fun getContainer(activity: Activity): ViewGroup =
            getModuleContext(activity).UI {
                frameLayout {
                    lparams(width = wrapContent, height = wrapContent, gravity = Gravity.BOTTOM)
                    id = getIdFromString("preview_container")
                    backgroundResource = getDrawable(getModuleContext(activity), "border")
                    padding = 1.toDp()

                    imageView {
                        id = getIdFromString("preview_image")
                        backgroundColor = 0
                        padding = 0
                        adjustViewBounds = true
                    }.lparams(width = matchParent, height = matchParent, gravity = Gravity.BOTTOM)
                }
            }.view as ViewGroup
}