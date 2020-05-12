package com.ljmu.andre.snaptools.ModulePack.SavingUtils;

import android.app.Activity;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapTypeDef;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats.AllSnaps;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats.TypeAllSnaps;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats.TypeUsernameSnaps;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats.UsernameSnaps;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormats.UsernameTypeSnaps;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.MEDIA_PATH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORAGE_FORMAT;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.CONTENT_PATH;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
public abstract class StorageFormat {
    private static final String SPLITTER_USERNAME = "]_\\[";
    private static final Map<String, Class<? extends StorageFormat>> formatMap = new ImmutableMap.Builder<String, Class<? extends StorageFormat>>()
            .put("SnapType->Username->Snaps", TypeUsernameSnaps.class)
            .put("Username->SnapType->Snaps", UsernameTypeSnaps.class)
            .put("SnapType->AllSnaps", TypeAllSnaps.class)
            .put("Username->Snaps", UsernameSnaps.class)
            .put("AllSnaps", AllSnaps.class)
            .build();

    public abstract List<File> getSnapTypeFolders(SnapType snapType);

    /**
     * ===========================================================================
     * Overload to build the file name based on input data
     * ===========================================================================
     */
    public File getOutputFile(SnapType snapType, String username, String dateTime, @Nullable Integer snapIndex,
                              String extension) {
        String filename = getFileName(username, dateTime, snapIndex, snapType, extension);
        return getOutputFile(snapType, username, filename);
    }

    /**
     * ===========================================================================
     * Small helper function to build a filename quickly
     * ===========================================================================
     */
    private String getFileName(String username, String dateTime, @Nullable Integer snapIndex,
                               SnapType snapType, String extension) {
        return String.format(
                "%s]_[%s%s.%s%s",
                username,
                dateTime,
                snapIndex == null ? "" : snapIndex,
                snapType.getName(),
                extension);
    }

    /**
     * ===========================================================================
     * Get the appropriate output file from the selected StorageFormat child
     * E.g TypeUsernameSnaps
     * ===========================================================================
     */
    public abstract File getOutputFile(SnapType snapType, String username, String filename);

    /**
     * ===========================================================================
     * Primary function to change and convert format types
     * ===========================================================================
     */
    public static void changeStorageFormat(String newFormatKey, Activity activity) {

        // Create a ThemedDialog with the Progress Extension =========================
        ThemedDialog progressDialog = new ThemedDialog(activity)
                .setTitle("Converting Storage Formats")
                .setDismissable(false)
                .setExtension(
                        new Progress()
                                .setMessage("Converting the old storage format to the newly selected one" +
                                        "\n\nIt is unadvised to close the application until the process is complete")
                                .setCancelable(false)
                                .enableProgress()
                );
        progressDialog.show();

        // Grab the extension so we can update the progress ==========================
        Progress progressExtension = (Progress) progressDialog.getExtension();


        /**
         * ===========================================================================
         * Thread everything so we don't get UI Hangs
         * ===========================================================================
         */
        Observable.fromCallable(() -> {
            // Using the new key, get the appropriate Format =============================
            StorageFormat storageFormat = getFormatFromKey(newFormatKey);
            if (storageFormat == null)
                throw new IllegalArgumentException("Unknown StorageFormat type: " + newFormatKey);

            return storageFormat.performStorageFormatConversion(progressExtension);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(@NonNull Integer o) {
                        Timber.d("Processed %s snaps", o);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Timber.e(e);
                        progressDialog.dismiss();
                        SafeToast.show(activity, "Failed to convert Storage Format", Toast.LENGTH_LONG, true);
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                        SafeToast.show(activity, "Successfully converted Storage Format", Toast.LENGTH_LONG);
                    }
                });
        /** =========================================================================== **/

        // Regardless of the state of the conversion, update the setting =============
        putAndKill(STORAGE_FORMAT, newFormatKey, activity);
    }

    /**
     * ===========================================================================
     * Get and initialise the StorageFormat with the supplied key from the formatMap
     * ===========================================================================
     */
    public static StorageFormat getFormatFromKey(String formatKey) {
        Class<? extends StorageFormat> formatClass = formatMap.get(formatKey);
        Assert.notNull("Null StorageFormat class for " + formatKey, formatClass);

        try {
            return formatClass.newInstance();
        } catch (Throwable e) {
            // Fatal error the app as this should not happen =============================
            throw new RuntimeException(e);
        }
    }

