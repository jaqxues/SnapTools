package com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.common.io.Files
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.customTabStrip
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.headerNoUnderline
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.label
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSeekBar
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.labelledSpinner
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.splitter
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.CustomViews.Companion.wrapContentViewPager
import com.ljmu.andre.snaptools.ModulePack.Notifications.DotNotification
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification
import com.ljmu.andre.snaptools.ModulePack.Notifications.StackingDotNotification
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingModeHelper.getSavingModeForType
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SavingTriggers.SavingTrigger
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapTypeDef
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormat
import com.ljmu.andre.snaptools.ModulePack.Utils.*
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toDp
import com.ljmu.andre.snaptools.ModulePack.Utils.KotlinUtils.Companion.toId
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.*
import com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.*
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.assignItemChangedProvider
import com.ljmu.andre.snaptools.Utils.*
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.addToMap
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill
import com.ljmu.andre.snaptools.Utils.ResourceUtils.*
import com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import org.jetbrains.anko.*
import timber.log.Timber
import java.io.File

@Suppress("UNCHECKED_CAST", "DEPRECATION")
/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
class SavingViewProvider(val activity: Activity) {

    @SuppressLint("ResourceType")
    fun <T : ViewGroup> getMainContainer(): T =
            activity.UI {
                scrollView {
                    verticalLayout {
                        isFocusable = true
                        isFocusableInTouchMode = true
                        requestFocus()
                        requestFocusFromTouch()

                        savingHeader()

                        splitter().lparams(width = matchParent, height = 1.toDp()) {
                            horizontalMargin = 16.toDp()
                            bottomMargin = 14.toDp()
                        }

                        notificationSettings()

                        splitter().lparams(width = matchParent, height = 1.toDp()) {
                            verticalMargin = 14.toDp()
                            horizontalMargin = 16.toDp()
                        }

                        mediaStorageSettings()

                        splitter().lparams(width = matchParent, height = 1.toDp()) {
                            verticalMargin = 14.toDp()
                            horizontalMargin = 16.toDp()
                        }

                        folderNameSettings()
                    }
                }
            }.view as T


    /**
     * ===========================================================================
     * SAVING HEADER
     * ===========================================================================
     */
    private fun ViewGroup.savingHeader() {
        verticalLayout {
            id = "saving_settings_header".toId()

            val tabStrip = customTabStrip {
                id = getIdFromString("saving_settings_tab")
            }

            verticalLayout {
                val viewPager: WrapContentViewPager = wrapContentViewPager {
                    id = getIdFromString("view_pager")
                }.lparams(width = matchParent, height = wrapContent)

                setupSavingSettings(viewPager)

                tabStrip.setupWithViewPager(viewPager)
            }
        }
    }

    private fun setupSavingSettings(viewPager: WrapContentViewPager) {
        val viewList = SnapTypeDef.INST.values()
                .filter { it !== SnapTypeDef.SENT }
                .mapTo(ArrayList<Pair<String, View>>())
                { Pair.create(it.name, getSavingSettingsLayout(it)) }

        viewList.add(Pair.create(SnapTypeDef.SENT.name, getSavingSentSettings()))

        viewPager.adapter = ListedViewPageAdapter(
                viewList
        )

    }

    private fun <T : View> getSavingSentSettings(): T =
            activity.UI {
                themedSwitchCompatX(getStyle(activity, "DefaultSwitch")) {
                    verticalPadding = 26.toDp()
                    horizontalPadding = 26.toDp()
                    text = "Save Sent Snaps"
                    isChecked = getPref(SAVE_SENT_SNAPS)

                    setOnCheckedChangeListener { _, isChecked ->
                        putAndKill(SAVE_SENT_SNAPS, isChecked, activity)
                    }
                }
            }.view as T

