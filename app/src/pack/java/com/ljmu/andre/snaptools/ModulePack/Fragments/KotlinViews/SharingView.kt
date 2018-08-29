package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.view.Gravity
import android.widget.LinearLayout
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BATCHED_MEDIA_CAP
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_SHARING_TUTORIAL
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.getLabelledSeekbar
import com.ljmu.andre.snaptools.Utils.Constants
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_VIDEO_COMPRESSION_DIALOG
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedSwitchCompat

@Suppress("DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class SharingView {
    @SuppressLint("ResourceType")
    fun getContainer(activity: Activity): LinearLayout =
            activity.UI {
                verticalLayout {
                    lparams(width = matchParent, height = wrapContent)
                    padding = dip(16)

                    textView("Misc Settings") {
                        setTextAppearance(activity, getStyle(activity, "HeaderText"))
                    }.lparams {
                        horizontalMargin = dip(5)
                    }

                    view {
                        backgroundResource = getColor(activity, "primaryLight")
                    }.lparams(width = matchParent, height = dip(1))

                    themedSwitchCompat(getStyle(activity, "DefaultSwitch")) {
                        text = "Display tutorial when sharing"
                        verticalPadding = dip(10)
                        id = getIdFromString("switch_sharing_show_tutorial")
                        isChecked = getPref(SHOW_SHARING_TUTORIAL)
                        setOnCheckedChangeListener({ _, isChecked -> putAndKill(SHOW_SHARING_TUTORIAL, isChecked, activity) })
                    }.lparams(width = matchParent, height = wrapContent) {
                        horizontalMargin = dip(15)
                    }

                    themedSwitchCompat(getStyle(activity, "DefaultSwitch")) {
                        text = "Video Compression Prompt"
                        verticalPadding = dip(10)
                        id = getIdFromString("switch_sharing_show_compression_dialog")

                        if (Constants.getApkVersionCode() >= 65) {
                            isChecked = getPref(SHOW_VIDEO_COMPRESSION_DIALOG)
                            setOnCheckedChangeListener({ _, isChecked -> putAndKill(SHOW_VIDEO_COMPRESSION_DIALOG, isChecked, activity) })
                        } else {
                            text = text as String + "\n(Apk Update Required)"
                            isEnabled = false
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        horizontalMargin = dip(15)
                    }

                    textView("Batched Media Cap") {
                        setTextAppearance(activity, getStyle(activity, "HeaderText"))
                    }.lparams {
                        horizontalMargin = dip(5)
                    }

                    view {
                        backgroundResource = getColor(activity, "primaryLight")
                    }.lparams(width = matchParent, height = dip(1))

                    textView("Sets the max length of shared videos") {
                        setTextAppearance(activity, getStyle(activity, "DefaultText"))
                    }

                    verticalLayout {
                        id = getIdFromString("batch_slider_container")
                    }.lparams(width = matchParent, height = wrapContent)

                    addView(
                            getLabelledSeekbar(
                                    activity,
                                    "%s Snaps",
                                    36,
                                    6,
                                    getPref(BATCHED_MEDIA_CAP),
                                    true,
                                    { _, progress ->
                                        putAndKill(BATCHED_MEDIA_CAP, progress, activity)
                                        if (progress > 6) {
                                            DialogFactory.createBasicMessage(
                                                    activity,
                                                    "Batched Media Warning",
                                                    "6 is the default value of Snapchat and the only officially supported value, anything higher may cause the video to fail to send or Snapchat to crash"
                                            ).show()
                                        }
                                    }
                            )
                    )

                    textView("(1 snap = 10 seconds)") {
                        setTextAppearance(activity, getStyle(activity, "DefaultText"))
                        gravity = Gravity.CENTER
                        textSize = 10F
                    }.lparams(width = matchParent, height = wrapContent)
                }
            }.view as LinearLayout
}