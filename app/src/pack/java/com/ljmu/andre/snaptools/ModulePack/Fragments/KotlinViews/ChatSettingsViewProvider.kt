package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import com.jaqxues.akrolyb.prefs.getPref
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.header
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCK_TYPING_NOTIFICATIONS
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_CHAT_IN_SC
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORE_CHAT_MESSAGES
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.themedSwitchCompatX
import org.jetbrains.anko.*

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@Suppress("DEPRECATION", "UNCHECKED_CAST")
class ChatSettingsViewProvider {

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(activity: Activity): T =
            activity.UI {
                scrollView {
                    lparams(matchParent, matchParent)

                    verticalLayout {
                        header("Chat Saving Settings")

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            verticalPadding = 5.toDp()
                            horizontalPadding = 10.toDp()
                            text = "Auto save messages in app"
                            isChecked = SAVE_CHAT_IN_SC.getPref()

                            setOnCheckedChangeListener { _, isChecked ->
                                putAndKill(SAVE_CHAT_IN_SC, isChecked, activity)
                            }
                        }

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            verticalPadding = 5.toDp()
                            horizontalPadding = 10.toDp()
                            text = "Store messages locally"
                            isChecked = STORE_CHAT_MESSAGES.getPref()

                            setOnCheckedChangeListener { _, isChecked ->
                                putAndKill(STORE_CHAT_MESSAGES, isChecked, activity)
                            }
                        }

                        header("Chat Notifications")

                        themedSwitchCompatX(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                            verticalPadding = 5.toDp()
                            horizontalPadding = 10.toDp()
                            text = "Disable inbound 'X is typing' notifications"
                            isChecked = BLOCK_TYPING_NOTIFICATIONS.getPref()

                            setOnCheckedChangeListener { _, isChecked ->
                                putAndKill(BLOCK_TYPING_NOTIFICATIONS, isChecked, activity)
                            }
                        }

                    }.lparams(matchParent)
                }
            }.view as T
}