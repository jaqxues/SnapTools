package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.jaqxues.akrolyb.prefs.getPref
import com.jaqxues.akrolyb.prefs.putPref
import com.jaqxues.akrolyb.prefs.toggle
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSeekBar
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.StealthViewing
import com.ljmu.andre.snaptools.ModulePack.StealthViewing.bypassNextStealthView
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCK_OUTGOING_TYPING_NOTIFICATION
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.DEFAULT_CHAT_STEALTH
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.DEFAULT_SNAP_STEALTH
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_CHAT_STEALTH_BUTTON
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_CHAT_STEALTH_MESSAGE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_SNAP_STEALTH_BUTTON
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_SNAP_STEALTH_MESSAGE
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_CHAT_BUTTON_ALPHA
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_CHAT_BUTTON_LEFT
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_CHAT_BUTTON_PADDING
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_MARK_STORY_VIEWED
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_SNAP_BUTTON_ALPHA
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_SNAP_BUTTON_MARGIN
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_SNAP_BUTTON_SIZE
import com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getStealthLocation
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.getSelectableBorderedDrawable
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.togglePreference
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import com.ljmu.andre.snaptools.Utils.SafeToast
import com.ljmu.andre.snaptools.Utils.themedSwitchCompatX
import org.jetbrains.anko.*

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
                            val isLeftSelected = STEALTH_CHAT_BUTTON_LEFT.getPref()
                            val chatButtonLocations = ArrayList<String>()
                            chatButtonLocations.add("Left")
                            chatButtonLocations.add("Right")


                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_chat_stealth".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show chat stealth button"
                                isChecked = SHOW_CHAT_STEALTH_BUTTON.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(SHOW_CHAT_STEALTH_BUTTON, isChecked, activity)
                                }
                            }

                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_stealth_typing".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Hide '... is typing' Notification"
                                isChecked = BLOCK_OUTGOING_TYPING_NOTIFICATION.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(BLOCK_OUTGOING_TYPING_NOTIFICATION, isChecked, activity)
                                }
                            }

                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_chat_stealth_message".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show Chat Stealth Message"
                                isChecked = SHOW_CHAT_STEALTH_MESSAGE.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    SHOW_CHAT_STEALTH_MESSAGE.putPref(isChecked)
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
                                                            if (STEALTH_CHAT_BUTTON_LEFT.getPref())
                                                                RelativeLayout.ALIGN_PARENT_LEFT
                                                            else
                                                                RelativeLayout.ALIGN_PARENT_RIGHT
                                                    )

                                                    activity.find<ImageView>("active_chat_stealth_image".toId()).layoutParams = layoutParams
                                                },
                                                { if (STEALTH_CHAT_BUTTON_LEFT.getPref()) "Left" else "Right" }
                                        )
                                )
                            }.lparams(matchParent)

                            labelledSeekBar(
                                    id = "seek_chat_stealth_opacity",
                                    text = "Opacity (%s%%)",
                                    progress = STEALTH_CHAT_BUTTON_ALPHA.getPref(),
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
                                    progress = STEALTH_CHAT_BUTTON_PADDING.getPref(),
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
                                    alpha = STEALTH_CHAT_BUTTON_ALPHA.getPref().toFloat() / 100
                                    val pad = STEALTH_CHAT_BUTTON_PADDING.getPref()
                                    setPadding(pad, pad, pad, pad)

                                    //val rule = if (STEALTH_CHAT_BUTTON_LEFT).getPref() RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                                    val layoutParams = RelativeLayout.LayoutParams(48.toDp(), MATCH_PARENT)
                                    //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                                    layoutParams.addRule(
                                            if (STEALTH_CHAT_BUTTON_LEFT.getPref())
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

                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_snap_stealth".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show snap stealth button"
                                isChecked = SHOW_SNAP_STEALTH_BUTTON.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    putAndKill(SHOW_SNAP_STEALTH_BUTTON, isChecked, activity)
                                }
                            }

                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_show_snap_stealth_message".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Show snap stealth message"
                                isChecked = SHOW_SNAP_STEALTH_MESSAGE.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    SHOW_SNAP_STEALTH_MESSAGE.putPref(isChecked)
                                }
                            }

                            themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                                id = "switch_mark_story_viewed".toId()

                                verticalPadding = 5.toDp()
                                horizontalPadding = 10.toDp()
                                text = "Mark stories as viewed client side"
                                isChecked = STEALTH_MARK_STORY_VIEWED.getPref()

                                setOnCheckedChangeListener { _, isChecked ->
                                    STEALTH_MARK_STORY_VIEWED.putPref(isChecked)
                                }
                            }

                            labelledSeekBar(
                                    id = "seek_stealth_snap_size",
                                    text = "Size (%sdp)",
                                    progress = STEALTH_SNAP_BUTTON_SIZE.getPref(),
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
                                    progress = STEALTH_SNAP_BUTTON_ALPHA.getPref(),
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
                                    progress = STEALTH_SNAP_BUTTON_MARGIN.getPref(),
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
                                val size = STEALTH_SNAP_BUTTON_SIZE.getPref()
                                imageView(getDrawable(activity, "visibility_open")) {
                                    id = "active_snap_stealth_image".toId()
                                    alpha = STEALTH_CHAT_BUTTON_ALPHA.getPref().toFloat() / 100
                                }.lparams(size.toDp(), size.toDp()) {
                                    gravity = Gravity.CENTER
                                    margin = STEALTH_SNAP_BUTTON_MARGIN.getPref()
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

                        imageView(if (DEFAULT_CHAT_STEALTH.getPref()) closedEyeId else openEyeId) {
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

                            if (SHOW_CHAT_STEALTH_MESSAGE.getPref()) {
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

                        imageView(if (DEFAULT_SNAP_STEALTH.getPref()) closedEyeId else openEyeId) {
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

                            if (SHOW_SNAP_STEALTH_MESSAGE.getPref()) {
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
                    val size = STEALTH_SNAP_BUTTON_SIZE.getPref()

                    imageView(if (DEFAULT_SNAP_STEALTH.getPref()) closedEyeId else openEyeId) {
                        id = "active_snap_stealth_image".toId()
                        backgroundResource = getSelectableBackgroundId(moduleContext)
                        alpha = STEALTH_SNAP_BUTTON_ALPHA.getPref().toFloat() / 100

                        setOnClickListener {
                            bypassNextStealthView = !bypassNextStealthView
                            setImageResource(if (!bypassNextStealthView) closedEyeId else openEyeId)

                            if (SHOW_SNAP_STEALTH_MESSAGE.getPref()) {
                                SafeToast.show(
                                        snapActivity,
                                        "Current Snap Stealth: " + if (!bypassNextStealthView) "Active" else "Inactive",
                                        Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }.lparams(size.toDp(), size.toDp()) {
                        gravity = getStealthLocation().gravity
                        margin = STEALTH_SNAP_BUTTON_MARGIN.getPref()
                    }
                }
            }.view as T

    fun <T : ImageView> getStealthChatButton(snapActivity: Activity, headerId: Int, moduleContext: Context, isCheetah: Boolean): T =
            moduleContext.UI {
                val openEyeId = getDrawable(moduleContext, "visibility_open")
                val closedEyeId = getDrawable(moduleContext, "visibility_closed")

                imageView(if (DEFAULT_CHAT_STEALTH.getPref()) closedEyeId else openEyeId) {
                    id = "active_chat_stealth_image".toId()
                    alpha = STEALTH_CHAT_BUTTON_ALPHA.getPref().toFloat() / 100
                    val pad = STEALTH_CHAT_BUTTON_PADDING.getPref()
                    setPadding(0, pad, 0, pad)

                    setOnClickListener {
                        val chatStealth = DEFAULT_CHAT_STEALTH.toggle()
                        setImageResource(if (chatStealth) closedEyeId else openEyeId)

                        if (SHOW_CHAT_STEALTH_MESSAGE.getPref()) {
                            SafeToast.show(
                                    snapActivity,
                                    "Global Chat Stealth: " + if (chatStealth) "Active" else "Inactive",
                                    Toast.LENGTH_SHORT
                            )
                        }
                    }

                    val leftAlign = STEALTH_CHAT_BUTTON_LEFT.getPref()
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