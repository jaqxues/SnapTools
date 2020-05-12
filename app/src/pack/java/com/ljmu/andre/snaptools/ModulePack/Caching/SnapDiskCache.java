package com.ljmu.andre.snaptools.ModulePack.Caching;

import androidx.annotation.NonNull;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SaveNotification.ToastType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.Utils.CustomObservers.ErrorObserver;
import com.ljmu.andre.snaptools.Utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.TEMP_PATH;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SnapDiskCache {
    private static final String TEMP_SNAP_PREFIX = "STMedia";
    private static final Object MAP_LOCK = new Object();
    private static SnapDiskCache instance;
    private final Map<String, List<SaveableFile>> snapFileListMap = new HashMap<>();

    public void writeToCache(Snap snap, ByteArrayOutputStream byteArrayOutput) throws IOException {
        SaveableFile tempSnapFile;

        if (!snap.isVideo()) {
            Timber.d("Snap not video, inserting into list");
            tempSnapFile = createSnapFileDetails(snap.getKey(), snap.getFileExtension(), byteArrayOutput.size());

            insertSnapFileDetails(snap, byteArrayOutput.size(), tempSnapFile);
        } else {
            Timber.d("Snap video, creating ghostly temp file");
            tempSnapFile = createSnapFileDetails(snap.getKey(), ".mp4?", byteArrayOutput.size());
        }

        Timber.d("Duplicating %s bytes", byteArrayOutput.size());
        duplicateStream(snap, byteArrayOutput, tempSnapFile);
    }

    public void destroyTempDir() {
        Observable.create(
                emitter -> {
                    try {
                        File tempDir = getOrCreateTempDir();
                        FileUtils.deleteRecursive(tempDir, false);
                        FileUtils.createFile(new File(tempDir, ".nomedia"));
                    } catch (IOException e) {
                        Timber.e(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new ErrorObserver<>("Couldn't destroy temp dir"));
    }

    private File getOrCreateTempDir() throws IOException {
        String tempDirPath = getPref(TEMP_PATH);
        File tempDir = new File(tempDirPath);

        if (!tempDir.exists()) {
            if (!tempDir.mkdirs())
                throw new IOException("Couldn't find or create the Temp Directory");
            else
                FileUtils.createFile(new File(tempDir, ".nomedia"));
        }

        return tempDir;
    }

    @NonNull
    private SaveableFile createSnapFileDetails(String key, String extension, long size) throws IOException {
        File tempDir = getOrCreateTempDir();

        int snapKeyHashCode = Math.abs(key.hashCode());
        return SaveableFile.generateSaveable(
                tempDir,
                TEMP_SNAP_PREFIX + snapKeyHashCode,
                extension,
                size
        );
    }

    private void insertSnapFileDetails(Snap snap, long newSnapLength, SaveableFile tempSnapFile) {
        synchronized (MAP_LOCK) {
            List<SaveableFile> snapFileList = getFileListForKey(snap.getKey());

            Timber.d("Retrieving cache list [Size: %s]", snapFileList == null ? "NULL" : snapFileList.size());

            if (snapFileList != null &&
                    checkFileListContainsItem(snapFileList, newSnapLength, tempSnapFile)) {
                Timber.i("Attempted to write small media: " + snap.toString());
                return;
            }

            if (snapFileList == null) {
                Timber.d("Creating snap file list for: " + snap.getKey());
                snapFileList = new ArrayList<>();
                snapFileListMap.put(snap.getKey(), snapFileList);
            }

            Timber.d("Inserted new temp file into file list [File: %s]", tempSnapFile);
            snapFileList.add(tempSnapFile);
        }
    }

    private void duplicateStream(Snap snap, ByteArrayOutputStream outputStream, SaveableFile tempSnapFile) {
        try {
            FileUtils.streamCopy(outputStream, new FileOutputStream(tempSnapFile));

            if (snap.isVideo())
                handleVideoMedia(snap, tempSnapFile);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }
    }

    public boolean containsKey(String key) {
        synchronized (MAP_LOCK) {
            return snapFileListMap.containsKey(key);
        }
    }

    private boolean checkFileListContainsItem(List<SaveableFile> fileDetailsList, long newSnapLength,
                                              SaveableFile tempSnapFile) {
        Iterator<SaveableFile> tempFileIterator = fileDetailsList.iterator();

        while (tempFileIterator.hasNext()) {
            SaveableFile existingTempFile = tempFileIterator.next();

            int compareResult = compareSnapFiles(tempSnapFile, newSnapLength, existingTempFile);

            switch (compareResult) {
                case -1:
                    Timber.i("Removing smaller file: " + existingTempFile);
                    tempFileIterator.remove();
                    break;
                case 0:
                    Timber.d("Existing file");
                    return true;
                case 1:
                    Timber.i("Tried to write smaller fil: " + tempSnapFile);
                    return true;
            }
        }

        return false;
    }

    /**
     * ===========================================================================
     * -1 = REMOVE
     * 0 = THE SAME
     * 1 = OVERWRITE
     * ===========================================================================
     */
    private int compareSnapFiles(SaveableFile newSnapFile, long newSnapFileLength,
                                 SaveableFile existingSnapFile) {
        Timber.d("Comparing [f1: %s] to [f2: %s]", newSnapFile, existingSnapFile);

        if (newSnapFile.getExtension().equals(existingSnapFile.getExtension())) {
            long otherLength = existingSnapFile.length();
            Timber.d("Size comparison [f1: %s] [f2: %s]", newSnapFileLength, otherLength);

            if (newSnapFileLength > otherLength)
                return -1;
            else if (newSnapFileLength < otherLength)
                return 1;
            else
                return 0;
        } else
            return 1;
    }

    private void handleVideoMedia(Snap snap, SaveableFile videoSnapMedia) {
        Closer closer = Closer.create();

        Timber.d("Attempting to export zip file");

        try {
            ZipInputStream zipInput = closer.register(
                    new ZipInputStream(new FileInputStream(videoSnapMedia))
            );

            Timber.d("Zip input: " + zipInput);

            ZipEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                Timber.d("Zip Entry: " + entry.getName());

                if (entry.isDirectory())
                    continue;

                if (!entry.getName().contains("media~"))
                    continue;

                SaveableFile tempSnapFile = createSnapFileDetails(snap.getKey(), snap.getFileExtension(), -1);

                long copiedBytes = ByteStreams.copy(
                        zipInput,
                        closer.register(new FileOutputStream(tempSnapFile))
                );
                Timber.d("Copied %s bytes from zip", copiedBytes);

                insertSnapFileDetails(snap, copiedBytes, tempSnapFile);

                return;
            }
        } catch (Throwable e) {
            Timber.e(e, "Error exporting zip media");
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }

        Timber.i("Tried to process zip that wasn't a zip... Assuming it was a video");
        insertSnapFileDetails(snap, videoSnapMedia.length(), videoSnapMedia);
    }

    public TransferState exportSnapMedia(Snap snap) {
        Timber.d("Exporting media for: " + snap.toString());

        List<SaveableFile> snapFileList = getFileListForKey(snap.getKey());

        if (snapFileList == null || snapFileList.isEmpty()) {
            Timber.e("No temp media for snap");
            return TransferState.FAILED;
        }

        Timber.d("Exporting %s media files", snapFileList.size());

        TransferState transferState = TransferState.EXISTENT;

        int index = -1;
        for (SaveableFile tempSnapFile : snapFileList) {
            index++;

            if (tempSnapFile.hasBeenSaved())
                continue;

            Timber.d("Processing media #%s", index);
            long tempFileSize = tempSnapFile.length();

            File outputFile = snap.getOutputFile();
            Timber.d("Initial outputfile: " + outputFile);

            if (outputFile.exists())
                Timber.d("Initial output exists... Performing iterative checks");

            while (outputFile.exists()) {
                Timber.d("Comparing filesize [Index: %s][Temp: %s][Target: %s]", index, tempFileSize, outputFile.length());

                if (tempFileSize == outputFile.length()) {
                    Timber.i("File already exists");
                    outputFile = null;
                    break;
                }

                Timber.d("Performing iteration [Index: %s]", index);

                outputFile = snap.getOutputFile(index++);
            }

            if (outputFile == null)
                continue;

            try {
                Timber.d("Exporting %s", outputFile);
                Files.copy(tempSnapFile, outputFile);

                if (outputFile.length() <= 0) {
                    transferState = TransferState.FAILED;
                    Timber.d("Output file contains no bytes");
                } else {
                    transferState = TransferState.SUCCESS;
                    snap.runMediaScanner(outputFile.getAbsolutePath());
                }
            } catch (IOException e) {
                Timber.e(e);
                transferState = TransferState.FAILED;
            }
        }

        if (transferState == TransferState.SUCCESS || transferState == TransferState.EXISTENT) {
            Timber.d("Successfully saved, clearing saved items");
            markFilesAsSaved(snapFileList);
            Snap.removeFromCache(snap.getKey());
        }

        return transferState;
    }

    private List<SaveableFile> getFileListForKey(String key) {
        synchronized (MAP_LOCK) {
            return snapFileListMap.get(key);
        }
    }

    private void markFilesAsSaved(List<SaveableFile> snapFileList) {
        for (SaveableFile tempSnapFile : snapFileList)
            tempSnapFile.markSaved();
    }

    public static SnapDiskCache getInstance() {
        if (instance == null)
            instance = new SnapDiskCache();

        return instance;
    }

    public enum TransferState {
        SUCCESS(ToastType.GOOD),
        FAILED(ToastType.BAD),
        EXISTENT(ToastType.WARNING);

        private ToastType toastType;

        TransferState(ToastType toastType) {
            this.toastType = toastType;
        }

        public ToastType getToastType() {
            return toastType;
        }
    }

    @SuppressWarnings({"unused", "SerializableHasSerializationMethods"})
    private static class SaveableFile extends File {
        private static final long serialVersionUID = 1090693903808892417L;
        private boolean hasBeenSaved;
        private String extension;

        private SaveableFile(File parent, String prefix, String extension) {
            super(parent, prefix + extension);
            this.extension = extension;
        }

        void markSaved() {
            hasBeenSaved = true;
        }

        boolean hasBeenSaved() {
            return hasBeenSaved;
        }

        String getExtension() {
            return extension;
        }

        @NonNull
        static SaveableFile generateSaveable(File parent, String prefix, String extension, long size) {
            return generateSaveable(parent, prefix, extension, size, 0);
        }

        @NonNull
        private static SaveableFile generateSaveable(File parent, String prefix, String extension, long size, int iteration) {
            SaveableFile saveableFile = new SaveableFile(parent, prefix, extension);

            if (size == -1L)
                return saveableFile;

            if (saveableFile.exists() && iteration < 10) {
                if (saveableFile.length() != size)
                    return generateSaveable(parent, prefix + (int) (Math.random() * 999), extension, size, ++iteration);
            }

            return saveableFile;
        }
    }
}
