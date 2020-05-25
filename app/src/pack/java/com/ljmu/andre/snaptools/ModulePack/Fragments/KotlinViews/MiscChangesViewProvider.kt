package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.jaqxues.akrolyb.prefs.getPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.label
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.Fragments.MiscChangesFragment
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ListedViewPageAdapter
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.COPY_BUTTON
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CURRENT_FONT
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CUT_BUTTON
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_ANIMATED_CONTENT_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CAMERA2_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CAPTIONV2_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_CHAT_VIDEO_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_EMOJIBRUSH_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_FPS_OVERLAY_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_GIPHY_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_HANDSFREEREC_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_INSIGHTS_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_MULTILINE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FORCE_SKYFILTERS_STATE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.PASTE_BUTTON
import com.ljmu.andre.snaptools.ModulePack.Utils.Result
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.Utils.Callable
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor
import com.ljmu.andre.snaptools.Utils.themedSwitchCompatX
import com.ljmu.andre.snaptools.Utils.viewPagerX
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
@Suppress("DEPRECATION", "UNCHECKED_CAST")
class MiscChangesViewProvider(
        var activity: Activity,
        private var generalUICallable: Callable<ViewGroup>,
        private var experiemtnsUICallable: Callable<ViewGroup>,
        private var eventCallable: Callable<Result<MiscChangesFragment.MiscChangesEvent, Any>>
) {

    val fontList: List<String> = ArrayList()
    private val onOffDefaultList = arrayListOf(
            "Default", "On", "Off"
    )
    private val fontSpinnerAdapter = FontSpinnerAdapter(fontList)

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(): T =
            activity.UI {
                verticalLayout {
                    val tabStrip = customTabStrip {
                        id = ResourceUtils.getIdFromString("tab_strip")
                    }

                    verticalLayout {
                        horizontalPadding = 16.toDp()
                        val viewPager: ViewPager = viewPagerX {
                            id = ResourceUtils.getIdFromString("view_pager")
                        }.lparams(width = matchParent, height = wrapContent)

                        setupPages(viewPager)

                        tabStrip.setupWithViewPager(viewPager)
                    }
                }
            }.view as T


    private fun setupPages(viewPager: ViewPager) {
        val viewList = ArrayList<Pair<String, View>>()

        viewList.add(Pair.create("General", initGeneralPage()))
        viewList.add(Pair.create("Experiments", initExperimentsPage()))

        viewPager.adapter = ListedViewPageAdapter(
                viewList
        )
    }

    // ===========================================================================
    // ===========================================================================
    // ===========================================================================

    /**
     * ===========================================================================
     * Setup General Page
     * ===========================================================================
     */
    @SuppressLint("ResourceType")
    fun <T : ViewGroup> initGeneralPage(): T =
            activity.UI {
                scrollView {
                    lparams(matchParent, matchParent)
                    id = "general_scrollview".toId()

                    verticalLayout {
                        header("Caption Settings")

                        linearLayout {
                            label("Snapchat Font: ").lparams(width = matchParent, height = wrapContent) {
                                weight = 1f
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            themedSpinner {
                                id = "font_selector_spinner".toId()
                                adapter = fontSpinnerAdapter

                                ViewFactory.assignItemChangedProvider(
                                        this,
                                        ViewFactory.OnItemChangedProvider<String>(
                                                { newItem, _, _ ->
                                                    eventCallable.call(
                                                            Result(MiscChangesFragment.MiscChangesEvent.FONT_SELECTED, newItem)
                                                    )

                                                    fontSpinnerAdapter.currentFont = newItem
                                                    fontSpinnerAdapter.notifyDataSetChanged()
                                                },
                                                { CURRENT_FONT.getPref() }
                                        )
                                )
                            }.lparams(matchParent) {
                                gravity = Gravity.CENTER_VERTICAL
                                weight = 1f
                            }
                        }

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            text = "Force Caption Multi-Line"
                            verticalPadding = dip(10)
                            id = ResourceUtils.getIdFromString("switch_misc_force_multiline")
                            isChecked = FORCE_MULTILINE.getPref()
                            setOnCheckedChangeListener { _, isChecked -> putAndKill(FORCE_MULTILINE, isChecked, activity) }
                        }.lparams(matchParent)

                        header("Caption Menu Settings")

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            text = "Cut Button"
                            verticalPadding = dip(10)
                            id = ResourceUtils.getIdFromString("switch_misc_context_cut")
                            isChecked = CUT_BUTTON.getPref()
                            setOnCheckedChangeListener { _, isChecked -> putAndKill(CUT_BUTTON, isChecked, activity) }
                        }.lparams(matchParent)

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            text = "Copy Option"
                            verticalPadding = dip(10)
                            id = ResourceUtils.getIdFromString("switch_misc_context_copy")
                            isChecked = COPY_BUTTON.getPref()
                            setOnCheckedChangeListener { _, isChecked -> putAndKill(COPY_BUTTON, isChecked, activity) }
                        }.lparams(matchParent)

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            text = "Paste Button"
                            verticalPadding = dip(10)
                            id = ResourceUtils.getIdFromString("switch_misc_context_paste")
                            isChecked = PASTE_BUTTON.getPref()
                            setOnCheckedChangeListener { _, isChecked -> putAndKill(PASTE_BUTTON, isChecked, activity) }
                        }.lparams(matchParent)

                        generalUICallable.call(this)
                    }.lparams(matchParent, wrapContent) {
                        margin = 16.toDp()
                    }
                }
            }.view as T


    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    /**
     * ===========================================================================
     * Setup Experiments Page
     * ===========================================================================
     */

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> initExperimentsPage(): T =
            activity.UI {
                scrollView {
                    lparams(matchParent, matchParent)
                    id = "experiments_scrollview".toId()

                    verticalLayout {

                        textView("Experimental features/settings provided by snapchat") {
                            textColor = ContextCompat.getColor(activity, getColor(activity, "errorLight"))
                            gravity = Gravity.CENTER
                        }.lparams(matchParent)

//                        labelledSpinner(
//                                label = "Cheetah Chat UI: ",
//                                initialItem = getPref<String>(FORCE_CHEETAH_CHAT_STATE),
//                                items = onOffDefaultList,
//                                id = "spinner_cheetah",
//                                itemSelectedListener = ViewFactory.OnItemChangedProvider<String>(
//                                        { newItem, _, _ ->
//                                            putAndKill(FORCE_CHEETAH_CHAT_STATE, newItem, activity)
//                                        },
//                                        { getPref<String>(FORCE_CHEETAH_CHAT_STATE) }
//                                ))

                        labelledSpinner(
                                label = "Insights: ",
                                initialItem = FORCE_INSIGHTS_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_insights",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_INSIGHTS_STATE, newItem, activity)
                                        },
                                        { FORCE_INSIGHTS_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Video Chat: ",
                                initialItem = FORCE_CHAT_VIDEO_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_videochat",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_CHAT_VIDEO_STATE, newItem, activity)
                                        },
                                        { FORCE_CHAT_VIDEO_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Animated Content: ",
                                initialItem = FORCE_ANIMATED_CONTENT_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_animatedcontent",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_ANIMATED_CONTENT_STATE, newItem, activity)
                                        },
                                        { FORCE_ANIMATED_CONTENT_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Giphy Stickers: ",
                                initialItem = FORCE_GIPHY_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_giphy",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_GIPHY_STATE, newItem, activity)
                                        },
                                        { FORCE_GIPHY_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Captions V2: ",
                                initialItem = FORCE_CAPTIONV2_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_captionsv2",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_CAPTIONV2_STATE, newItem, activity)
                                        },
                                        { FORCE_CAPTIONV2_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Camera2: ",
                                initialItem = FORCE_CAMERA2_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_camera2",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_CAMERA2_STATE, newItem, activity)
                                        },
                                        { FORCE_CAMERA2_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Hands Free Recording: ",
                                initialItem = FORCE_HANDSFREEREC_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_handsfree",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_HANDSFREEREC_STATE, newItem, activity)
                                        },
                                        { FORCE_HANDSFREEREC_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "FPS Overlay: ",
                                initialItem = FORCE_FPS_OVERLAY_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_fpsoverlay",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_FPS_OVERLAY_STATE, newItem, activity)
                                        },
                                        { FORCE_FPS_OVERLAY_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Skyfilters: ",
                                initialItem = FORCE_SKYFILTERS_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_skyfilters",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_SKYFILTERS_STATE, newItem, activity)
                                        },
                                        { FORCE_SKYFILTERS_STATE.getPref() }
                                ))

                        labelledSpinner(
                                label = "Emoji Brush: ",
                                initialItem = FORCE_EMOJIBRUSH_STATE.getPref(),
                                items = onOffDefaultList,
                                id = "spinner_emojibrush",
                                itemSelectedListener = ViewFactory.OnItemChangedProvider(
                                        { newItem, _, _ ->
                                            putAndKill(FORCE_EMOJIBRUSH_STATE, newItem, activity)
                                        },
                                        { FORCE_EMOJIBRUSH_STATE.getPref() }
                                ))

                        experiemtnsUICallable.call(this)
                    }.lparams(matchParent)
                }
            }.view as T

    fun refreshFontAdapter() {
        fontSpinnerAdapter.notifyDataSetChanged()
    }

    class FontSpinnerAdapter(val list: List<String>) : BaseAdapter() {
        private val typefaceCache = HashMap<String, Typeface>()
        var currentFont: String = CURRENT_FONT.getPref()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return with(parent!!.context) {
                linearLayout {
                    lparams(matchParent)
                    padding = 10.toDp()

                    val fontFilename = getItem(position)
                    backgroundColor = Color.TRANSPARENT

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