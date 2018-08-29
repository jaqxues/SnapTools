package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class PickerViewProvider() {

    @SuppressLint("ResourceType")
    // Use Module Context
    fun <T : ViewGroup> getMainContainer(context: Context): T =
            getModuleContext(context).UI {
                val modContext = getModuleContext(context)

                verticalLayout {
                    lparams(matchParent)

                    frameLayout {
                        id = "color_picker_container".toId()
                    }.lparams(300.toDp(), 300.toDp()) {
                        gravity = Gravity.CENTER
                    }

                    linearLayout {

                        themedButton(ResourceUtils.getStyle(modContext, "NeutralButton")) {
                            text = "Cancel"
                            id = "button_cancel".toId()
                        }.lparams(width = matchParent, height = wrapContent) {
                            weight = 1f
                        }

                        themedButton(ResourceUtils.getStyle(modContext, "NeutralButton")) {
                            text = "Okay"
                            id = "button_okay".toId()
                        }.lparams(width = matchParent, height = wrapContent) {
                            weight = 1f
                        }
                    }.lparams(matchParent) {
                        topMargin = 10.toDp()
                    }
                }
            }.view as T
}