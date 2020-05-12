package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.annotation.SuppressLint
import android.app.Activity
import androidx.core.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.Dialogs.Content.TextInput
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.*
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedSwitchCompat

@Suppress("DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class StoryBlockerViewProvider {
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getContainer(activity: Activity): T =
            activity.UI {
                verticalLayout {
                    lparams(width = matchParent, height = matchParent) {
                        margin = dip(16)
                    }

                    textView("Advert Blocking") {
                        setTextAppearance(activity, ResourceUtils.getStyle(activity, "HeaderText"))
                    }.lparams {
                        horizontalMargin = dip(5)
                    }

                    view {
                        backgroundResource = ResourceUtils.getColor(activity, "primaryLight")
                    }.lparams(width = matchParent, height = dip(1))

                    themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                        text = "Block Story Discover Snaps"
                        isChecked = getPref(STORY_BLOCKER_DISCOVER_BLOCKED)

                        verticalPadding = dip(10)
                        setOnCheckedChangeListener { _, isChecked ->
                            putAndKill(STORY_BLOCKER_DISCOVER_BLOCKED, isChecked, activity)
                        }
                    }

                    themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                        text = "Block Story Video Adverts"
                        isChecked = getPref(STORY_BLOCKER_ADVERTS_BLOCKED)

                        verticalPadding = dip(10)
                        setOnCheckedChangeListener { _, isChecked ->
                            putAndKill(STORY_BLOCKER_ADVERTS_BLOCKED, isChecked, activity)
                        }
                    }

                    themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                        text = "Show Block Stories button in Snapchat"
                        isChecked = getPref(STORY_BLOCKER_SHOW_BUTTON)

                        verticalPadding = dip(10)
                        setOnCheckedChangeListener { _, isChecked ->
                            putAndKill(STORY_BLOCKER_SHOW_BUTTON, isChecked, activity)
                        }
                    }

                    textView("Blocked User Stories") {
                        setTextAppearance(activity, ResourceUtils.getStyle(activity, "HeaderText"))
                        textColor = ContextCompat.getColor(activity, getColor(activity, "error"))
                    }.lparams {
                        horizontalMargin = dip(5)
                    }

                    view {
                        backgroundResource = ResourceUtils.getColor(activity, "errorLight")
                    }.lparams(width = matchParent, height = dip(1))

                    val mAdapter = BlockedUserAdapter(activity)

                    listView {
                        adapter = mAdapter
                    }.lparams(width = matchParent, height = matchParent) {
                        weight = 1f
                    }

                    view {
                        backgroundResource = ResourceUtils.getColor(activity, "primaryWashed")
                    }.lparams(width = matchParent, height = dip(1)) {
                        verticalMargin = dip(5)
                    }

                    themedButton {
                        text = "Block User Stories"
                        backgroundResource = getDrawable(activity, "error_button")
                        setTextAppearance(activity, getStyle(activity, "ErrorButton"))

                        setOnClickListener {
                            DialogFactory.createTextInputDialog(
                                    activity,
                                    "Block User Story",
                                    "Add a username to the list of stories to block."
                                            + "\n" + htmlHighlight("YES") + " - To block the supplied username"
                                            + "\n" + htmlHighlight("NO") + " - To cancel",
                                    "Username",
                                    "",
                                    1,
                                    object : ThemedDialog.ThemedClickListener() {
                                        override fun clicked(themedDialog: ThemedDialog) {
                                            val input = themedDialog.getExtension<TextInput>()
                                            val username = input.inputMessage

                                            if (username == null || username.isEmpty()) {
                                                SafeToastAdapter.showErrorToast(
                                                        activity,
                                                        "Invalid Username Supplied!"
                                                )
                                                return
                                            }

                                            addToCollection(BLOCKED_STORIES, username.toLowerCase())
                                            mAdapter.refreshUserList()
                                            themedDialog.dismiss()
                                        }
                                    }
                            ).show()
                        }
                    }.lparams(width = matchParent, height = wrapContent)
                }
            }.view as T

    class BlockedUserAdapter(val activity: Activity) : BaseAdapter() {
        private var blockedUserList: List<String> = ArrayList(getPref<HashSet<String>>(BLOCKED_STORIES))

        fun refreshUserList() {
            blockedUserList = ArrayList(getPref<HashSet<String>>(BLOCKED_STORIES))
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = getItem(position)
            return with(parent!!.context) {
                verticalLayout {
                    lparams(width = matchParent, height = wrapContent)

                    linearLayout {
                        textView(item) {
                            horizontalPadding = dip(20)
                        }.lparams(width = matchParent, height = wrapContent) {
                            gravity = Gravity.CENTER
                            weight = 1f
                        }

                        themedButton {
                            text = "Unblock"

                            setOnClickListener {
                                removeFromCollection(BLOCKED_STORIES, item)
                                refreshUserList()
                            }
                        }.lparams(width = wrapContent, height = wrapContent) {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        verticalMargin = dip(5)
                    }

                    view {
                        backgroundResource = ResourceUtils.getColor(activity, "errorLight")
                    }.lparams(width = matchParent, height = dip(1))
                }
            }
        }

        override fun getItem(position: Int): String = blockedUserList[position]

        override fun getItemId(position: Int): Long = getIdFromString(getItem(position)).toLong()

        override fun getCount(): Int = blockedUserList.size

    }
}