package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.R
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.kekstudio.dachshundtablayout.DachshundTabLayout
import com.ljmu.andre.snaptools.ModulePack.UIComponents.CenteredArrayAdapter
import com.ljmu.andre.snaptools.ModulePack.UIComponents.ColourPicker.HSLColorPicker
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.assignItemChangedProvider
import com.ljmu.andre.snaptools.ModulePack.Utils.WrapContentViewPager
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import java.util.*

@Suppress("DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class CustomViews {

    companion object {

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewGroup.splitter(colorId: String = "primaryLight") = splitter(colorId) { }
        fun ViewGroup.splitter(colorId: String = "primaryLight",
                               init: (@AnkoViewDslMarker View).() -> Unit) =
                view {
                    init()

                    backgroundResource = getColor(context, colorId)
                }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewGroup.header(title: String, gravity: Int = Gravity.START, underColour: String = "primaryLight") =
                verticalLayout {
                    lparams(width = matchParent, height = wrapContent)

                    textView(title) {
                        setTextAppearance(context, getStyle(context, "HeaderText"))
                        this.gravity = gravity
                    }.lparams {
                        horizontalMargin = 5.toDp()
                        this.gravity = gravity
                    }

                    view {
                        backgroundResource = getColor(context, underColour)
                    }.lparams(width = matchParent, height = 1.toDp())
                }

        fun ViewGroup.headerNoUnderline(title: String, mGravity: Int = Gravity.START) {
            frameLayout {
                textView(title) {
                    setTextAppearance(context, getStyle(context, "HeaderText"))
                    gravity = mGravity
                }.lparams {
                    horizontalMargin = 5.toDp()
                    gravity = mGravity
                }
            }
        }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun <T> ViewGroup.labelledSpinner(label: String, initialItem: String, items: ArrayList<String>,
                                          itemSelectedListener: ViewFactory.OnItemChangedProvider<T>, id: String? = null,
                                          adapter: BaseAdapter? = null) =
                linearLayout {
                    var mAdapter = adapter
                    if (mAdapter == null) {
                        mAdapter = CenteredArrayAdapter<String>(context,
                                R.layout.simple_spinner_dropdown_item, items)
                    }

                    val currentIndex = Math.max(0, items.indexOf(initialItem))

                    label(label).lparams(width = matchParent, weight = 1f) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    themedSpinner {
                        this.adapter = mAdapter
                        setSelection(currentIndex, false)

                        assignItemChangedProvider(
                                this,
                                itemSelectedListener
                        )

                        if (id != null)
                            this.id = getIdFromString(id)
                    }.lparams(width = matchParent, weight = 1f) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewGroup.labelledSeekBar(id: String? = null, text: String, progress: Int = 0, min: Int = 0, max: Int = 100,
                                      resultListener: ViewFactory.OnSeekBarResult? = null,
                                      progressListener: ViewFactory.OnSeekBarProgress? = null) =
                labelledSeekBar(id, text, progress, min, max, resultListener, progressListener) {}

        fun ViewGroup.labelledSeekBar(id: String? = null, text: String, progress: Int = 0, min: Int = 0, max: Int = 100,
                                      resultListener: ViewFactory.OnSeekBarResult? = null,
                                      progressListener: ViewFactory.OnSeekBarProgress? = null,
                                      init: (@AnkoViewDslMarker ViewGroup).() -> Unit) =
                verticalLayout {
                    val bindProgress: Boolean = text.contains("%s")

                    var initialLabel = text
                    if (bindProgress)
                        initialLabel = String.format(text, progress)

                    val label = label(initialLabel).lparams(width = matchParent, weight = 1f)

                    themedSeekBar {
                        verticalPadding = 5.toDp()

                        if (id != null)
                            this.id = getIdFromString(id)

                        this.max = max - min
                        this.progress = progress - min

                        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                                var newProgress = progress
                                newProgress += min

                                if (bindProgress) {
                                    label.text = String.format(text, newProgress)
                                }

                                progressListener?.onSeekProgress(seekBar, newProgress)
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar) {
                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar) {
                                resultListener?.onSeekResult(seekBar, seekBar.progress + min)
                            }
                        })
                    }.lparams(width = matchParent, weight = 1f)

                    init()
                }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewGroup.label(title: String) = label(title) {}
        fun ViewGroup.label(title: String, init: (@AnkoViewDslMarker TextView).() -> Unit) =
                textView(title) {
                    @Suppress("DEPRECATION")
                    setTextAppearance(context, getStyle(context, "DefaultText"))
                    textSize = 16f
                    leftPadding = 10.toDp()
                    gravity = Gravity.CENTER_VERTICAL

                    init()
                }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewManager.hslColorPicker(): HSLColorPicker = hslColorPicker {}
        fun ViewManager.hslColorPicker(init: (@AnkoViewDslMarker HSLColorPicker).() -> Unit): HSLColorPicker =
                ankoView({ ctx: Context -> HSLColorPicker(ctx) }, theme = 0) { init() }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewManager.wrapContentViewPager(): WrapContentViewPager = wrapContentViewPager {}
        fun ViewManager.wrapContentViewPager(init: (@AnkoViewDslMarker WrapContentViewPager).() -> Unit): WrapContentViewPager =
                ankoView({ ctx: Context -> WrapContentViewPager(ctx) }, theme = 0) { init() }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================

        fun ViewManager.customTabStrip(): DachshundTabLayout = customTabStrip {}
        fun ViewManager.customTabStrip(init: (@AnkoViewDslMarker DachshundTabLayout).() -> Unit): DachshundTabLayout =
                ankoView({ ctx: Context ->
                    val savingModeTabLayout = DachshundTabLayout(ctx)
                    savingModeTabLayout.layoutParams =
                            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 40.toDp())

                    savingModeTabLayout.setBackgroundResource(
                            getDrawable(ctx, "tab_border")
                    )
                    savingModeTabLayout.setSelectedTabIndicatorColor(
                            ContextCompat.getColor(
                                    ctx,
                                    getColor(ctx, "primaryLight")
                            )
                    )
                    savingModeTabLayout.setTabTextColors(
                            ContextCompat.getColor(
                                    ctx,
                                    getColor(ctx, "textTertiary")
                            ),
                            ContextCompat.getColor(
                                    ctx,
                                    getColor(ctx, "primaryLight")
                            )
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        savingModeTabLayout.elevation = 4f

                    savingModeTabLayout
                }, theme = 0) {
                    init()
                }

        // ===========================================================================
        // ===========================================================================
        // ===========================================================================


    }
}