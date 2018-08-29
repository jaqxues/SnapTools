package com.ljmu.andre.snaptools.Utils;

import com.google.common.io.Files;
import com.ljmu.andre.GsonPreferences.Preferences;
import timber.log.Timber;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.BACKUP_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CONTENT_PATH;
import static com.ljmu.andre.snaptools.Utils.StringUtils.yyyyMMddHHmmss;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BackupRestoreUtils {
    public static String getFreshFilenameNoExt() {
        return yyyyMMddHHmmss.format(new Date(System.currentTimeMillis()));
    }

    public static String createFilename(String profileName) {
        return "SettingsBackup_" + profileName + ".json";
    }

    public static boolean backupCurrentProfile(String profileName) {
        File currentPrefFile = new File((String) getPref(CONTENT_PATH), Preferences.getPreferenceFilename());
        if (!currentPrefFile.exists()) {
            Timber.w("Preference file doesn't exist?");
            return false;
        }

        File backupFile = getBackupFile(profileName);
        try {
            //noinspection ResultOfMethodCallIgnored
            backupFile.createNewFile();

            Files.copy(
                    currentPrefFile,
                    backupFile
            );

            return true;
        } catch (IOException e) {
            Timber.e(e);
        }

        return false;
    }

    private static File getBackupFile(String profileName) {
        File backupDir = getCreateDir(BACKUP_PATH);
        if (backupDir == null || !backupDir.exists()) {
            Timber.w("Backup dir not created");
            return null;
        }

        return new File(backupDir, createFilename(profileName));
    }

    public static boolean restoreProfile(String profileName) {
        File backupFile = getBackupFile(profileName);

        if (backupFile == null || !backupFile.exists()) {
            Timber.w("Restore file doesn't exist");
            return false;
        }


        File currentPrefFile = new File((String) getPref(CONTENT_PATH), Preferences.getPreferenceFilename());

        try {
            if (!currentPrefFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                currentPrefFile.createNewFile();
            }

            Files.copy(
                    backupFile,
                    currentPrefFile
            );

            return true;
        } catch (IOException e) {
            Timber.e(e);
        }

        return false;
    }
}
