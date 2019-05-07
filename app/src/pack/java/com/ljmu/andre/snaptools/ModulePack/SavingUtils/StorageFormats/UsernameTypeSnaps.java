package com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.MEDIA_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class UsernameTypeSnaps extends StorageFormat {

    @Override
    public List<File> getSnapTypeFolders(SnapType snapType) {
        File mediaDir = new File(
                (String) getPref(MEDIA_PATH)
        );

        File[] userFolders = mediaDir.listFiles(File::isDirectory);

        if (userFolders == null || userFolders.length <= 0)
            return new ArrayList<>();

        List<File> typeFolderList = new ArrayList<>();

        for (File userFolder : userFolders) {
            File snapTypeFolder = new File(
                    userFolder,
                    snapType.getFolderName()
            );

            if (snapTypeFolder.exists())
                typeFolderList.add(snapTypeFolder);
        }

        return typeFolderList;
    }

    /**
     * ===========================================================================
     * Build an appropriate outputfile based on this StorageFormat
     * ===========================================================================
     */
    @Override
    public File getOutputFile(SnapType snapType, String username, String filename) {
        File parentDir = new File(
                getPref(MEDIA_PATH)
                        + File.separator
                        + username
                        + File.separator
                        + snapType.getFolderName()
        );

        //noinspection ResultOfMethodCallIgnored
        parentDir.mkdirs();

        return new File(
                parentDir,
                filename
        );
    }

    public boolean snapUsesThisFormat(File snapFile, SnapType snapType) {

        File snapTypeDir = snapFile.getParentFile();
        boolean namesMatch = snapTypeDir.getName().equals(snapType.getFolderName());
        boolean hierarchyMatch = snapFile.getParentFile().getParentFile().getParentFile().getName().contains("Media");

        return namesMatch && hierarchyMatch;
    }
}