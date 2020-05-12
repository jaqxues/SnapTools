package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import com.ljmu.andre.GsonPreferences.Preferences
import com.ljmu.andre.GsonPreferences.Preferences.*
import com.ljmu.andre.snaptools.Dialogs.Content.TextInput
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.headerNoUnderline
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.label
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.splitter
import com.ljmu.andre.snaptools.ModulePack.Fragments.LensSettingsFragment
import com.ljmu.andre.snaptools.ModulePack.Fragments.LensSettingsFragment.LensUIEvent
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.LensProfileUtils
import com.ljmu.andre.snaptools.ModulePack.Utils.ListedViewPageAdapter
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.Utils.*
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.KILL_SC_ON_CHANGE
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LENS_SELECTOR_SPAN
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedSwitchCompat
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.jetbrains.anko.support.v4.viewPager
import timber.log.Timber

@Suppress("DEPRECATION", "UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class LensViewProvider(
        val activity: Activity, private val lensUIEventCallable: Callable<LensSettingsFragment.LensUIEvent>,
        private val setupSettingsCallable: Callable<View>, private val setupLensesCallable: Callable<View>) {

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(): T =
            activity.UI {
                verticalLayout {
                    val tabStrip = customTabStrip {
                        id = ResourceUtils.getIdFromString("tab_strip")
                    }

                    verticalLayout {
                        horizontalPadding = 16.toDp()
                        val viewPager: ViewPager = viewPager {
                            id = ResourceUtils.getIdFromString("view_pager")
                        }.lparams(width = matchParent, height = wrapContent)

                        setupPages(viewPager)

                        tabStrip.setupWithViewPager(viewPager)
                    }
                }
            }.view as T

    private fun setupPages(viewPager: ViewPager) {
        val viewList = ArrayList<Pair<String, View>>()

        viewList.add(Pair.create("Lenses", initLensesView()))
        viewList.add(Pair.create("Settings", initSettingsView()))

        viewPager.adapter = ListedViewPageAdapter(
                viewList
        )
    }

    /**
     * ===========================================================================
     * Lens Selector
     * ===========================================================================
     */
    private fun <T : ViewGroup> initLensesView(): T =
            activity.UI {
                verticalLayout {
                    lparams(matchParent, matchParent)
                    topPadding = 16.toDp()

                    val layoutInflater = LayoutInflater.from(activity)

                    swipeRefreshLayout {
                        id = "swipe_refresh_lenses".toId()

                        val recyclerView = layoutInflater.inflate(
                                getLayout(activity, "recyclerview"), null, false
                        )
                        recyclerView.id = "recycler_lenses".toId()
                        recyclerView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        addView(recyclerView)
                    }.lparams(matchParent, matchParent, 1f)

                    relativeLayout {
                        id = getIdFromString("empty_lenses_container")
                        visibility = View.GONE

                        verticalLayout {
                            imageView {
                                setImageResource(getDrawable(activity, "snaptools_logo"))
                                setColorFilter(
                                        ContextCompat.getColor(activity, getColor(activity, "backgroundSecondary")),
                                        PorterDuff.Mode.MULTIPLY
                                )
                            }.lparams(width = wrapContent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }

                            textView("No Lenses Found") {
                                textColor = ContextCompat.getColor(
                                        activity,
                                        getColor(activity, "error")
                                )
                            }.lparams(width = wrapContent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }

                            textView("Lenses collected within Snapchat will appear here.") {
                                textColor = ContextCompat.getColor(
                                        activity,
                                        getColor(activity, "textTertiary")
                                )
                                gravity = Gravity.CENTER
                            }.lparams(width = wrapContent, height = wrapContent) {
                                gravity = Gravity.CENTER
                            }
                        }.lparams(wrapContent, wrapContent) {
                            centerInParent()
                        }
                    }.lparams(width = matchParent, height = matchParent) {
                        weight = 1f
                    }

                    verticalLayout {
                        /**
                         * ===========================================================================
                         * Toolbar Content
                         * ===========================================================================
                         */
                        verticalLayout {
                            id = "toolbar_content".toId()
                            visibility = View.GONE

                            splitter(colorId = "primary").lparams(width = matchParent, height = 1.toDp())

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show Lens Names"
                                isChecked = Preferences.getPref(SHOW_LENS_NAMES)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putPref(SHOW_LENS_NAMES, isChecked)
                                    lensUIEventCallable.call(LensUIEvent.SHOW_LENS_NAMES)
                                }
                            }

                            val seekbar: SeekBar

                            if (Constants.getApkVersionCode() >= 66) {
                                seekbar = layoutInflater.inflate(getLayout(activity, "discrete_seekbar"), null, false) as SeekBar
                                addView(seekbar)
                            } else {
                                textView("APK update needed for seekbar theme") {
                                    textColor = ContextCompat.getColor(context, getColor(context, "errorLight"))
                                    textSize = 10f
                                }.lparams { gravity = Gravity.CENTER }

                                seekbar = themedSeekBar()
                            }

                            seekbar.id = "seek_lens_span".toId()
                            seekbar.max = 9
                            seekbar.progress = getPref<Int>(LENS_SELECTOR_SPAN) - 1
                            seekbar.verticalPadding = 10.toDp()
                            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    putPref(LENS_SELECTOR_SPAN, progress + 1)
                                    lensUIEventCallable.call(LensUIEvent.UPDATE_LENS_SPAN)
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                }

                                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                }
                            })

                            linearLayout {
                                label("Name Filter: ").lparams {
                                    gravity = Gravity.CENTER_VERTICAL
                                }

                                themedEditText {
                                    id = "txt_lens_filter".toId()
                                    setTextAppearance(context, getStyle(context, "DefaultText"))
                                    setSingleLine()
                                    hint = "Lens Name"
                                    horizontalPadding = 10.toDp()
                                }.lparams(width = matchParent, weight = 1f) {
                                    gravity = Gravity.CENTER_VERTICAL
                                }
                            }.lparams(width = matchParent)

                        }.lparams(width = matchParent) {
                            bottomMargin = 5.toDp()
                        }
                        // ===========================================================================
                        // ===========================================================================

                        /**
                         * ===========================================================================
                         * Toolbar Panel
                         * ===========================================================================
                         */

                        splitter("primary").lparams(matchParent, 1.toDp())

                        linearLayout {
                            backgroundResource = getSelectableBackgroundId(context)
                            setOnClickListener {
                                val toolbarContent = activity.find<ViewGroup>("toolbar_content".toId())
                                val dropdownArrow = activity.find<View>("img_toolbar_arrow".toId())

                                if (toolbarContent.visibility == View.VISIBLE) {
                                    AnimationUtils.collapse(toolbarContent)
                                    AnimationUtils.rotate(dropdownArrow, true)
                                } else {
                                    AnimationUtils.expand(toolbarContent)
                                    AnimationUtils.rotate(dropdownArrow, false)
                                }
                            }

                            imageView {
                                id = "img_toolbar_arrow".toId()
                                imageResource = getDrawable(context, "dropdown_arrow")
                                padding = 13.toDp()
                                scaleType = ImageView.ScaleType.FIT_CENTER
                            }.lparams(40.toDp(), matchParent) {
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            textView("Toolbar") {
                                textColor = ContextCompat.getColor(context, getColor(context, "primaryLight"))
                                horizontalPadding = 10.toDp()
                                textSize = 14f
                                setTypeface(null, Typeface.BOLD)
                            }.lparams(width = matchParent, weight = 1f) {
                                gravity = Gravity.CENTER_VERTICAL
                            }
                        }.lparams(width = matchParent, height = 50.toDp())

                    }.lparams(width = matchParent) {
                        gravity = Gravity.BOTTOM
                    }

                    setupLensesCallable.call(this)
                }
            }.view as T


    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    /**
     * ===========================================================================
     * Settings View
     * ===========================================================================
     */
    private fun <T : ViewGroup> initSettingsView(): T =
            activity.UI {
                scrollView {
                    verticalPadding = 16.toDp()

                    verticalLayout {
                        // ===========================================================================

                        headerNoUnderline("Lens Statistics", Gravity.CENTER)
                        lensStatistics()

                        linearLayout {
                            themedButton(getStyle(activity, "NeutralButton")) {
                                text = "Activate All"
                                id = "button_lens_select_all".toId()

                                setOnClickListener {
                                    lensUIEventCallable.call(LensUIEvent.ENABLE_ALL)
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                weight = 1f
                            }

                            themedButton(getStyle(activity, "NeutralButton")) {
                                text = "Deactivate All"
                                id = "button_lens_deselect_all".toId()

                                setOnClickListener {
                                    lensUIEventCallable.call(LensUIEvent.DISABLE_ALL)
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                weight = 1f
                            }
                        }.lparams(width = matchParent, height = wrapContent) {
                            bottomMargin = 20.toDp()
                        }

                        // ===========================================================================

                        themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            padding = 10.toDp()
                            text = "Enable lens when collected"
                            isChecked = Preferences.getPref(LENS_AUTO_ENABLE)

                            setOnCheckedChangeListener { _, isChecked ->
                                putAndKill(LENS_AUTO_ENABLE, isChecked, activity)
                            }
                        }

                        // ===========================================================================

                        themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            padding = 10.toDp()
                            text = "Enable new merged lenses"
                            isChecked = Preferences.getPref(LENS_MERGE_ENABLE)

                            setOnCheckedChangeListener { _, isChecked ->
                                putAndKill(LENS_MERGE_ENABLE, isChecked, activity)
                            }
                        }

                        // ===========================================================================

                        themedButton(ResourceUtils.getStyle(context, "NeutralButton")) {
                            id = "button_merge_lens_dbs".toId()
                            text = "Merge Lens Databases"

                            setOnClickListener {
                                lensUIEventCallable.call(LensUIEvent.MERGE)
                            }
                        }.lparams(width = matchParent) {
                            bottomMargin = 10.toDp()
                        }

                        // ===========================================================================

                        header("Lens Profile Management")

                        /**
                         * ===========================================================================
                         * Restore Lens Profile Spinner
                         * ===========================================================================
                         */
                        val restoreValues = java.util.ArrayList<String>()
                        restoreValues.add("Current")

                        val profilesPath = getCreateDir(BACKUPS_PATH)
                        val profilesExists: Boolean = profilesPath != null && profilesPath.exists()

                        /**
                         * ===========================================================================
                         * Load the restore point filenames
                         * ===========================================================================
                         */
                        if (!profilesExists) {
                            Timber.w("Lens profiles directory couldn't be created")

                            textView("Couldn't find or create Lens Profiles Directory") {
                                textColor = ContextCompat.getColor(context, getColor(context, "errorLight"))
                                gravity = Gravity.CENTER
                            }.lparams(width = matchParent, height = wrapContent)
                        } else {
                            // ===========================================================================
                            profilesPath!!.listFiles({ _, s -> s.startsWith("LensProfile_") })
                                    ?.mapTo(restoreValues) {
                                        it.name.replace("LensProfile_", "")
                                                .replace(".json", "")
                                    }
                            // ===========================================================================
                        }

                        val deletableAdapter = DeletableAdapter(restoreValues, Callable {
                            val profile = restoreValues[it]

                            DialogFactory.createConfirmation(
                                    activity,
                                    "Delete Lens Profile",
                                    "Are you sure you wish to delete the Lens Profile " + htmlHighlight(profile) + "?",
                                    object : ThemedDialog.ThemedClickListener() {
                                        override fun clicked(themedDialog: ThemedDialog?) {
                                            LensProfileUtils.deleteProfile(restoreValues[it])

                                            if (it > 0 && it < restoreValues.size)
                                                restoreValues.removeAt(it)

                                            themedDialog!!.dismiss()
                                        }
                                    }
                            ).show()
                        })

                        labelledSpinner("Lens Profile: ", "Current", restoreValues,
                                ViewFactory.OnItemChangedProvider<String>(
                                        { newItem, _, _ ->
                                            DialogFactory.createConfirmation(
                                                    activity,
                                                    "Confirm Restore",
                                                    "Are you sure you wish to restore the lens profile: " + htmlHighlight(newItem),
                                                    object : ThemedDialog.ThemedClickListener() {
                                                        override fun clicked(themedDialog: ThemedDialog?) {
                                                            val restoreResult = LensProfileUtils.restoreProfile(newItem)

                                                            activity.findOptional<Spinner>("spinner_lens_profiles".toId())
                                                                    ?.setSelection(0, false)

                                                            SafeToast.show(
                                                                    activity,
                                                                    restoreResult.value,
                                                                    !restoreResult.key
                                                            )

                                                            if (!restoreResult.key)
                                                                return

                                                            if (getPref(KILL_SC_ON_CHANGE))
                                                                PackUtils.killSCService(activity)

                                                            lensUIEventCallable.call(LensUIEvent.RELOAD_LENSES)
                                                            //refreshLensStatistics()

                                                            themedDialog!!.dismiss()
                                                        }
                                                    },
                                                    object : ThemedDialog.ThemedClickListener() {
                                                        override fun clicked(themedDialog: ThemedDialog?) {
                                                            activity.findOptional<Spinner>("spinner_lens_profiles".toId())
                                                                    ?.setSelection(0, false)
                                                            themedDialog!!.dismiss()
                                                        }

                                                    }
                                            ).show()
                                        },
                                        { "Current" }
                                ), id = "spinner_lens_profiles", adapter = deletableAdapter).isEnabled = profilesExists
                        // ===========================================================================

                        /**
                         * ===========================================================================
                         * Backup Lens Profile Button
                         * ===========================================================================
                         */
                        themedButton(ResourceUtils.getStyle(context, "NeutralButton")) {
                            id = "button_create_lens_profile".toId()
                            text = "Create Lens Profile"

                            setOnClickListener {
                                DialogFactory.createTextInputDialog(
                                        activity,
                                        "Create Lens Profile",
                                        "Create a new lens profile? "
                                                + "\n" + htmlHighlight("Filename:"),
                                        "Filename",
                                        LensProfileUtils.getFreshFilenameNoExt(),
                                        null,
                                        object : ThemedDialog.ThemedClickListener() {
                                            override fun clicked(themedDialog: ThemedDialog?) {
                                                themedDialog!!.dismiss()

                                                val textInput = themedDialog.getExtension<TextInput>()
                                                val success = LensProfileUtils.backupCurrentProfile(textInput.inputMessage)

                                                if (!success) {
                                                    SafeToast.show(
                                                            activity,
                                                            "Failed to backup lens profile",
                                                            true
                                                    )
                                                } else {
                                                    restoreValues.add(textInput.inputMessage)
                                                    SafeToast.show(
                                                            activity,
                                                            "Backed up lens profile",
                                                            false
                                                    )
                                                }
                                            }

                                        }
                                ).show()
                            }
                        }.isEnabled = profilesExists
                        // ===========================================================================
                    }

                    setupSettingsCallable.call(this)
                }
            }.view as T

    // ===========================================================================
    // ===========================================================================

    private fun ViewGroup.lensStatistics() =
            verticalLayout {
                lparams(width = matchParent, height = wrapContent).lparams {
                    bottomMargin = 10.toDp()
                }

                backgroundColor = ContextCompat.getColor(activity, ResourceUtils.getColor(activity, "backgroundTertiary"))

                view {
                    backgroundResource = ResourceUtils.getColor(activity, "primaryLight")
                }.lparams(width = matchParent, height = 1.toDp())

                linearLayout {
                    verticalLayout {
                        textView("Active Lenses") {
                            setTextAppearance(activity, ResourceUtils.getStyle(activity, "HeaderText"))
                        }.lparams {
                            gravity = Gravity.CENTER
                        }

                        textView("Loading") {
                            id = ResourceUtils.getIdFromString("txt_active_lenses")
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                        }.lparams {
                            gravity = Gravity.CENTER
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        weight = 1f
                    }

                    verticalLayout {
                        textView("Total Lenses") {
                            setTextAppearance(activity, ResourceUtils.getStyle(activity, "HeaderText"))
                        }.lparams {
                            gravity = Gravity.CENTER
                        }

                        textView("Loading") {
                            id = "txt_total_lenses".toId()
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                        }.lparams {
                            gravity = Gravity.CENTER
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        weight = 1f
                    }
                }.lparams(width = matchParent, height = wrapContent)

                view {
                    backgroundResource = ResourceUtils.getColor(activity, "primaryWashed")
                }.lparams(width = matchParent, height = 1.toDp())
            }

    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    class DeletableAdapter(val list: ArrayList<String>, private val deleteItemCallable: Callable<Int>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return with(parent!!.context) {
                linearLayout {
                    lparams(matchParent, wrapContent)
                    padding = 10.toDp()

                    textView(getItem(position)).lparams(matchParent, wrapContent, 1f) {
                        gravity = Gravity.CENTER_VERTICAL
                    }

                    if (position > 0) {
                        imageView(getDrawable(context, "delete_96")) {
                            backgroundResource = getSelectableBackgroundId(context)
                            setColorFilter(ContextCompat.getColor(context, getColor(context, "errorLight")))

                            setOnClickListener {
                                deleteItemCallable.call(position)
                                notifyDataSetChanged()
                            }
                        }.lparams(30.toDp(), 30.toDp()) {
                            gravity = Gravity.CENTER_VERTICAL
                            marginStart = 5.toDp()
                        }
                    }
                }
            }
        }

        override fun getItem(position: Int): String = list[position]

        override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

        override fun getCount(): Int = list.size
    }
}