package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.jaqxues.akrolyb.prefs.getPref
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Fragments.MiscChangesFragment
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CURRENT_FONT
import com.ljmu.andre.snaptools.Utils.Callable
import com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import org.jetbrains.anko.*

@Suppress("UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class FontPickerDialogExtension(val activity: Activity, fontList: List<String>, val fontListener: Callable<String>) : ThemedDialog.ThemedDialogExtension {
    private val fontAdapter = FontListAdapter(fontList)
    private val modContext = getModuleContext(activity)!!

    override fun onCreate(inflater: LayoutInflater?, parent: View?, content: ViewGroup?, themedDialog: ThemedDialog?) {
        content?.addView(
                getPickerView(content.context, themedDialog)
        )
    }

    private fun <T : ViewGroup> getPickerView(context: Context, themedDialog: ThemedDialog?): T =
            context.UI {
                verticalLayout {
                    lparams(matchParent, wrapContent)

                    listView {
                        adapter = fontAdapter

                        setOnItemClickListener { _, _, position, id ->
                            val selectedFont = fontAdapter.getItem(position)
                            fontListener.call(selectedFont)
                            fontAdapter.currentFont = selectedFont
                            fontAdapter.notifyDataSetChanged()
                        }
                    }.lparams(matchParent, wrapContent, 1f)

                    themedButton(ResourceUtils.getStyle(modContext, "NeutralButton")) {
                        text = "Done"

                        setOnClickListener {
                            themedDialog?.dismiss()
                        }
                    }.lparams(width = matchParent)
                }
            }.view as T

    class FontListAdapter(val list: List<String>) : BaseAdapter() {
        private val typefaceCache = HashMap<String, Typeface>()
        var currentFont: String = CURRENT_FONT.getPref()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return with(parent!!.context) {
                linearLayout {
                    lparams(matchParent)
                    padding = 10.toDp()

                    val fontFilename = getItem(position)

                    backgroundColor = if (fontFilename == currentFont)
                        Color.parseColor("#22FFFFFF")
                    else
                        Color.TRANSPARENT

                    textView(fontFilename) {
                        var selectedTypeface = typefaceCache[fontFilename]

                        if (selectedTypeface == null) {
                            selectedTypeface = MiscChangesFragment.getTypefaceSafe(fontFilename)
                            typefaceCache.put(fontFilename, selectedTypeface)
                        }

                        typeface = selectedTypeface
                        gravity = Gravity.CENTER
                    }.lparams(matchParent)
                }
            }
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val fontFilename = getItem(position)

            val view = getView(position, convertView, parent)
            if (fontFilename == currentFont)
                view.setBackgroundColor(Color.parseColor("#22FFFFFF"))

            return view
        }

        override fun getItem(position: Int): String = list[position]

        override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

        override fun getCount(): Int = list.size
    }
}