    /**
     * ===========================================================================
     * Uses the SnapMetaData list from {@link #buildSnapList(Progress)} to
     * rebuild the Media directory
     * ===========================================================================
     */
    protected int performStorageFormatConversion(Progress progress) {
        List<SnapMetaData> snapMetaDataList = buildSnapList(progress);

        Timber.d("Processed Snaps: " + snapMetaDataList.size());

        // Finish the Progress dialog by filling in the last 25% =====================
        int counter = 0;
        double totalSize = snapMetaDataList.size();
        int updateOffset = (int) Math.ceil(totalSize * 0.1);

        for (SnapMetaData snapMetaData : snapMetaDataList) {
            if (++counter % updateOffset >= updateOffset - 1 && counter > 0 && totalSize > 0)
                progress.setProgress(75 + (int) (((double) counter / totalSize) * 25));

            File newSnapFile = getOutputFile(
                    snapMetaData.getSnapType(),
                    snapMetaData.getUsername(),
                    snapMetaData.getCurrentFile().getName()
            );

            //noinspection ResultOfMethodCallIgnored
            snapMetaData.getCurrentFile().renameTo(newSnapFile);
        }

        File contentPath = Preferences.getCreateDir(CONTENT_PATH);
        File backupDirPath = new File(contentPath, "Media_Conversions");
        FileUtils.deleteEmptyFolders(backupDirPath);

        return counter;
    }

    /**
     * ===========================================================================
     * Creates all the mappings for the Snap Conversions
     * ===========================================================================
     */
    protected List<SnapMetaData> buildSnapList(Progress progress) {
        List<SnapMetaData> metaDataList = new ArrayList<>();

        File contentPath = Preferences.getCreateDir(CONTENT_PATH);
        File mediaDirPath = new File((String) getPref(MEDIA_PATH));
        File backupDirPath = new File(contentPath, "Media_Conversions");

        if (!mediaDirPath.exists() || FileUtils.isDirEmpty(mediaDirPath))
            return Collections.emptyList();

        /**
         * ===========================================================================
         * Create a backup folder and put a readme file in it
         * ===========================================================================
         */
        if (!backupDirPath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            backupDirPath.mkdirs();

            FileUtils.createReadme(backupDirPath, "FolderInfo", "This folder contains files that failed during a Storage Format Conversion");
        }
        /** ========================================================================== **/


        // Generate a list of possible formats =======================================
        List<StorageFormat> storageFormats = new ArrayList<>(formatMap.size());
        for (String formatKey : formatMap.keySet())
            storageFormats.add(getFormatFromKey(formatKey));

        // Create an iterator to traverse through all files in the media dir =========
        Iterable<File> mediaFilesIterator = Files.fileTraverser().depthFirstPreOrder(mediaDirPath);

        /**
         * ===========================================================================
         * Counters for the Progress Dialog
         * ===========================================================================
         */
        // Get the total files in the iterator =======================================
        double totalCount = 0;
        for (File file : mediaFilesIterator)
            if (file.isFile())
                ++totalCount;

        // Get how many items to update the progress dialog after (10% of totalCount)
        int updateOffset = (int) Math.ceil(totalCount * 0.1);

        // How many items have been processed ========================================
        int processedItems = 0;
        /** ========================================================================== **/


        /**
         * ===========================================================================
         * Performs the file tree iteration -> The bulk of the work
         * ===========================================================================
         */
        for (File mediaFile : mediaFilesIterator) {

            // Increment processed items and check if we've hit the updateOffset Modulus
            // Only fill in 75% of the process as there's more to do later ===============
            if (++processedItems % updateOffset >= updateOffset - 1 && processedItems > 0 && totalCount > 0)
                progress.setProgress((int) (((double) processedItems / totalCount) * 75));

            // Directories should not be processed =======================================
            if (mediaFile.isDirectory())
                continue;

            // Get the parent of the media file and strip the root path ==================
            // We want everything after SnapTools/Media ==================================
            String mediaFileParent = mediaFile.getParent();
            String mediaFilePathStripped = mediaFileParent.replace(mediaDirPath.getAbsolutePath(), "");

            // Using the stripped filepath, create the updated backup ====================
            File backupMediaDir = new File(backupDirPath, mediaFilePathStripped);
            File backupMedia = new File(backupMediaDir, mediaFile.getName());

            // If the backup already exists, replace it - Could possibly be corrupt ======
            if (backupMedia.exists()) {
                //noinspection ResultOfMethodCallIgnored
                backupMedia.delete();
            }

            //noinspection ResultOfMethodCallIgnored
            backupMediaDir.mkdirs();

            // This shouldn't occur, if it does there's nothing we can do but ignore it ==
            if (!mediaFile.renameTo(backupMedia))
                Timber.w("Error renaming media to backup location");

            // Create some meta-data that can be used to rebuild the folders =============
            SnapMetaData metaData = new SnapMetaData();

            // Store the file being scanned so we can rename it late =====================
            metaData.setCurrentFile(backupMedia);

            // Extract the username from the filename ====================================
            String fileName = backupMedia.getName();
            String[] splitUsername = fileName.split(SPLITTER_USERNAME);

            if (splitUsername.length <= 1) {
                // If we couldn't get a filename, leave it behind ============================
                Timber.w("Couldn't determine username from " + backupMedia.getName());
                continue;
            }

            metaData.setUsername(splitUsername[0]);

            // This is a horrible function however it's simple and works for now =========
            // Our aim is to determine the StorageFormat that created the file ===========
            StorageFormat mediaFormat = null;
            SnapType snapType = getSnapTypeFromFile(backupMedia);

            if (snapType == null) {
                Timber.w("Couldn't find SnapType for: " + backupMedia);
                continue;
            }

            for (StorageFormat testFormat : storageFormats) {

                // Determine if the snap was made using this format ==========================
                if (testFormat.snapUsesThisFormat(backupMedia, snapType)) {
                    mediaFormat = testFormat;
                    break;
                }
            }

            // If we couldn't find a format, leave the file behind =======================
            if (mediaFormat == null) {
                Timber.w("Couldn't find media format for: " + backupMedia);
                continue;
            }

            // Unreachable if the SnapType hasn't been set ===============================
            metaData.setSnapType(snapType);

            // Verify if everything we've just done has been successful ==================
            // If so, add it to the list to be processed =================================
            if (metaData.isValid())
                metaDataList.add(metaData);
            else
                Timber.w("Failed to generate SnapMetaData for: " + backupMedia);
        }

        // Only delete empty folders - Failed snaps will be retained =================
        // Could cause issues but integrity is vital =================================
        FileUtils.deleteEmptyFolders(mediaDirPath);

        return metaDataList;
    }

