package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.GsonPreferences.Preferences.putPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getFilterScaleType
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedSwitchCompat
import org.jetbrains.anko.support.v4.swipeRefreshLayout


@Suppress("UNCHECKED_CAST", "DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class FiltersSelectionView {
    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    lparams(width = matchParent, height = matchParent)

                    customTabStrip {
                        id = getIdFromString("tab_layout")
                    }

                    frameLayout {
                        lparams(width = matchParent, height = matchParent)
                        padding = 16.toDp()

                        id = getIdFromString("content_container")
                    }.lparams(width = matchParent, height = matchParent)
                }
            }.view as T

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getSettingsContainer(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    scrollView {
                        verticalLayout {
                            textView("Filter Statistics") {
                                setTextAppearance(activity, getStyle(activity, "HeaderText"))
                            }.lparams {
                                gravity = Gravity.CENTER
                                horizontalMargin = 5.toDp()
                            }

                            verticalLayout {
                                backgroundColor = ContextCompat.getColor(activity, getColor(activity, "backgroundTertiary"))

                                view {
                                    backgroundResource = getColor(activity, "primaryLight")
                                }.lparams(width = matchParent, height = 1.toDp())

                                linearLayout {
                                    verticalLayout {
                                        textView("Active Filters") {
                                            setTextAppearance(activity, getStyle(activity, "HeaderText"))
                                        }.lparams {
                                            gravity = Gravity.CENTER
                                        }

                                        textView(getString(activity, "placeholder")) {
                                            id = getIdFromString("txt_active_filters")
                                            textSize = 18f
                                            setTypeface(null, Typeface.BOLD)
                                        }.lparams {
                                            gravity = Gravity.CENTER
                                        }
                                    }.lparams(width = matchParent, height = wrapContent) {
                                        weight = 1f
                                    }

                                    verticalLayout {
                                        textView("Total Filters") {
                                            setTextAppearance(activity, getStyle(activity, "HeaderText"))
                                        }.lparams {
                                            gravity = Gravity.CENTER
                                        }

                                        textView(getString(activity, "placeholder")) {
                                            id = getIdFromString("txt_total_filters")
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
                                    backgroundResource = getColor(activity, "primaryWashed")
                                }.lparams(width = matchParent, height = 1.toDp())
                            }.lparams(width = matchParent, height = wrapContent).lparams {
                                bottomMargin = 10.toDp()
                            }

                            linearLayout {
                                themedButton(getStyle(activity, "NeutralButton")) {
                                    text = "Activate All"
                                    id = getIdFromString("btn_activate_filters")
                                }.lparams(width = matchParent, height = wrapContent) {
                                    weight = 1f
                                }

                                themedButton(getStyle(activity, "NeutralButton")) {
                                    text = "Deactivate All"
                                    id = getIdFromString("btn_deactivate_filters")
                                }.lparams(width = matchParent, height = wrapContent) {
                                    weight = 1f
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                bottomMargin = 20.toDp()
                            }

                            header("Misc Settings")

                            val scalingTypeList: ArrayList<String> = ArrayList()
                            ImageView.ScaleType.values().mapTo(scalingTypeList) { it.name }
                            var selectedScalingIndex: Int = scalingTypeList.indexOf(getPref(FILTER_SCALING_TYPE))

                            if (selectedScalingIndex == -1)
                                selectedScalingIndex = scalingTypeList.indexOf(FILTER_SCALING_TYPE.getDefaultVal())

                            addView(ViewFactory.getLabelledSpinner(
                                    activity,
                                    "spinner_scale_type",
                                    "Scaling Type",
                                    selectedScalingIndex,
                                    scalingTypeList,
                                    null
                            ))

                            header("Sample Background Settings")

                            themedSwitchCompat(getStyle(activity, "DefaultSwitch")) {
                                id = getIdFromString("switch_show_sample")
                                text = "Toggle Filter Background"
                                isChecked = getPref(FILTER_SHOW_SAMPLE_BACKGROUND)

                                verticalPadding = 10.toDp()
                            }

                            themedButton(getStyle(activity, "NeutralButton")) {
                                id = getIdFromString("button_select_sample")
                                text = "Select Filter Background Sample"
                            }.lparams(width = matchParent, height = wrapContent)

                            imageView {
                                id = getIdFromString("img_sample_preview")
                                backgroundColor = 0
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                adjustViewBounds = true
                            }.lparams(width = 100.toDp(), height = matchParent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                                verticalMargin = 10.toDp()
                            }

                            textView("Filter Background Sample") {
                                id = getIdFromString("txt_sample_preview")
                            }.lparams(width = wrapContent, height = wrapContent) {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }

                            header("Now Playing Settings")

                            textView("The Now Playing filter is primarily optimised for Spotify, however other players may work provided the song being listened to is common enough") {
                                textColor = ContextCompat.getColor(activity, getColor(activity, "errorLight"))
                                gravity = Gravity.CENTER
                            }.lparams(width = matchParent, height = wrapContent)

                            view {
                                backgroundResource = getColor(activity, "primaryWashed")
                                alpha = 0.75f
                            }.lparams(width = matchParent, height = 1.toDp())

                            themedSwitchCompat(getStyle(activity, "DefaultSwitch")) {
                                id = getIdFromString("switch_now_playing_enabled")
                                text = "Filter Enabled"
                                isChecked = getPref(FILTER_NOW_PLAYING_ENABLED)

                                verticalPadding = 10.toDp()
                                setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked != getPref<Boolean>(FILTER_NOW_PLAYING_ENABLED)) {
                                        putPref(FILTER_NOW_PLAYING_ENABLED, isChecked)
                                    }
                                }
                            }
                        }
                    }
                }
            }.view as T

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getFiltersView(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    isFocusable = true
                    isFocusableInTouchMode = true

                    swipeRefreshLayout {
                        id = getIdFromString("filter_container")
                        visibility = View.GONE
                    }.lparams(width = matchParent, height = matchParent) {
                        weight = 1f
                    }

                    relativeLayout {
                        id = getIdFromString("empty_filters_container")

                        imageView {
                            id = getIdFromString("logo")
                            setImageResource(getDrawable(activity, "snaptools_logo"))
                            setColorFilter(
                                    ContextCompat.getColor(activity, getColor(activity, "backgroundSecondary")),
                                    PorterDuff.Mode.MULTIPLY
                            )
                        }.lparams(width = wrapContent, height = wrapContent) {
                            centerInParent()
                        }

                        textView("No Filters Found") {
                            id = getIdFromString("txt_no_filters_header")
                            textColor = ContextCompat.getColor(
                                    activity,
                                    getColor(activity, "error")
                            )
                        }.lparams(width = wrapContent, height = wrapContent) {
                            below(getIdFromString("logo"))
                            centerHorizontally()
                        }

                        textView("Please add compatible filter files (.png) to the SnapTools/Filters directory and they will appear here, where you can activate and manage which appear within Snapchat or not") {
                            textColor = ContextCompat.getColor(
                                    activity,
                                    getColor(activity, "textTertiary")
                            )
                            gravity = Gravity.CENTER
                        }.lparams(width = wrapContent, height = wrapContent) {
                            below(getIdFromString("txt_no_filters_header"))
                            centerHorizontally()
                        }
                    }.lparams(width = matchParent, height = matchParent) {
                        weight = 1f
                    }

                    view {
                        backgroundResource = getColor(activity, "primary")
                    }.lparams(width = matchParent, height = 1.toDp()) {
                        verticalMargin = 2.toDp()
                    }

                    linearLayout {
                        imageView(getDrawable(activity, "search_96")) {
                            id = getIdFromString("img_search_filters")
                            setColorFilter(ContextCompat.getColor(activity, getColor(activity, "primaryLight")))
                            setOnClickListener {
                                val searchBar: EditText = activity.findViewById(getIdFromString("txt_search_filters")) as EditText
                                searchBar.requestFocus()
                                searchBar.requestFocusFromTouch()
                                val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.showSoftInput(
                                        activity.findViewById(getIdFromString("img_search_filters")),
                                        InputMethodManager.SHOW_IMPLICIT
                                )
                            }
                        }.lparams(width = 30.toDp(), height = 30.toDp()) {
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        editText {
                            hint = "Search Filters"
                            id = getIdFromString("txt_search_filters")
                        }.lparams(width = matchParent, height = wrapContent) {
                            Gravity.CENTER_VERTICAL
                            weight = 1f
                        }

                        imageView(getDrawable(activity, "delete_96")) {
                            setColorFilter(ContextCompat.getColor(activity, getColor(activity, "errorLight")))
                            setOnClickListener {
                                val searchBar: EditText = activity.findViewById(getIdFromString("txt_search_filters")) as EditText
                                searchBar.setText("")
                                searchBar.clearFocus()
                            }
                        }.lparams(width = 30.toDp(), height = 30.toDp()) {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                    }.lparams(width = matchParent, height = wrapContent)
                }
            }.view as T

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getFilterItemHolder(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    id = getIdFromString("filter_item_holder")
                    backgroundResource = getDrawable(activity, "border_washed")
                    isClickable = true
                    padding = 2.toDp()
                    lparams(width = matchParent, height = wrapContent) {
                        margin = 2.toDp()
                    }

                    frameLayout {
                        id = getIdFromString("filter_background_layout")
                        isClickable = false
                        isDuplicateParentStateEnabled = true

                        imageView {
                            id = getIdFromString("filter_sample_background")
                            backgroundColor = 0
                            isClickable = false
                            isDuplicateParentStateEnabled = true
                            scaleType = ImageView.ScaleType.FIT_XY
                            adjustViewBounds = true
                        }.lparams(width = matchParent, height = matchParent) {
                            margin = 2.toDp()
                        }

                        imageView {
                            id = getIdFromString("filter_thumbnail")
                            backgroundColor = 0
                            isClickable = false
                            isDuplicateParentStateEnabled = true
                            scaleType = getFilterScaleType()
                        }.lparams(width = matchParent, height = matchParent) {
                            margin = 2.toDp()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        margin = 4.toDp()
                    }

                    textView(getString(activity, "placeholder")) {
                        id = getIdFromString("txt_message")
                        gravity = Gravity.CENTER
                        isClickable = false
                        isDuplicateParentStateEnabled = true
                    }.lparams(width = matchParent, height = wrapContent) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
            }.view as T

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getFilterPreviewContainer(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    id = getIdFromString("preview_container")
                    lparams(width = matchParent, height = matchParent)
                    backgroundColor = Color.BLACK
                    isClickable = false
                    isFocusable = false

                    frameLayout {
                        id = getIdFromString("filter_background_layout")
                        isClickable = false
                        isDuplicateParentStateEnabled = true

                        imageView {
                            id = getIdFromString("preview_sample_background")
                            backgroundColor = 0
                            isClickable = false
                            isFocusable = false
                            isDuplicateParentStateEnabled = true
                            scaleType = ImageView.ScaleType.FIT_XY
                            gravity = Gravity.CENTER
                            adjustViewBounds = true
                        }.lparams(width = matchParent, height = matchParent) {
                            gravity = Gravity.CENTER
                        }

                        imageView {
                            id = getIdFromString("preview_imageview")
                            backgroundColor = 0
                            isClickable = false
                            isFocusable = false
                            isDuplicateParentStateEnabled = true
                            scaleType = getFilterScaleType()
                            gravity = Gravity.CENTER
                        }.lparams(width = matchParent, height = matchParent) {
                            gravity = Gravity.CENTER
                        }

                    }.lparams(width = matchParent, height = wrapContent)
                }
            }.view as T


    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getPlayerPositionSetter(activity: Activity): T =
            activity.UI {
                relativeLayout {
                    lparams(width = matchParent, height = matchParent)
                }
            }.view as T
}