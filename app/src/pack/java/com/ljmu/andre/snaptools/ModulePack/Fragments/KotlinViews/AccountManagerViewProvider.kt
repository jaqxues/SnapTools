package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.app.Activity
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Fragments.AccountManagerFragment.AccountEvent
import com.ljmu.andre.snaptools.ModulePack.Fragments.AccountManagerFragment.AccountManagerEvent
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter
import com.ljmu.andre.snaptools.ModulePack.Utils.AccountManagerUtils
import com.ljmu.andre.snaptools.ModulePack.Utils.AccountManagerUtils.SnapchatAccountModel
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ListedViewPageAdapter
import com.ljmu.andre.snaptools.ModulePack.Utils.Result
import com.ljmu.andre.snaptools.Utils.Callable
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory
import com.ljmu.andre.snaptools.Utils.ResourceUtils
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.viewPager

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class AccountManagerViewProvider(val activity: Activity, private val managerCallable: Callable<AccountManagerEvent>,
                                 private val accountEventCallable: Callable<Result<AccountEvent, Int>>,
                                 private val accountList: List<SnapchatAccountModel>,
                                 val setupAccountsView: Callable<ViewGroup>, val setupSettingsView: Callable<ViewGroup>) {
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
                /*verticalLayout {
                    lparams(matchParent, matchParent)
                    horizontalPadding = 16.toDp()
                    topPadding = 16.toDp()

                    id = "main_container".toId()

                    accountDisplayView {
                        visibility = View.GONE
                    }

                    errorView {
                        setErrorViewText(
                                this,
                                "Accounts Locked",
                                "Please enter your password to decrypt your Accounts",
                                "Unlock Accounts"
                        )
                    }
                }*/
            }.view as T

    private fun setupPages(viewPager: ViewPager) {
        val viewList = ArrayList<Pair<String, View>>()

        viewList.add(Pair.create("Accounts", initAccountsView()))
        viewList.add(Pair.create("Settings", initSettingsView()))

        viewPager.adapter = ListedViewPageAdapter(
                viewList
        )
    }

    private fun <T : ViewGroup> initSettingsView(): T =
            activity.UI {
                verticalLayout {
                    /*themedSwitchCompat(ResourceUtils.getStyle(activity, "DefaultSwitch")) {
                        verticalPadding = 5.toDp()
                        horizontalPadding = 10.toDp()
                        text = "Use Encryption"
                        isChecked = Preferences.getPref(ModulePreferenceDef.SHOW_LENS_NAMES)

                        setOnCheckedChangeListener { _, isChecked ->
                            managerCallable.call(
                                    if (isChecked) AccountManagerEvent.ENCRYPT_ALL else AccountManagerEvent.DECRYPT_ALL
                            )
                        }
                    }*/

                    themedButton(ResourceUtils.getStyle(activity, "ErrorButton")) {
                        text = "Backup Current Account"

                        setOnClickListener {
                            managerCallable.call(AccountManagerEvent.BACKUP_ACCOUNT)
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        topMargin = 5.toDp()
                        gravity = Gravity.CENTER
                    }

                    themedButton(ResourceUtils.getStyle(activity, "ErrorButton")) {
                        text = "Safe Snapchat Logout"

                        setOnClickListener {
                            val logoutResultCallable = Callable<Result<Boolean, String>> {
                                if (it.key)
                                    SafeToastAdapter.showDefaultToast(activity, it.value)
                                else
                                    SafeToastAdapter.showErrorToast(activity, it.value)
                            }

                            DialogFactory.createConfirmation(
                                    activity,
                                    "[IMPORTANT] Perform Snapchat Logout?",
                                    "Are you sure you would like to perform a semi-safe logout of Snapchat?"
                                            + "\nThis will ensure your authentication token remains valid, but will log you out of Snapchat and require you to pass Safetynet to log back in",
                                    object : ThemedDialog.ThemedClickListener() {
                                        override fun clicked(themedDialog: ThemedDialog?) {
                                            themedDialog?.dismiss()
                                            AccountManagerUtils.safeLogout(
                                                    activity,
                                                    logoutResultCallable
                                            )
                                        }
                                    }
                            ).show()
                        }
                    }.lparams(width = matchParent, height = wrapContent) {
                        topMargin = 5.toDp()
                        gravity = Gravity.CENTER
                    }

                    setupSettingsView.call(this)
                }
            }.view as T

    private fun <T : ViewGroup> initAccountsView(): T =
            activity.UI {
                verticalLayout {
                    lparams(matchParent, matchParent, 1f)
                    id = "accounts_container".toId()

                    listView {
                        visibility = View.GONE

                        id = "accounts_list_view".toId()

                        adapter = DeletableAdapter(accountList, Callable {
                            accountEventCallable.call(Result(AccountEvent.DELETE, it))
                        })

                        setOnItemClickListener { _, _, position, _ ->
                            accountEventCallable.call(Result(AccountEvent.RESTORE, position))
                        }
                    }.lparams(matchParent, matchParent)

                    errorView {
                        setErrorViewText(
                                this,
                                "Accounts Locked",
                                "Please enter your password to decrypt your Accounts",
                                "Unlock Accounts"
                        )
                    }

                    /*verticalLayout {
                        /**
                         * ===========================================================================
                         * Toolbar Content
                         * ===========================================================================
                         */
                        verticalLayout {
                            id = "toolbar_content".toId()
                            visibility = View.GONE

                            splitter(colorId = "primary").lparams(width = matchParent, height = 1.toDp())

                            themedButton(ResourceUtils.getStyle(activity, "ErrorButton")) {
                                text = "Safe Snapchat Logout"

                                setOnClickListener {
                                    val logoutResultCallable = Callable<Result<Boolean, String>> {
                                        if (it.key)
                                            SafeToastAdapter.showDefaultToast(activity, it.value)
                                        else
                                            SafeToastAdapter.showErrorToast(activity, it.value)
                                    }

                                    DialogFactory.createConfirmation(
                                            activity,
                                            "[IMPORTANT] Delete ChatMessage Database?",
                                            "Would you like to delete your SnapTools ChatMessage Database?"
                                                    + "\nThis option is provided as the Account Manager backs up " +
                                                    htmlHighlight("TODO: ") + " Build this message",
                                            object : ThemedDialog.ThemedClickListener() {
                                                override fun clicked(themedDialog: ThemedDialog?) {
                                                    themedDialog?.dismiss()
                                                    AccountManagerUtils.safeLogout(
                                                            activity,
                                                            true,
                                                            logoutResultCallable
                                                    )
                                                }
                                            },
                                            object : ThemedDialog.ThemedClickListener() {
                                                override fun clicked(themedDialog: ThemedDialog?) {
                                                    themedDialog?.dismiss()
                                                    AccountManagerUtils.safeLogout(
                                                            activity,
                                                            false,
                                                            logoutResultCallable
                                                    )
                                                }
                                            }
                                    ).show()
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                topMargin = 5.toDp()
                                gravity = Gravity.CENTER
                            }

                            themedButton(ResourceUtils.getStyle(activity, "ErrorButton")) {
                                text = "Backup Current Account"

                                setOnClickListener {
                                    managerCallable.call(AccountManagerEvent.BACKUP_ACCOUNT)
                                }
                            }.lparams(width = matchParent, height = wrapContent) {
                                topMargin = 5.toDp()
                                gravity = Gravity.CENTER
                            }

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
                            backgroundResource = FrameworkViewFactory.getSelectableBackgroundId(context)
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
                                imageResource = ResourceUtils.getDrawable(context, "dropdown_arrow")
                                padding = 13.toDp()
                                scaleType = ImageView.ScaleType.FIT_CENTER
                            }.lparams(40.toDp(), matchParent) {
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            textView("Toolbar") {
                                textColor = ContextCompat.getColor(context, ResourceUtils.getColor(context, "primaryLight"))
                                horizontalPadding = 10.toDp()
                                textSize = 14f
                                setTypeface(null, Typeface.BOLD)
                            }.lparams(width = matchParent, weight = 1f) {
                                gravity = Gravity.CENTER_VERTICAL
                            }
                        }.lparams(width = matchParent, height = 50.toDp())

                    }.lparams(width = matchParent) {
                        gravity = Gravity.BOTTOM
                    }*/

                    setupAccountsView.call(this)
                }
            }.view as T

    fun errorViewPasswordMode(errorView: ViewGroup) {
        setErrorViewText(
                errorView,
                "Invalid Decryption Password",
                "Decryption unsuccessful, password may be incorrect or account files have been tampered with",
                "Retry Credentials"
        )

        errorView.find<Button>("button_retry_credentials".toId())
                .visibility = View.VISIBLE
    }

    fun errorViewGeneralMode(errorView: ViewGroup, header: String, message: String) {
        setErrorViewText(errorView, header, message)

        errorView.find<Button>("button_retry_credentials".toId())
                .visibility = View.GONE
    }

    fun setErrorViewText(errorView: ViewGroup, header: String? = null, message: String? = null,
                         buttonText: String? = null) {
        if (header != null) {
            errorView.find<TextView>("error_view_header".toId())
                    .text = header
        }

        if (message != null) {
            errorView.find<TextView>("error_view_message".toId())
                    .text = message
        }

        if (buttonText != null) {
            val errorButton = errorView.find<TextView>("button_retry_credentials".toId())
            errorButton.text = buttonText
            errorButton.visibility = View.VISIBLE
        }
    }

    fun ViewGroup.errorView() = errorView { }
    fun ViewGroup.errorView(init: (@AnkoViewDslMarker ViewGroup).() -> Unit) =
            relativeLayout {
                lparams(matchParent, matchParent)
                id = "error_container".toId()

                verticalLayout {

                    imageView {
                        setImageResource(ResourceUtils.getDrawable(activity, "snaptools_logo"))
                        setColorFilter(
                                ContextCompat.getColor(activity, ResourceUtils.getColor(activity, "backgroundSecondary")),
                                PorterDuff.Mode.MULTIPLY
                        )
                    }.lparams(width = wrapContent, height = wrapContent) {
                        gravity = Gravity.CENTER
                    }

                    textView("#Placeholder#") {
                        id = "error_view_header".toId();

                        textColor = ContextCompat.getColor(
                                activity,
                                ResourceUtils.getColor(activity, "error")
                        )
                    }.lparams(width = wrapContent, height = wrapContent) {
                        gravity = Gravity.CENTER
                    }

                    textView("#Placeholder#") {
                        id = "error_view_message".toId()

                        textColor = ContextCompat.getColor(
                                activity,
                                ResourceUtils.getColor(activity, "textTertiary")
                        )
                        gravity = Gravity.CENTER
                    }.lparams(width = wrapContent, height = wrapContent) {
                        gravity = Gravity.CENTER
                    }

                    themedButton(ResourceUtils.getStyle(activity, "ErrorButton")) {
                        text = "Retry Credentials"
                        id = "button_retry_credentials".toId()

                        setOnClickListener {
                            managerCallable.call(AccountManagerEvent.RETRY_CREDENTIALS)
                        }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = 5.toDp()
                        gravity = Gravity.CENTER
                    }
                }.lparams {
                    centerInParent()
                }

                init()
            }

    class DeletableAdapter(val list: List<SnapchatAccountModel>, private val deleteItemCallable: Callable<Int>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return with(parent!!.context) {
                linearLayout {
                    val account = getItem(position)

                    lparams(matchParent, wrapContent)

                    verticalLayout {
                        padding = 10.toDp()

                        textView(account.decryptedIdentifier).lparams(matchParent, wrapContent) {
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        if (account.isMissingFile) {
                            textView("Missing Account File!") {
                                textColor = ContextCompat.getColor(
                                        context,
                                        ResourceUtils.getColor(context, "error")
                                )
                            }.lparams(matchParent, wrapContent)
                        }

                        if (!account.isEncrypted) {
                            textView("Unencrypted!") {
                                textColor = ContextCompat.getColor(
                                        context,
                                        ResourceUtils.getColor(context, "error")
                                )
                            }.lparams(matchParent, wrapContent)
                        }
                    }.lparams(matchParent, wrapContent, 1f) {
                        gravity = Gravity.CENTER_VERTICAL
                    }

                    imageView(ResourceUtils.getDrawable(context, "delete_96")) {
                        horizontalPadding = 5.toDp()

                        backgroundResource = FrameworkViewFactory.getSelectableBackgroundId(context)
                        setColorFilter(ContextCompat.getColor(context, ResourceUtils.getColor(context, "errorLight")))

                        setOnClickListener {
                            deleteItemCallable.call(position)
                            notifyDataSetChanged()
                        }
                    }.lparams(40.toDp(), matchParent) {
                        gravity = Gravity.CENTER_VERTICAL
                    }
                }
            }
        }

        override fun getItem(position: Int): SnapchatAccountModel = list[position]

        override fun getItemId(position: Int): Long = getItem(position).identifier.hashCode().toLong()

        override fun getCount(): Int = list.size
    }
}