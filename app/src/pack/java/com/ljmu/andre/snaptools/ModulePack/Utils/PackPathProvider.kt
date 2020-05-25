package com.ljmu.andre.snaptools.ModulePack.Utils

import com.jaqxues.akrolyb.prefs.getPref
import com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CUSTOM_MEDIA_PATH
import com.ljmu.andre.snaptools.Utils.PathProvider


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 25.05.20 - Time 14:59.
 */
object PackPathProvider {
    @JvmStatic
    fun getMediaPath() = CUSTOM_MEDIA_PATH.getPref() ?: PathProvider.getContentPath() + "Media/"
    @JvmStatic
    fun getFiltersPath() = PathProvider.getContentPath() + "Filters/"
    @JvmStatic
    fun getFontsPath() = PathProvider.getContentPath() + "Fonts/"
    @JvmStatic
    fun getChatExportPath() = PathProvider.getContentPath() + "ExportedChats/"
    @JvmStatic
    fun getAccountsPath() = PathProvider.getContentPath() + "/Accounts"
}
