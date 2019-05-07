package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSeekBar
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter
import com.ljmu.andre.snaptools.ModulePack.StealthViewing
import com.ljmu.andre.snaptools.ModulePack.StealthViewing.bypassNextStealthView
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.togglePreference
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
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

                        header("Chat Stealth Button")
                        val isLeftSelected = getPref<Boolean>(STEALTH_CHAT_BUTTON_LEFT)
                        val chatButtonLocations = ArrayList<String>()
                        chatButtonLocations.add("Left")
                        chatButtonLocations.add("Right")

                        labelledSpinner(
                                "Position: ",
                                if (isLeftSelected) "Left" else "Right",
                                chatButtonLocations,
                                ViewFactory.OnItemChangedProvider<String>(
                                        { newItem, _, _ ->
                                            putAndKill(STEALTH_CHAT_BUTTON_LEFT, newItem == "Left", activity)

                                            val rule = if (getPref(STEALTH_CHAT_BUTTON_LEFT)) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                                            val layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                                            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                                            layoutParams.addRule(rule, "txt_example_user".toId())
                                            activity.find<ImageView>("active_chat_stealth_image".toId()).layoutParams = layoutParams
                                        },
                                        { if (getPref<Boolean>(STEALTH_CHAT_BUTTON_LEFT)) "Left" else "Right" }
                                )
                        )

                        labelledSeekBar(
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
                                setColorFilter(ContextCompat.getColor(activity, getColor(activity, "backgroundPrimary")))
                                alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                                val pad = getPref<Int>(STEALTH_CHAT_BUTTON_PADDING)
                                setPadding(pad, pad, pad, pad)

                                val rule = if (getPref(STEALTH_CHAT_BUTTON_LEFT)) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                                val layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                                layoutParams.addRule(rule, "txt_example_user".toId())
                                this.layoutParams = layoutParams
                            }.lparams(wrapContent, matchParent)
                        }.lparams(matchParent, 50.toDp())

                        header("Snap Stealth Button")

                        labelledSeekBar(
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
                                text = "Margin (%spx)",
                                progress = getPref(STEALTH_SNAP_BUTTON_MARGIN),
                                max = 50,
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
                                setColorFilter(ContextCompat.getColor(activity, getColor(activity, "textPrimary")))
                                alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                            }.lparams(size.toDp(), size.toDp()) {
                                gravity = Gravity.CENTER
                                margin = getPref<Int>(STEALTH_SNAP_BUTTON_MARGIN)
                            }
                        }.lparams(matchParent, wrapContent)
                    }.lparams(matchParent)
                }
            }.view as T

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getProfileContainer(snapActivity: Activity, moduleContext: Context): T =
            moduleContext.UI {
                linearLayout {
                    lparams(matchParent, wrapContent)

                    val openEyeId = getDrawable(moduleContext, "visibility_open")
                    val closedEyeId = getDrawable(moduleContext, "visibility_closed")

                    verticalLayout {
                        imageView(if (getPref(DEFAULT_CHAT_STEALTH)) closedEyeId else openEyeId) {
                            id = "chat_stealth_image".toId()
                            setColorFilter(ContextCompat.getColor(moduleContext, getColor(moduleContext, "textPrimary")))
                        }.lparams(dip(50), dip(50)) {
                            gravity = Gravity.CENTER
                        }

                        textView("Chats") {
                            setTextAppearance(moduleContext, getStyle(moduleContext, "DefaultText"))
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        backgroundResource = getSelectableBackgroundId(moduleContext)

                        setOnClickListener {
                            val chatStealth = togglePreference(DEFAULT_CHAT_STEALTH)

                            find<ImageView>("chat_stealth_image".toId()).setImageResource(
                                    if (chatStealth) closedEyeId else openEyeId
                            )

                            snapActivity.findOptional<ImageView>("active_chat_stealth_image".toId())?.setImageResource(
                                    if (chatStealth) closedEyeId else openEyeId
                            )
                        }
                    }.lparams(matchParent, wrapContent, 1f) {
                        gravity = Gravity.CENTER
                    }

                    verticalLayout {
                        imageView(if (getPref(DEFAULT_SNAP_STEALTH)) closedEyeId else openEyeId) {
                            id = "snap_stealth_image".toId()
                            setColorFilter(ContextCompat.getColor(moduleContext, getColor(moduleContext, "textPrimary")))
                        }.lparams(dip(50), dip(50)) {
                            gravity = Gravity.CENTER
                        }

                        textView("Snaps") {
                            setTextAppearance(moduleContext, getStyle(moduleContext, "DefaultText"))
                        }.lparams(wrapContent, wrapContent) {
                            gravity = Gravity.CENTER
                        }

                        backgroundResource = getSelectableBackgroundId(moduleContext)

                        setOnClickListener {
                            val snapStealth = togglePreference(DEFAULT_SNAP_STEALTH)
                            StealthViewing.bypassNextStealthView = !snapStealth

                            find<ImageView>("snap_stealth_image".toId()).setImageResource(
                                    if (snapStealth) closedEyeId else openEyeId
                            )

                            snapActivity.findOptional<ImageView>("active_snap_stealth_image".toId())?.setImageResource(
                                    if (snapStealth) closedEyeId else openEyeId
                            )
                        }
                    }.lparams(matchParent, wrapContent, 1f) {
                        gravity = Gravity.CENTER
                    }
                }
            }.view as T

    fun <T : ViewGroup> getStealthSnapLayout(moduleContext: Context): T =
            moduleContext.UI {
                relativeLayout {
                    lparams(matchParent, matchParent)
                    id = "stealth_layout".toId()

                    val openEyeId = getDrawable(moduleContext, "visibility_open")
                    val closedEyeId = getDrawable(moduleContext, "visibility_closed")
                    val size = getPref<Int>(STEALTH_SNAP_BUTTON_SIZE)

                    imageView(if (getPref(DEFAULT_SNAP_STEALTH)) closedEyeId else openEyeId) {
                        id = "active_snap_stealth_image".toId()
                        backgroundResource = getSelectableBackgroundId(moduleContext)
                        setColorFilter(ContextCompat.getColor(moduleContext, getColor(moduleContext, "textPrimary")))
                        alpha = getPref<Int>(STEALTH_SNAP_BUTTON_ALPHA).toFloat() / 100

                        setOnClickListener {
                            bypassNextStealthView = !bypassNextStealthView
                            setImageResource(if (!bypassNextStealthView) closedEyeId else openEyeId)
                        }
                    }.lparams(size.toDp(), size.toDp()) {
                        centerHorizontally()
                        margin = getPref<Int>(STEALTH_SNAP_BUTTON_MARGIN)
                    }
                }
            }.view as T

    fun <T : ImageView> getStealthChatButton(snapActivity: Activity, moduleContext: Context): T =
            moduleContext.UI {
                val openEyeId = getDrawable(moduleContext, "visibility_open")
                val closedEyeId = getDrawable(moduleContext, "visibility_closed")

                imageView(if (getPref(DEFAULT_CHAT_STEALTH)) closedEyeId else openEyeId) {
                    id = "active_chat_stealth_image".toId()
                    backgroundResource = getSelectableBackgroundId(moduleContext)
                    setColorFilter(ContextCompat.getColor(moduleContext, getColor(moduleContext, "backgroundPrimary")))
                    alpha = getPref<Int>(STEALTH_CHAT_BUTTON_ALPHA).toFloat() / 100
                    val pad = getPref<Int>(STEALTH_CHAT_BUTTON_PADDING)
                    setPadding(pad, pad, pad, pad)

                    setOnClickListener {
                        setImageResource(if (togglePreference(DEFAULT_CHAT_STEALTH)) closedEyeId else openEyeId)
                    }

                    val rule = if (getPref(STEALTH_CHAT_BUTTON_LEFT)) RelativeLayout.LEFT_OF else RelativeLayout.RIGHT_OF
                    val layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                    layoutParams.addRule(rule, getId(snapActivity, "chat_friends_name"))
                    this.layoutParams = layoutParams
                }
            }.view as T
}