    @Nullable
    public SnapType getSnapTypeFromFile(File snapFile) {
        String fileName = snapFile.getName();
        String[] splitName = fileName.split("\\.");

        if (splitName.length > 2) {
            String type = splitName[splitName.length - 2];
            Timber.d("Type: " + type);
            return SnapTypeDef.INST.fromName(type);
        }

        return null;
    }

    /**
     * ===========================================================================
     * Function to determine if the snapFile was created by this format
     * ===========================================================================
     */
    public abstract boolean snapUsesThisFormat(File snapFile, SnapType snapType);

    public static Set<String> getMapTypes() {
        return formatMap.keySet();
    }

    /**
     * ===========================================================================
     * Utility function to return the current storage format the user chose
     * ===========================================================================
     */
    public static StorageFormat getAppropriateFormat() {
        String formatPref = getPref(STORAGE_FORMAT);
        return getFormatFromKey(formatPref);
    }

    /**
     * ===========================================================================
     * A simple class used to hold the data needed to rebuild the Media directory
     * ===========================================================================
     */
    public static class SnapMetaData {
        private File currentFile;
        private String username;
        private SnapType snapType;

        public File getCurrentFile() {
            return currentFile;
        }

        public SnapMetaData setCurrentFile(File currentFile) {
            this.currentFile = currentFile;
            return this;
        }

        public SnapType getSnapType() {
            return snapType;
        }

        public SnapMetaData setSnapType(SnapType snapType) {
            this.snapType = snapType;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public SnapMetaData setUsername(String username) {
            this.username = username;
            return this;
        }

        public boolean isValid() {
            return username != null && snapType != null && currentFile != null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("currentFile", currentFile)
                    .add("username", username)
                    .add("snapType", snapType)
                    .toString();
        }
    }
}