    private fun <T : ViewGroup> getSavingSettingsLayout(snapType: SnapType): T =
            activity.UI {
                verticalLayout {
                    horizontalPadding = 16.toDp()
                    verticalPadding = 26.toDp()

                    lparams(width = matchParent, height = wrapContent)
                    val currentType = getSavingModeForType(snapType)

                    val savingModes: ArrayList<String> = SavingTrigger.SavingMode.values()
                            .mapTo(ArrayList()) { it.displayName }
                    val currentIndex = savingModes.indexOf(currentType.displayName)

                    linearLayout {
                        id = "saving_setting_spinner_container".toId()

                        label("Saving Mode: ").lparams(width = matchParent, height = wrapContent) {
                            weight = 1f
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        themedSpinner {
                            adapter = ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_dropdown_item, savingModes)
                            setSelection(currentIndex, false)

                            assignItemChangedProvider(
                                    this,
                                    ViewFactory.OnItemChangedProvider<String>(
                                            { newItem, _, _ ->
                                                addToMap(SAVING_MODES, snapType.name, newItem, activity)

                                                Timber.d("Searching for ID: " + getIdFromString("saving_mode_container_" + snapType.name))
                                                val savingModeContainer: ViewGroup? = activity.findOptional(getIdFromString("saving_mode_container_" + snapType.name))

                                                if (savingModeContainer == null) {
                                                    Timber.e("Null SavingMode Container")
                                                    return@OnItemChangedProvider
                                                }

                                                savingModeContainer.removeAllViews()
                                                savingModeContainer.settingsView(snapType, SavingTrigger.SavingMode.fromNameOptional(newItem))

                                                if (Constants.getApkVersionCode() >= 66)
                                                    AnimationUtils.sequentGroup(savingModeContainer)
                                            },
                                            { getSavingModeForType(snapType).name }
                                    )
                            )
                        }.lparams(width = matchParent) {
                            gravity = Gravity.CENTER_VERTICAL
                            weight = 1f
                        }
                    }.lparams(width = matchParent)

                    verticalLayout {
                        id = getIdFromString("saving_mode_container_" + snapType.name)
                        Timber.d("Set ID to: " + id)
                        settingsView(snapType, currentType)
                    }.lparams(width = matchParent)
                }
            }.view as T

    private fun ViewGroup.settingsView(snapType: SnapType, savingMode: SavingTrigger.SavingMode) =
            when (savingMode) {
                SavingTrigger.SavingMode.BUTTON -> buttonSettings(snapType)
                SavingTrigger.SavingMode.FLING_TO_SAVE -> flingSettings(snapType)
                else -> frameLayout()
            }

