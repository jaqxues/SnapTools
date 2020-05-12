package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.hslColorPicker
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.Listeners.SimpleColorSelectionListener
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.Utils.Callable
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor
import org.jetbrains.anko.*

@Suppress("UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class ColorPickerDialogExtension(activity: Activity, initColorRes: String, private val colorCallable: Callable<Int>) : ThemedDialog.ThemedDialogExtension {
    private val modContext = getModuleContext(activity)!!
    private var currentColor = ContextCompat.getColor(modContext, getColor(modContext, initColorRes))

    override fun onCreate(inflater: LayoutInflater?, parent: View?, content: ViewGroup?, themedDialog: ThemedDialog?) {
        content?.addView(
                getColorPickerView(content.context, themedDialog!!)
        )
    }

    private fun <T : ViewGroup> getColorPickerView(context: Context, themedDialog: ThemedDialog): T =
            context.UI {
                verticalLayout {
                    lparams(matchParent, matchParent)

                    hslColorPicker {
                        setColor(currentColor)

                        setColorSelectionListener(object : SimpleColorSelectionListener() {
                            override fun onColorSelected(color: Int) {
                                currentColor = color
                            }
                        })
                    }.lparams(matchParent, wrapContent, 1f)

                    linearLayout {
                        themedButton(ResourceUtils.getStyle(modContext, "NeutralButton")) {
                            id = "btn_cancel".toId()
                            text = "Cancel"

                            setOnClickListener {
                                themedDialog.dismiss()
                            }
                        }.lparams(width = matchParent, weight = 1f)

                        themedButton(ResourceUtils.getStyle(modContext, "NeutralButton")) {
                            id = "btn_done".toId()
                            text = "Done"

                            setOnClickListener {
                                themedDialog.dismiss()
                                colorCallable.call(currentColor)
                            }
                        }.lparams(width = matchParent, weight = 1f)
                    }.lparams(matchParent)
                }
            }.view as T
}