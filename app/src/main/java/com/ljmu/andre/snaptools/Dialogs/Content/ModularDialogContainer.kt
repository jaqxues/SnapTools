package com.ljmu.andre.snaptools.Dialogs.Content

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.ljmu.andre.snaptools.Dialogs.DialogComponent
import com.ljmu.andre.snaptools.Dialogs.ModularDialog
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class ModularDialogContainer : DialogComponent {
    override fun bind(activity: Activity, dialog: ModularDialog): View =
            activity.UI {
                relativeLayout {
                    lparams(width = wrapContent, height = wrapContent)
                    backgroundResource = R.color.backgroundPrimary

                    val container: ViewGroup = verticalLayout {
                        id = getIdFromString("ModularDialogContainer")
                    }.lparams(width = matchParent, height = wrapContent)

                    frameLayout {
                        backgroundResource = R.drawable.border
                    }.lparams(width = matchParent) {
                        sameTop(container)
                        sameBottom(container)
                    }
                }
            }.view
}