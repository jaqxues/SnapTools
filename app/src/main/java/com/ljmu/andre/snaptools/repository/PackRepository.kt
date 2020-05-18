package com.ljmu.andre.snaptools.repository

import android.app.Activity
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ljmu.andre.GsonPreferences.Preferences.getPref
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest
import com.ljmu.andre.snaptools.EventBus.Events.PackUnloadEvent
import com.ljmu.andre.snaptools.Framework.FrameworkManager
import com.ljmu.andre.snaptools.Framework.MetaData.FailedPackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.MODULES_PATH
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS
import com.ljmu.andre.snaptools.Utils.PackUtils.getPackMetaData
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers
import com.ljmu.andre.snaptools.Utils.Result
import timber.log.Timber
import java.io.File


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 13.05.20 - Time 10:01.
 */
class PackRepository {
    // Private Mutable LiveData
    private val _eventDispatcher = MutableLiveData<Any>()
    private val _localMetadata = MutableLiveData<List<LocalPackMetaData>>()
    // Exposed public LiveData
    val eventDispatcher: LiveData<Any> = _eventDispatcher
    val localMetadata: LiveData<List<LocalPackMetaData>> = _localMetadata

    private val packDirectory = File(getPref(MODULES_PATH) as String)

    fun refreshLocalMetadata(evtHandler: PackEventRequest.EventHandler) {
        val jarFileList = packDirectory.listFiles { _, name -> name.endsWith(".jar") }
        if (jarFileList.isNullOrEmpty()) {
            _localMetadata.postValue(emptyList())
            return
        }

        val packs = jarFileList.associate { file ->
            val metadata = try {
                getPackMetaData(file, evtHandler)
            } catch (t: Throwable) {
                FailedPackMetaData(evtHandler).run {
                    reason = t.message
                    name = file.name.replace(".jar", "")
                    completedBinding()
                }
            }
            metadata.name to metadata
        }

        val packsList = packs.values.toMutableList()

        // Selected Packs that have been deleted or cannot be found for some reason
        getPref<Set<String>>(SELECTED_PACKS).forEach { packName ->
            if (packName in packs) return@forEach

            val failedMetadata = FailedPackMetaData(evtHandler).run {
                reason = "Pack is enabled but cannot be found in the installed list"
                name = packName
                completedBinding()
            }
            packsList.add(failedMetadata)
        }

        _localMetadata.postValue(packsList.sorted())
    }

    fun unloadPack(packName: String, activity: Activity): Result<String> {
        val modPack = FrameworkManager.getModulePack(packName)
        if (modPack == null) {
            val e = IllegalStateException("Pack was not loaded. Could not unload it")
            Timber.e(e)
            return Result.Error(e)
        }
        if (FrameworkManager.unloadModPack(packName)) {
            _eventDispatcher.value = PackUnloadEvent(modPack.packMetaData)
            return Result.Success(packName)
        }
        return Result.Error(Exception("Unknown Exception while trying to unload the Pack $packName"))
    }

    fun disablePack(packName: String, activity: Activity): Result<String> {
        val result = unloadPack(packName, activity)
        if (result is Result.Error)
            return result
        FrameworkManager.disableModPack(packName)
        return Result.Success(packName)
    }

    /**
     * Unloads, disables and deletes the pack
     */
    fun deletePack(packName: String, activity: Activity, evtHandler: PackEventRequest.EventHandler): Result<String> {
        val result = disablePack(packName, activity)
        if (result is Result.Error)
            return result
        FrameworkManager.deleteModPack(packName, activity)

        if (PreferenceHelpers.collectionContains(SELECTED_PACKS, packName)) {
            val e = IllegalStateException("Failed to delete Pack $packName")
            Timber.e(e)
            return Result.Error(e)
        }
        _eventDispatcher.value = PackDeleteEvent(packName)
        refreshLocalMetadata(evtHandler)
        return Result.Success(packName)
    }

    /**
     * Download Pack and update LiveData
     */
    fun downloadPack() {
    }

    fun setTutorialPacks() {
        _localMetadata.value =
                listOf("10.0.0.0", "10.1.0.1", "10.12.1.0", "10.16.0.0").map {
                    LocalPackMetaData.getTutorialPack(it)
                }
    }

    fun clear() {
        _localMetadata.value = emptyList()
    }

    @WorkerThread
    fun enablePack(packName: String, activity: Activity) {
        PreferenceHelpers.addToCollection(SELECTED_PACKS, packName, activity)
        val packLoadEvent = FrameworkManager.loadModPack(activity, packName, PackLoadState(packName))
        _eventDispatcher.postValue(packLoadEvent)
    }
}