package com.ljmu.andre.snaptools.Dialogs.Content

import android.app.Activity
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import com.ljmu.andre.snaptools.Dialogs.DialogComponent
import com.ljmu.andre.snaptools.Dialogs.ModularDialog
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString
import org.jetbrains.anko.*

@Suppress("DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class ModularTextView(private var message: String) : DialogComponent {
    override fun bind(activity: Activity, dialog: ModularDialog): View =
            activity.UI {
                verticalLayout {
                    lparams(width = matchParent, height = wrapContent)

                    textView {
                        horizontalPadding = dip(10)
                        verticalPadding = dip(25)

                        id = getIdFromString("ModularTextView")

                        text = getSpannedHtml(message)

                        textSize = 18f

                        if (message.startsWith("<center>"))
                            gravity = Gravity.CENTER_HORIZONTAL

                        try {
                            movementMethod = LinkMovementMethod.getInstance()
                        } catch (ignored: Throwable) {
                        }
                    }.lparams(matchParent)
                }
            }.view
}