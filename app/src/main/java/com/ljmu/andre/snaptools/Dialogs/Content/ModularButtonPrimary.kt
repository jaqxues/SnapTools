package com.ljmu.andre.snaptools.Dialogs.Content

import android.app.Activity
import androidx.core.content.ContextCompat
import android.view.View
import com.ljmu.andre.snaptools.Dialogs.DialogComponent
import com.ljmu.andre.snaptools.Dialogs.ModularDialog
import com.ljmu.andre.snaptools.Utils.ContextHelper
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable
import org.jetbrains.anko.UI
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.button
import org.jetbrains.anko.textColor

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class ModularButtonPrimary(private var text: String, private var clickListener: ModularButtonClickListener) : DialogComponent {
    var drawable: String? = "flat_button_primary"
    var btnTextColor: String? = "primaryLight"

    public fun setButtonDrawable(drawable: String): ModularButtonPrimary {
        this.drawable = drawable
        return this
    }

    public fun setButtonTextColor(textColor: String): ModularButtonPrimary {
        btnTextColor = textColor
        return this
    }

    override fun bind(activity: Activity, dialog: ModularDialog): View =
            activity.UI {
                button(text) {
                    val context = ContextHelper.getModuleContext(activity)
                    backgroundResource = getDrawable(context, drawable)
                    textColor = ContextCompat.getColor(context, getColor(context, btnTextColor))


                    setOnClickListener {
                        clickListener.click(dialog);
                    }
                }
            }.view

    interface ModularButtonClickListener {
        fun click(dialog: ModularDialog)
    }
}