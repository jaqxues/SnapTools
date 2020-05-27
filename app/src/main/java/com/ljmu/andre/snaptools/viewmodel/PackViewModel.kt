package com.ljmu.andre.snaptools.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaqxues.akrolyb.pack.PackMetadata
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket
import com.ljmu.andre.snaptools.Utils.Request
import com.ljmu.andre.snaptools.Utils.Result
import com.ljmu.andre.snaptools.repository.PackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 13.05.20 - Time 10:40.
 */
class PackViewModel : ViewModel() {
    val localMetadata get() = packRepo.localMetadata
    val eventDispatcher get() = packRepo.eventDispatcher
    val remoteMetadata get() = packRepo.remoteMetadata

    fun refreshLocalPacks(eventHandler: PackEventRequest.EventHandler) {
        viewModelScope.launch(Dispatchers.IO) {
            packRepo.refreshLocalMetadata(eventHandler)
        }
    }

    fun refreshRemotePacks(eventHandler: PackEventRequest.EventHandler, activity: Activity, invalidateCache: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            packRepo.refreshRemoteMetadata(eventHandler, activity, invalidateCache)
        }
    }

    fun setTutorialPacks() {
        packRepo.setTutorialPacks()
    }

    fun clearRemotePacks() {
        packRepo.clearRemote()
    }

    fun clearLocalPacks() {
        packRepo.clearLocal()
    }

    fun <T: PackMetaData> getInfoFromName(packName: String, packs: List<T>?): Pair<T, Int>? {
        return packs?.find { it.name == packName }?.let {
            it to packs.indexOf(it)
        }
    }

    fun getServerPack(packName: String): ServerPackMetaData? {
        val req = remoteMetadata.value

        if (!(req is Request.Loaded && req.result is Result.Success)) {
            Timber.w("Packs have not been loaded successfully")
            return null
        }

        return getInfoFromName(packName, req.result.data)?.first
    }

    fun requestChangeLog(activity: Activity, pack: ServerPackMetaData): LiveData<Request<PackDataPacket>> {
        val liveData = MutableLiveData<Request<PackDataPacket>>()
        liveData.value = Request.Pending
        viewModelScope.launch(Dispatchers.IO) {
            packRepo.getChangelog(activity, pack, liveData)
        }
        return liveData
    }

    fun enablePack(packName: String, activity: Activity): LiveData<Result<String>> {
        val liveData = MutableLiveData<Result<String>>()
        viewModelScope.launch(Dispatchers.Default) {
            liveData.postValue(
                    try {
                        packRepo.enablePack(packName, activity)
                        Result.Success(packName)
                    } catch (e: Exception) {
                        Timber.e(e, "Could not load Pack")
                        Result.Error(e)
                    })
        }
        return liveData
    }

    fun unloadPack(packName: String, activity: Activity) = packRepo.unloadPack(packName, activity)

    fun deletePack(packName: String, activity: Activity, evtHandler: PackEventRequest.EventHandler) = packRepo.deletePack(packName, activity, evtHandler)

    fun downloadPack() {
        viewModelScope.launch(Dispatchers.IO) {
            packRepo.downloadPack()
        }
    }

    companion object {
        private val packRepo by lazy { PackRepository() }
    }
}