    private fun ViewGroup.buttonSettings(snapType: SnapType) =
            verticalLayout {
                lparams(width = matchParent)

                val buttonId = ("img_save_button_preview_" + snapType.name).toId()
                val initialLocation = getButtonLocation(snapType)
                val buttonLocations: ArrayList<String> = SavingButton.ButtonLocation.values().mapTo(ArrayList()) { it.displayText }

                labelledSpinner("Location: ", initialLocation.displayText, buttonLocations,
                        ViewFactory.OnItemChangedProvider<String>(
                                { newItem, _, _ -> addToMap(SAVE_BUTTON_LOCATIONS, snapType.name, newItem, activity) },
                                { getButtonLocation(snapType).displayText }
                        ))

                labelledSeekBar(
                        text = "Opacity (%s%%)",
                        progress = getButtonOpacity(snapType),
                        progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                            activity.find<ImageView>(
                                    ("img_save_button_preview_" + snapType.name).toId()
                            ).alpha = progress.toFloat() / 100f
                        },
                        resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                            addToMap(SAVE_BUTTON_OPACITIES, snapType.name, progress, activity)
                        }
                )

                labelledSeekBar(
                        text = "Width (%s%%)",
                        max = 50,
                        progress = getButtonWidth(snapType),
                        progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                            val size = SavingButton.getButtonSize(context, progress, getButtonHeightAspect(snapType))
                            activity.find<ImageView>(
                                    ("img_save_button_preview_" + snapType.name).toId()
                            ).lparams(width = size.first.toInt(), height = size.second.toInt()) {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }
                        },
                        resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                            addToMap(SAVE_BUTTON_WIDTHS, snapType.name, progress, activity)
                        }
                )

                labelledSeekBar(
                        text = "H Aspect (%s%%)",
                        max = 150,
                        min = 50,
                        progress = getButtonHeightAspect(snapType),
                        progressListener = ViewFactory.OnSeekBarProgress { _, progress ->
                            val size = SavingButton.getButtonSize(context, getButtonWidth(snapType), progress)
                            activity.find<ImageView>(
                                    ("img_save_button_preview_" + snapType.name).toId()
                            ).lparams(width = size.first.toInt(), height = size.second.toInt()) {
                                gravity = Gravity.CENTER_HORIZONTAL
                            }
                        },
                        resultListener = ViewFactory.OnSeekBarResult { _, progress ->
                            addToMap(SAVE_BUTTON_RELATIVE_HEIGHTS, snapType.name, progress, activity)
                        }
                )

                themedSwitchCompatX(getStyle(context, "DefaultSwitch")) {
                    padding = 10.toDp()
                    text = "Preview Button"
                    isChecked = true

                    setOnCheckedChangeListener { _, isChecked ->
                        activity.find<ImageView>(("img_save_button_preview_" + snapType.name).toId()).visibility =
                                if (isChecked) View.VISIBLE else View.GONE
                    }
                }.lparams(width = matchParent)

                val size = SavingButton.getButtonSize(context, getButtonWidth(snapType), getButtonHeightAspect(snapType))
                imageView {
                    id = buttonId
                    padding = 5.toDp()
                    setBackgroundResource(getDrawable(context, "save_256"))

                    val opacityPref = getButtonOpacity(snapType)
                    alpha = opacityPref.toFloat() / 100f
                    Timber.d("Alpha: " + alpha)
                }.lparams(width = size.first.toInt(), height = size.second.toInt()) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }

    private fun ViewGroup.flingSettings(snapType: SnapType) =
            verticalLayout {
                lparams(width = matchParent)

                linearLayout {
                    label("Velocity").lparams(width = matchParent, weight = 1f)
                    label(getFlingVelocity(snapType).toString() + "px/s") {
                        id = getIdFromString("label_velocity_display")
                    }.lparams(width = matchParent, weight = 1f)
                }.lparams(width = matchParent) {
                    bottomMargin = 10.toDp()
                }

                themedButton(getStyle(context, "NeutralButton")) {
                    text = "Open Velocity Threshold Picker"

                    setOnClickListener {
                        ThemedDialog(activity)
                                .setTitle("Velocity Threshold Picker")
                                .setExtension(
                                        FlingVelocityDialog()
                                                .setMessage("Perform a fling to generate a velocity")
                                                .setCallable { integer ->
                                                    Timber.d("Velocity: " + integer!!)

                                                    addToMap(
                                                            FLING_VELOCITY,
                                                            snapType.name,
                                                            integer,
                                                            activity
                                                    )

                                                    activity.find<TextView>(
                                                            getIdFromString("label_velocity_display")
                                                    ).text = integer.toString() + "px/s"
                                                }
                                ).show()
                    }
                }
            }

    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    /**
     * ===========================================================================
     * Notification Settings
     * ===========================================================================
     */
    private fun ViewGroup.notificationSettings() =
            verticalLayout {
                id = "notification_settings_container".toId()

                lparams(width = matchParent)
                padding = 16.toDp()

                headerNoUnderline("Save Notification Settings", Gravity.CENTER)

                val initialType: String = getPref(SAVE_NOTIFICATION_TYPE)
                val types: ArrayList<String> = SaveNotification.NotificationType.values().mapTo(ArrayList()) { it.displayText }

                labelledSpinner("Notification Type: ", initialType, types,
                        ViewFactory.OnItemChangedProvider<String>(
                                { newItem, _, _ ->
                                    putAndKill(SAVE_NOTIFICATION_TYPE, newItem, activity)
                                    val container = activity.find<ViewGroup>("container_notification_settings".toId())
                                    container.removeAllViews()
                                    container.refreshNotificationSettings()

                                    if (newItem == SaveNotification.NotificationType.LED.displayText)
                                        showLEDNotificationWarning()
                                },
                                { getPref(SAVE_NOTIFICATION_TYPE) }
                        ))

                verticalLayout {
                    id = "container_notification_settings".toId()

                    refreshNotificationSettings()
                }

                /*val initialLocation: String = getPref(DOT_LOCATION)
                val locations: ArrayList<String> = DotNotification.DotLocation.values().mapTo(ArrayList()) { it.displayText }

                labelledSpinner("Location: ", initialLocation, locations,
                        ViewFactory.OnItemChangedProvider<String>(
                                { newItem, _, _ -> putAndKill(DOT_LOCATION, newItem, activity) },
                                { getPref(DOT_LOCATION) }
                        ))

                val initialOrientation: String = getPref(STACKED_ORIENTATION)
                val orientations: ArrayList<String> = StackingDotNotification.StackingOrientation.values().mapTo(ArrayList()) { it.displayText }

                labelledSpinner("Orientation: ", initialOrientation, orientations,
                        ViewFactory.OnItemChangedProvider<String>(
                                { newItem, _, _ -> putAndKill(STACKED_ORIENTATION, newItem, activity) },
                                { getPref(STACKED_ORIENTATION) }
                        ))*/

                themedSwitchCompatX(getStyle(context, "DefaultSwitch")) {
                    padding = 10.toDp()
                    text = "Vibrate On Save"
                    isChecked = getPref(VIBRATE_ON_SAVE)

                    setOnCheckedChangeListener { _, isChecked ->
                        putAndKill(VIBRATE_ON_SAVE, isChecked, activity)
                    }
                }.lparams(width = matchParent)
            }

    private fun ViewGroup.refreshNotificationSettings() {
        val currentType = getPref<String>(SAVE_NOTIFICATION_TYPE)

        when (currentType) {
            SaveNotification.NotificationType.DOT.displayText -> getDotSettings()
            SaveNotification.NotificationType.STACKING_DOTS.displayText -> getStackedDotSettings()
            SaveNotification.NotificationType.LED.displayText -> getLEDSettings()
            else -> frameLayout()
        }
    }

    private fun ViewGroup.getDotSettings() {
        val initialLocation: String = getPref(DOT_LOCATION)
        val locations: ArrayList<String> = DotNotification.DotLocation.values().mapTo(ArrayList()) { it.displayText }

        labelledSpinner("Location: ", initialLocation, locations,
                ViewFactory.OnItemChangedProvider<String>(
                        { newItem, _, _ -> putAndKill(DOT_LOCATION, newItem, activity) },
                        { getPref(DOT_LOCATION) }
                ))
    }

    private fun ViewGroup.getStackedDotSettings() {
        getDotSettings()

        val initialOrientation: String = getPref(STACKED_ORIENTATION)
        val orientations: ArrayList<String> = StackingDotNotification.StackingOrientation.values().mapTo(ArrayList()) { it.displayText }

        labelledSpinner("Orientation: ", initialOrientation, orientations,
                ViewFactory.OnItemChangedProvider<String>(
                        { newItem, _, _ -> putAndKill(STACKED_ORIENTATION, newItem, activity) },
                        { getPref(STACKED_ORIENTATION) }
                ))
    }

    private fun ViewGroup.getLEDSettings() =
            themedButton(getStyle(context, "NeutralButton")) {
                text = "Test LED Flash"

                setOnClickListener {
                    SafeToastAdapter.showDefaultToast(activity, "Triggering LED Flash")
                    NotificationLEDUtil.flashLED(NotificationLEDUtil.NotificationColor.RED)
                }
            }

    private fun showLEDNotificationWarning() {
        DialogFactory.createBasicMessage(
                activity,
                "Warning",
                "Not all devices support our LED notification implementation. If it doesn't show for you, then use an alternative Save Notification option."
                        + "\nAdditionally, this method " + htmlHighlight("REQUIRES") + " root access. Furthermore, MagiskHide breaks this. Disable MagiskHide for Snapchat."
                        + "\nTest for root access?",
                object : ThemedDialog.ThemedClickListener() {
                    override fun clicked(themedDialog: ThemedDialog) {
                        themedDialog.dismiss()

                        val commandObservable = ShellUtils.sendCommand("")

                        commandObservable
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : CustomObservers.SimpleObserver<Boolean>() {
                                    override fun onNext(aBoolean: Boolean) {
                                        if (aBoolean)
                                            SafeToast.show(activity, "Root Access Confirmed", Toast.LENGTH_SHORT)
                                        else
                                            DialogFactory.createBasicMessage(activity, "Root Access", "Root Access was not successfully obtained. LED Notification type will not work.").show()
                                    }

                                    override fun onError(@NonNull e: Throwable) {
                                        Timber.e(e)
                                        DialogFactory.createBasicMessage(activity, "Root Access", "Root Access was not successfully obtained. LED Notification type will not work.").show()
                                    }
                                })
                    }
                }).show()
    }

    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    /**
     * ===========================================================================
     * Media Storage Format Settings
     * ===========================================================================
     */
    private fun ViewGroup.mediaStorageSettings() =
            verticalLayout {
                id = "media_storage_container".toId()

                lparams(width = matchParent)
                padding = 16.toDp()

                headerNoUnderline("Media Storage Format", Gravity.CENTER)

                val initialType: String = getPref(STORAGE_FORMAT)
                val types: ArrayList<String> = ArrayList(StorageFormat.getMapTypes())
                val currentIndex = types.indexOf(initialType)

                themedSpinner {
                    id = "media_storage_spinner".toId()

                    var isInternalCall = false

                    adapter = ArrayAdapter<String>(context,
                            R.layout.simple_spinner_dropdown_item, types)
                    setSelection(currentIndex, false)

                    assignItemChangedProvider(
                            this,
                            ViewFactory.OnItemChangedProvider<String>(
                                    { newItem, _, _ ->
                                        if (isInternalCall) {
                                            isInternalCall = false
                                            return@OnItemChangedProvider
                                        }

                                        // Confirmation Dialog =======================================================
                                        DialogFactory.createConfirmation(
                                                activity,
                                                "Confirm Storage Format Conversion",
                                                "Are you sure you wish to proceed with the conversion?" +
                                                        "\n\nIt can take up to a minute to complete",

                                                // Yes Click Event ===========================================================
                                                object : ThemedDialog.ThemedClickListener() {
                                                    override fun clicked(themedDialog: ThemedDialog) {
                                                        StorageFormat.changeStorageFormat(newItem, activity)
                                                        themedDialog.dismiss()
                                                    }
                                                },

                                                // No Click Event ============================================================
                                                object : ThemedDialog.ThemedClickListener() {
                                                    override fun clicked(themedDialog: ThemedDialog) {
                                                        //noinspection SuspiciousMethodCalls
                                                        val oldIndex: Int = types.indexOf(getPref(STORAGE_FORMAT))

                                                        // As setSelection will cause this entire method to re-run
                                                        // We set a boolean to ignore the next event
                                                        isInternalCall = true
                                                        setSelection(oldIndex, false)

                                                        themedDialog.dismiss()
                                                    }
                                                }
                                        ).setDismissable(false).show()
                                    },
                                    { getPref(STORAGE_FORMAT) }
                            )
                    )
                }.lparams(width = matchParent, weight = 1f) {
                    gravity = Gravity.CENTER_VERTICAL
                }

                themedButton(getStyle(context, "NeutralButton")) {
                    id = "button_media_path".toId()
                    text = "Select Media Storage Path"
                }
            }

    // ===========================================================================
    // ===========================================================================
    // ===========================================================================


    /**
     * ===========================================================================
     * Folder Name Settings
     * ===========================================================================
     */
    private fun ViewGroup.folderNameSettings() =
            verticalLayout {
                id = "folder_names_container".toId()

                lparams(width = matchParent)
                padding = 16.toDp()

                headerNoUnderline("Snap Folder Names", Gravity.CENTER)

                val alteredFileNames = HashMap<SnapType, String>()
                for (snapType: SnapType in SnapTypeDef.INST.values()) {
                    linearLayout {
                        label(snapType.name!!).lparams(width = matchParent, weight = 2f) {
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        themedEditText {
                            setTextAppearance(context, getStyle(context, "DefaultText"))
                            setText(snapType.folderName)
                            setSingleLine()
                            textSize = 16f
                            leftPadding = 10.toDp()

                            addTextChangedListener(object : ViewFactory.EditTextListener() {
                                override fun textChanged(source: EditText?, editable: Editable?) {
                                    alteredFileNames.put(snapType, editable.toString())
                                    activity.find<Button>("button_apply_folder_names".toId()).visibility = View.VISIBLE
                                }
                            })
                        }.lparams(width = matchParent, weight = 1f) {
                            gravity = Gravity.CENTER_VERTICAL
                        }
                    }
                }

                themedButton(getStyle(context, "NeutralButton")) {
                    id = "button_apply_folder_names".toId()
                    text = "Apply Folder Names"
                    visibility = View.GONE

                    setOnClickListener {

                        val storageFormat = StorageFormat.getAppropriateFormat()

                        for (entry in alteredFileNames.entries) {
                            val snapType = entry.key
                            val newFolderName = entry.value

                            val folders = storageFormat.getSnapTypeFolders(snapType)

                            for (snapTypeDir in folders) {
                                val snapTypeParentDir = snapTypeDir.parentFile
                                val newSnapTypeDir = File(
                                        snapTypeParentDir,
                                        newFolderName
                                )

                                val iterable = Files.fileTraverser().depthFirstPreOrder(snapTypeDir)

                                for (mediaFile in iterable) {
                                    if (mediaFile.isDirectory)
                                        continue

                                    val mediaType = storageFormat.getSnapTypeFromFile(mediaFile)

                                    if (mediaType !== snapType)
                                        continue

                                    val filePath = mediaFile.absolutePath
                                    val strippedPath = filePath.replace(snapTypeDir.absolutePath, "")
                                    Timber.d("Stripped Path: " + strippedPath)
                                    val newMediaFile = File(
                                            newSnapTypeDir,
                                            strippedPath
                                    )

                                    newMediaFile.parentFile.mkdirs()
                                    mediaFile.renameTo(newMediaFile)
                                    Timber.d("Renaming %s to %s", mediaFile, newMediaFile)
                                }

                                if (FileUtils.isDirEmpty(snapTypeDir)) {
                                    Timber.d("Deleting dir: " + snapTypeDir)
                                    FileUtils.deleteEmptyFolders(snapTypeDir)
                                    snapTypeDir.delete()
                                }
                            }

                            putAndKill(
                                    snapType.preference,
                                    newFolderName,
                                    activity
                            )

                            visibility = View.GONE
                        }

                        SafeToast.show(activity, "Completed folder renaming process", Toast.LENGTH_LONG)
                    }
                }
            }
}