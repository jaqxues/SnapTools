package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STEALTH_SNAP_BUTTON_LOCATION
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.detach
import com.ljmu.andre.snaptools.Utils.AnimationUtils
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor
import org.jetbrains.anko.*
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class StealthLocationOverlay(private val activity: Activity) {
    private val decorView: ViewGroup = activity.window.decorView as ViewGroup
    private val overlayContainer: ViewGroup = getOverlayContainer()
    private var startUiVisibility: Int = decorView.systemUiVisibility

    public fun show() {
        startUiVisibility = decorView.systemUiVisibility
        decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        decorView.addView(detach(overlayContainer))
        AnimationUtils.fade(overlayContainer, true)
    }

    public fun dismiss() {
        decorView.systemUiVisibility = startUiVisibility
        decorView.removeView(overlayContainer)
        AnimationUtils.fade(overlayContainer, false)
    }

    public fun refreshStatusUiVisibility() {
        if (getVisibility()) {
            decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            decorView.systemUiVisibility = startUiVisibility
        }
    }

    public fun getVisibility(): Boolean =
            overlayContainer.visibility == View.VISIBLE && overlayContainer.isShown

    private fun <T : ViewGroup> getOverlayContainer(): T =
            activity.UI {
                val iconSize = 50
                val selectedPosition = getPref<String>(STEALTH_SNAP_BUTTON_LOCATION)

                frameLayout {
                    layoutParams = LinearLayout.LayoutParams(matchParent, context.resources?.displayMetrics?.heightPixels!!, 1f)
                    backgroundResource = getColor(context, "backgroundPrimary")

                    for (stealthPosition in StealthPosition.values()) {
                        val isSelected = stealthPosition.name == selectedPosition;

                        Timber.d("Working with gravity: ${stealthPosition.gravity}")

                        imageView(ResourceUtils.getDrawable(context, if (isSelected) "visibility_open" else "visibility_closed")) {
                            if (isSelected)
                                id = "btn_selected_location".toId()

                            padding = 10.toDp()

                            backgroundDrawable = ViewFactory.getBorderedDrawable(
                                    ContextCompat.getColor(context, getColor(context, if (isSelected) "successLight" else "errorLight")),
                                    ContextCompat.getColor(context, getColor(context, "textSecondary")),
                                    2,
                                    10
                            )

                            setOnClickListener {
                                putAndKill(STEALTH_SNAP_BUTTON_LOCATION, stealthPosition.name, activity)
                                val currActiveItem = activity.findOptional<ImageView>("btn_selected_location".toId())

                                if (currActiveItem != null) {
                                    assignStealthLocationState(
                                            activity,
                                            currActiveItem,
                                            false
                                    )
                                }

                                assignStealthLocationState(
                                        activity,
                                        this,
                                        true
                                )

                                currActiveItem?.id = "inactive".toId()
                                this.id = "btn_selected_location".toId()
                            }
                        }.lparams(iconSize.toDp(), iconSize.toDp()) {
                            gravity = stealthPosition.gravity
                        }
                    }

                    frameLayout {
                        backgroundDrawable = ViewFactory.getBorderedDrawable(
                                ContextCompat.getColor(context, getColor(context, "backgroundPrimary")),
                                ContextCompat.getColor(context, getColor(context, "primaryLight")),
                                2,
                                10
                        )

                        verticalLayout {
                            padding = 10.toDp()

                            header("Set Stealth Location", gravity = Gravity.CENTER)

                            textView {
                                text = "Tap the boxes around the edge of the screen to assign the stealth button location for snaps."
                                gravity = Gravity.CENTER
                            }.lparams(matchParent)

                            themedButton(ResourceUtils.getStyle(activity, "NeutralButton")) {
                                text = "Done"

                                setOnClickListener {
                                    dismiss()
                                }
                            }.lparams {
                                topMargin = 10.toDp()
                                gravity = Gravity.CENTER
                            }
                        }.lparams(matchParent) {
                            gravity = Gravity.CENTER
                        }
                    }.lparams(matchParent, matchParent) {
                        margin = (iconSize + 5).toDp()
                        gravity = Gravity.CENTER
                    }
                }
            }.view as T

    private fun assignStealthLocationState(context: Context, imageView: ImageView?, isSelected: Boolean) {
        if (isSelected)
            imageView?.id = "btn_selected_location".toId()

        imageView?.imageResource = ResourceUtils.getDrawable(context, if (isSelected) "visibility_open" else "visibility_closed")
        imageView?.backgroundDrawable = ViewFactory.getBorderedDrawable(
                ContextCompat.getColor(context, getColor(context, if (isSelected) "successLight" else "errorLight")),
                ContextCompat.getColor(context, getColor(context, "textSecondary")),
                2,
                10
        )
    }

    enum class StealthPosition(var gravity: Int) {
        TOP_LEFT(Gravity.TOP or Gravity.LEFT),
        TOP(Gravity.TOP or Gravity.CENTER_HORIZONTAL),
        TOP_RIGHT(Gravity.TOP or Gravity.RIGHT),
        LEFT(Gravity.LEFT or Gravity.CENTER_VERTICAL),
        RIGHT(Gravity.RIGHT or Gravity.CENTER_VERTICAL),
        BOTTOM_LEFT(Gravity.BOTTOM or Gravity.LEFT),
        BOTTOM(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL),
        BOTTOM_RIGHT(Gravity.BOTTOM or Gravity.RIGHT);
    }
}