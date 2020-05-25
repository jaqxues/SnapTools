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

public class AllSnaps extends StorageFormat {
    @Override
    public List<File> getSnapTypeFolders(SnapType snapType) {
        List<File> typeFolderList = new ArrayList<>();
        typeFolderList.add(
                new File(PackPathProvider.getMediaPath())
        );

        return typeFolderList;
    }

    @Override
    public File getOutputFile(SnapType snapType, String username, String filename) {
        File parentDir = new File(PackPathProvider.getMediaPath());

        //noinspection ResultOfMethodCallIgnored
        parentDir.mkdirs();

        return new File(
                parentDir,
                filename
        );
    }

    @Override
    public boolean snapUsesThisFormat(File snapFile, SnapType snapType) {
        File mediaFile = snapFile.getParentFile();

        return mediaFile.getName().contains("Media") &&
                mediaFile.getParentFile().getName().contains("SnapTools");
    }
}
