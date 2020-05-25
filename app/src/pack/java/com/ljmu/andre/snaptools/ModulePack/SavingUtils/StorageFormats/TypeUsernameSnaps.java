package com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormat;
import com.ljmu.andre.snaptools.ModulePack.Utils.PackPathProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TypeUsernameSnaps extends StorageFormat {

    @Override
    public List<File> getSnapTypeFolders(SnapType snapType) {
        List<File> typeFolderList = new ArrayList<>();
        typeFolderList.add(
                new File(PackPathProvider.getMediaPath(), snapType.getFolderName())
        );

        return typeFolderList;
    }

    /**
     * ===========================================================================
     * Build an appropriate outputfile based on this StorageFormat
     * ===========================================================================
     */
    @Override
    public File getOutputFile(SnapType snapType, String username, String filename) {
        File parentDir = new File(PackPathProvider.getMediaPath(),
                snapType.getFolderName()
                        + File.separator
                        + username
        );

        //noinspection ResultOfMethodCallIgnored
        parentDir.mkdirs();

        return new File(
                parentDir,
                filename
        );
    }

    /**
     * ===========================================================================
     * Attempt to determine if a file was using the
     * {@link #getOutputFile(SnapType, String, String)} from this StorageFormat
     * <p>
     * The method used isn't efficient or elegant however it works for now
     * ===========================================================================
     */
    @Override
    public boolean snapUsesThisFormat(File snapFile, SnapType snapType) {
        // If getSnapTypeFromFile returns a SnapType, we can be sure it's the right format
        File usernameDir = snapFile.getParentFile();
        File snapTypeDir = usernameDir.getParentFile();

        return snapTypeDir.getName().equals(snapType.getFolderName());
    }
}