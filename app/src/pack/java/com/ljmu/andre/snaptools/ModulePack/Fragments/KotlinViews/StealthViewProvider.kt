package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.ljmu.andre.GsonPreferences.Preferences
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.GsonPreferences.Preferences.putPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSeekBar
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.StealthViewing
import com.ljmu.andre.snaptools.ModulePack.StealthViewing.bypassNextStealthView
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getStealthLocation
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.getSelectableBorderedDrawable
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.togglePreference
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import com.ljmu.andre.snaptools.Utils.SafeToast
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedSwitchCompat

@Suppress("UNCHECKED_CAST", "DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class StealthViewProvider {

    fun <T : ViewGroup> getMainContainer(activity: Activity): T =
            activity.UI {
                scrollView {
                    lparams(matchParent, matchParent)

                    verticalLayout {
                        padding = 16.toDp()

                        verticalLayout {
                            id = "chat_stealth_container".toId()

                            header("Chat Stealth Button")
                            val isLeftSelected = getPref<Boolean>(STEALTH_CHAT_BUTTON_LEFT)
                            val chatButtonLocations = ArrayList<String>()
                            chatButtonLocations.add("Left")
                            chatButtonLocations.add("Right")


                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_chat_stealth".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show chat stealth button"
                                isChecked = Preferences.getPref(SHOW_CHAT_STEALTH_BUTTON)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(SHOW_CHAT_STEALTH_BUTTON, isChecked, activity)
                                }
                            }

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_stealth_typing".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Hide '... is typing' Notification"
                                isChecked = Preferences.getPref(BLOCK_OUTGOING_TYPING_NOTIFICATION)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(BLOCK_OUTGOING_TYPING_NOTIFICATION, isChecked, activity)
                                }
                            }

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_chat_stealth_message".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show Chat Stealth Message"
                                isChecked = Preferences.getPref(SHOW_CHAT_STEALTH_MESSAGE)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putPref(SHOW_CHAT_STEALTH_MESSAGE, isChecked)
                                }
                            }

                            frameLayout {
                                id = "chat_stealth_position_container".toId()

                                labelledSpinner(
                                        "Position: ",
                                        if (isLeftSelected) "Left" else "Right",
                                        chatButtonLocations,
                                        ViewFactory.OnItemChangedProvider<String>(
                                                { newItem, _, _ ->
                                                    putAndKill(STEALTH_CHAT_BUTTON_LEFT, newItem == "Left", activity)
                                                    val layoutParams = RelativeLayout.LayoutParams(48.toDp(), MATCH_PARENT)

                                                    layoutParams.addRule(
                                                            if (getPref(STEALTH_CHAT_BUTTON_LEFT))
                                                                RelativeLayout.ALIGN_PARENT_LEFT
                                                            else
                                                                RelativeLayout.ALIGN_PARENT_RIGHT
                                                    )

                                                    activity.find<ImageView>("active_chat_stealth_image".toId()).layoutParams = layoutParams
                                                },
                                                { if (getPref<Boolean>(STEALTH_CHAT_BUTTON_LEFT)) "Left" else "Right" }
                                        )
                                )
                            }.lparams(matchParent)

                            labelledSeekBar(
                                    id = "seek_chat_stealth_opacity",
                                    text = "Opacity (%s%%)",
                                    progress = getPref(STEALTH_CHAT_BUTTON_ALPHA),
                                    max = 100,
                                    resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                                        putAndKill(STEALTH_CHAT_BUTTON_ALPHA, progress, activity)
                                    },
                                    progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                                        activity.find<ImageView>("active_chat_stealth_image".toId()).alpha = progress.toFloat() / 100
                                    }
                            )

                            labelledSeekBar(
                                    id = "seek_chat_stealth_padding",
                                    text = "Padding (%spx)",
                                    progress = getPref(STEALTH_CHAT_BUTTON_PADDING),
                                    max = 50,
                                    resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                                        putAndKill(STEALTH_CHAT_BUTTON_PADDING, progress, activity)
                                    },
                                    progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                                        activity.find<ImageView>("active_chat_stealth_image".toId()).padding = progress
                                    }
                            )

                            relativeLayout {
                                id = "chat_stealth_button_preview".toId()

                                backgroundColor = Color.WHITE

                                textView("Example User") {
                                    id = "txt_example_user".toId()
                                    textColor = Color.parseColor("#00ACFF")
                                    setTypeface(null, Typeface.BOLD)
                                }.lparams(wrapContent, wrapContent) {
                                    centerInParent()
                                }

                                imageView(getDrawable(activity, "visibility_open")) {
                                    id = "active_chat_stealth_image".toId()
                                    alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                                    val pad = getPref<Int>(STEALTH_CHAT_BUTTON_PADDING)
                                    setPadding(pad, pad, pad, pad)

                                    //val rule = if (getPref(STEALTH_CHAT_BUTTON_LEFT)) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                                    val layoutParams = RelativeLayout.LayoutParams(48.toDp(), MATCH_PARENT)
                                    //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                                    layoutParams.addRule(
                                            if (getPref(STEALTH_CHAT_BUTTON_LEFT))
                                                RelativeLayout.ALIGN_PARENT_LEFT
                                            else
                                                RelativeLayout.ALIGN_PARENT_RIGHT
                                    )

                                    this.layoutParams = layoutParams
                                }
                            }.lparams(matchParent, 48.toDp())
                        }.lparams(matchParent)

                        verticalLayout {
                            id = "snap_stealth_container".toId()

                            header("Snap Stealth Button")

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_snap_stealth".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show snap stealth button"
                                isChecked = getPref(SHOW_SNAP_STEALTH_BUTTON)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(SHOW_SNAP_STEALTH_BUTTON, isChecked, activity)
                                }
                            }

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_snap_stealth_message".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show snap stealth message"
                                isChecked = getPref(SHOW_SNAP_STEALTH_MESSAGE)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putPref(SHOW_SNAP_STEALTH_MESSAGE, isChecked)
                                }
                            }

                            themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_mark_story_viewed".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Mark stories as viewed client side"
                                isChecked = getPref(STEALTH_MARK_STORY_VIEWED)

                                setOnCheckedChangeListener { _, isChecked ->
                                    putPref(STEALTH_MARK_STORY_VIEWED, isChecked)
                                }
                            }

                            labelledSeekBar(
                                    id = "seek_stealth_snap_size",
                                    text = "Size (%sdp)",
                                    progress = getPref(STEALTH_SNAP_BUTTON_SIZE),
                                    max = 300,
                                    resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                                        putAndKill(STEALTH_SNAP_BUTTON_SIZE, progress, activity)
                                    },
                                    progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                                        val imageView = activity.find<ImageView>("active_snap_stealth_image".toId())
                                        val params = imageView.layoutParams
                                        params.height = progress.toDp()
                                        params.width = progress.toDp()
                                        imageView.layoutParams = params
                                    }
                            )

                            labelledSeekBar(
                                    id = "seek_stealth_snap_opacity",
                                    text = "Opacity (%s%%)",
                                    progress = getPref(STEALTH_SNAP_BUTTON_ALPHA),
                                    max = 100,
                                    resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                                        putAndKill(STEALTH_SNAP_BUTTON_ALPHA, progress, activity)
                                    },
                                    progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                                        activity.find<ImageView>("active_snap_stealth_image".toId()).alpha = progress.toFloat() / 100
                                    }
                            )

                            labelledSeekBar(
                                    id = "seek_stealth_snap_margin",
                                    text = "Margin (%spx)",
                                    progress = getPref(STEALTH_SNAP_BUTTON_MARGIN),
                                    max = 200,
                                    resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                                        putAndKill(STEALTH_SNAP_BUTTON_MARGIN, progress, activity)
                                    },
                                    progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                                        val imageView = activity.find<ImageView>("active_snap_stealth_image".toId())
                                        val params: FrameLayout.LayoutParams = imageView.layoutParams as FrameLayout.LayoutParams
                                        params.margin = progress
                                        imageView.layoutParams = params
                                    }
                            )

                            themedButton(getStyle(activity, "NeutralButton")) {
                                id = "button_stealth_snap_location".toId()

                                text = "Assign Location"
                            }

                            frameLayout {
                                val size = getPref<Int>(STEALTH_SNAP_BUTTON_SIZE)
                                imageView(getDrawable(activity, "visibility_open")) {
                                    id = "active_snap_stealth_image".toId()
                                    alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                                }.lparams(size.toDp(), size.toDp()) {
                                    gravity = Gravity.CENTER
                                    margin = getPref<Int>(STEALTH_SNAP_BUTTON_MARGIN)
                                }
                            }.lparams(matchParent)
                        }.lparams(matchParent)
                    }.lparams(matchParent)
                }
            }.view as T

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getProfileContainer(snapActivity: Activity, moduleContext: Context): T =
            moduleContext.UI {
                linearLayout {
                    lparams(wrapContent, wrapContent) {
                        bottomMargin = 3.toDp()
                        gravity = Gravity.CENTER
                    }

                    id = "stealth_profile_container".toId()

                    val openEyeId = getDrawable(moduleContext, "visibility_open")
                    val closedEyeId = getDrawable(moduleContext, "visibility_closed")
                    val whiteFiveOpacityColor = Color.parseColor("#0dffffff")
                    val whiteFifteenOpacityColor = Color.parseColor("#25ffffff")
                    val edgeRadius = 12.toDp().toFloat()

                    verticalLayout {
                        horizontalPadding = 30.toDp()
                        verticalPadding = 5.toDp()

                        imageView(if (getPref(DEFAULT_CHAT_STEALTH)) closedEyeId else openEyeId) {
                            id = "chat_stealth_image".toId()
                        }.lparams(dip(30), dip(30)) {
                            gravity = Gravity.CENTER
                        }

                        textView("Chats") {
                            setTextAppearance(moduleContext, getStyle(moduleContext, "DefaultText"))
                            textColor = ContextCompat.getColor(moduleContext, getColor(moduleContext, "textPrimaryWashed"))
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        backgroundDrawable = getSelectableBorderedDrawable(
                                whiteFiveOpacityColor,
                                whiteFifteenOpacityColor,
                                floatArrayOf(
                                        edgeRadius, edgeRadius,
                                        0f, 0f,
                                        0f, 0f,
                                        edgeRadius, edgeRadius
                                )
                        )

                        setOnClickListener {
                            val chatStealth = togglePreference(DEFAULT_CHAT_STEALTH)

                            if (getPref(SHOW_CHAT_STEALTH_MESSAGE)) {
                                SafeToast.show(
                                        snapActivity,
                                        "Global Chat Stealth: " + if (chatStealth) "Active" else "Inactive",
                                        Toast.LENGTH_SHORT
                                )
                            }

                            find<ImageView>("chat_stealth_image".toId()).setImageResource(
                                    if (chatStealth) closedEyeId else openEyeId
                            )

                            snapActivity.findOptional<ImageView>("active_chat_stealth_image".toId())?.setImageResource(
                                    if (chatStealth) closedEyeId else openEyeId
                            )
                        }
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER
                        rightMargin = 1.toDp()
                    }

                    verticalLayout {
                        horizontalPadding = 30.toDp()
                        verticalPadding = 5.toDp()

                        imageView(if (getPref(DEFAULT_SNAP_STEALTH)) closedEyeId else openEyeId) {
                            id = "snap_stealth_image".toId()
                        }.lparams(dip(30), dip(30)) {
                            gravity = Gravity.CENTER
                        }

                        textView("Snaps") {
                            setTextAppearance(moduleContext, getStyle(moduleContext, "DefaultText"))
                            textColor = ContextCompat.getColor(moduleContext, getColor(moduleContext, "textPrimaryWashed"));
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        backgroundDrawable = getSelectableBorderedDrawable(
                                whiteFiveOpacityColor,
                                whiteFifteenOpacityColor,
                                floatArrayOf(
                                        0f, 0f,
                                        edgeRadius, edgeRadius,
                                        edgeRadius, edgeRadius,
                                        0f, 0f
                                )
                        )

                        setOnClickListener {
                            val snapStealth = togglePreference(DEFAULT_SNAP_STEALTH)
                            StealthViewing.bypassNextStealthView = !snapStealth

                            if (getPref(SHOW_SNAP_STEALTH_MESSAGE)) {
                                SafeToast.show(
                                        snapActivity,
                                        "Default Snap Stealth: " + if (snapStealth) "Active" else "Inactive",
                                        Toast.LENGTH_SHORT
                                )
                            }

                            find<ImageView>("snap_stealth_image".toId()).setImageResource(
                                    if (snapStealth) closedEyeId else openEyeId
                            )

                            snapActivity.findOptional<ImageView>("active_snap_stealth_image".toId())?.setImageResource(
                                    if (snapStealth) closedEyeId else openEyeId
                            )
                        }
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.CENTER
                        leftMargin = 1.toDp()
                    }
                }
            }.view as T

    fun <T : ViewGroup> getStealthSnapLayout(snapActivity: Activity, moduleContext: Context): T =
            moduleContext.UI {
                frameLayout {
                    lparams(matchParent, matchParent)
                    id = "stealth_layout".toId()

                    val openEyeId = getDrawable(moduleContext, "visibility_open")
                    val closedEyeId = getDrawable(moduleContext, "visibility_closed")
                    val size = getPref<Int>(STEALTH_SNAP_BUTTON_SIZE)

                    imageView(if (getPref(DEFAULT_SNAP_STEALTH)) closedEyeId else openEyeId) {
                        id = "active_snap_stealth_image".toId()
                        backgroundResource = getSelectableBackgroundId(moduleContext)
                        alpha = getPref<Int>(STEALTH_SNAP_BUTTON_ALPHA).toFloat() / 100

                        setOnClickListener {
                            bypassNextStealthView = !bypassNextStealthView
                            setImageResource(if (!bypassNextStealthView) closedEyeId else openEyeId)

                            if (getPref(SHOW_SNAP_STEALTH_MESSAGE)) {
                                SafeToast.show(
                                        snapActivity,
                                        "Current Snap Stealth: " + if (!bypassNextStealthView) "Active" else "Inactive",
                                        Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }.lparams(size.toDp(), size.toDp()) {
                        gravity = getStealthLocation().gravity
                        margin = getPref<Int>(STEALTH_SNAP_BUTTON_MARGIN)
                    }
                }
            }.view as T

    fun <T : ImageView> getStealthChatButton(snapActivity: Activity, headerId: Int, moduleContext: Context, isCheetah: Boolean): T =
            moduleContext.UI {
                val openEyeId = getDrawable(moduleContext, "visibility_open")
                val closedEyeId = getDrawable(moduleContext, "visibility_closed")

                imageView(if (getPref(DEFAULT_CHAT_STEALTH)) closedEyeId else openEyeId) {
                    id = "active_chat_stealth_image".toId()
                    alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                    val pad = getPref<Int>(STEALTH_CHAT_BUTTON_PADDING)
                    setPadding(0, pad, 0, pad)

                    setOnClickListener {
                        val chatStealth = togglePreference(DEFAULT_CHAT_STEALTH)
                        setImageResource(if (chatStealth) closedEyeId else openEyeId)

                        if (getPref(SHOW_CHAT_STEALTH_MESSAGE)) {
                            SafeToast.show(
                                    snapActivity,
                                    "Global Chat Stealth: " + if (chatStealth) "Active" else "Inactive",
                                    Toast.LENGTH_SHORT
                            )
                        }
                    }

                    val leftAlign = getPref<Boolean>(STEALTH_CHAT_BUTTON_LEFT)
                    val layoutParams: RelativeLayout.LayoutParams

                    //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                    if (isCheetah) {
                        layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        val rule = if (leftAlign) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                        layoutParams.addRule(rule, headerId)
                    } else {
                        layoutParams = RelativeLayout.LayoutParams(50.toDp(), MATCH_PARENT)
                        layoutParams.addRule(if (leftAlign) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT)
                    }

                    this.layoutParams = layoutParams
                }
            }.view as T
}