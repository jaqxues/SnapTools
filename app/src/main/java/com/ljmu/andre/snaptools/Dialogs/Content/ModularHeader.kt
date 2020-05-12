package com.ljmu.andre.snaptools.Dialogs.Content

import android.app.Activity
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.View
import com.ljmu.andre.snaptools.Dialogs.DialogComponent
import com.ljmu.andre.snaptools.Dialogs.ModularDialog
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class ModularHeader(private val headerText: String) : DialogComponent {

    override fun bind(activity: Activity, dialog: ModularDialog): View =
            activity.UI {
                relativeLayout {
                    backgroundResource = R.color.backgroundTertiary

                    textView(headerText) {
                        textColor = ContextCompat.getColor(getModuleContext(activity), R.color.primary)
                        padding = dip(5)
                        backgroundResource = R.drawable.border
                        textSize = 24f
                        gravity = Gravity.CENTER
                    }.lparams(width = matchParent, height = wrapContent)
                }
            }.view
}