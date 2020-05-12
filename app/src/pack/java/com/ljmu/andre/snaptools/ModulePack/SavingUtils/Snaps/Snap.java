package com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.ConstantDefiner.Constant;
import com.ljmu.andre.ConstantDefiner.ConstantDefiner;
import com.ljmu.andre.GsonPreferences.Preferences.Preference;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.StorageFormat;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CHAT_FOLDER_NAME;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.GROUP_FOLDER_NAME;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.RECEIVED_FOLDER_NAME;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SENT_FOLDER_NAME;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_FOLDER_NAME;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unchecked")
public abstract class Snap {
    private static final Map<String, Snap> streamSnapCache = new HashMap<>();
    private static final Object SNAP_CACHE_LOCK = new Object();
    private static StorageFormat storageFormat;

    final Object PROCESSING_LOCK = new Object();
    protected String key;
    private Activity context;
    private String username;
    private String dateTime;
    private String fileExtension;
    private boolean isZipped;
    private boolean isVideo;
    private SnapType snapType;

    Snap() {
        storageFormat = StorageFormat.getAppropriateFormat();
    }

    @Nullable
    public abstract SaveState providingAlgorithm();

    @Nullable
    public abstract SaveState copyStream(ByteArrayOutputStream outputStream);

    @Nullable
    public abstract SaveState finalDisplayEvent();

    public void finished() {
        removeFromCache(key);
    }

    @Nullable
    public static <T extends Snap> T removeFromCache(String key) {
        synchronized (SNAP_CACHE_LOCK) {
            return (T) streamSnapCache.remove(key);
        }
    }

    public Context getContext() {
        return context;
    }

    public Snap setContext(Activity context) {
        this.context = context;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Snap setKey(String key) {
        this.key = key;
        return this;
    }

    public <T extends Snap> T setDateTime(long timestamp) {
        return setDateTime(convertTimestamp(timestamp));
    }

    public <T extends Snap> T setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return (T) this;
    }

    public String convertTimestamp(long timestamp) {
        return StringUtils.yyyyMMddHHmmssSSS.format(timestamp);
    }

    public <T extends Snap> T setIsZipped(boolean isZipped) {
        this.isZipped = isZipped;
        return (T) this;
    }

    public boolean isZipped() {
        return isZipped;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public SnapType getSnapType() {
        return snapType;
    }

    public <T extends Snap> T setSnapType(SnapType snapType) {
        this.snapType = snapType;
        return (T) this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getOutputFile() {
        return getOutputFile(null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getOutputFile(@Nullable Integer snapIndex) {
        Assert.notNull("Incomplete: " + toString(), username, dateTime, snapType, fileExtension);

        return storageFormat.getOutputFile(snapType, username, dateTime, snapIndex, fileExtension);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("key", key)
                .add("username", username)
                .add("dateTime", dateTime)
                .add("fileExtension", fileExtension)
                .add("snapType", snapType)
                .toString();
    }

    public void runMediaScanner(String path) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path}, null,
                    (path1, uri) -> Timber.d("Scanned file: " + path1));
        } catch (Exception e) {
            Timber.e(e, "Failed to scan file: " + path);
        }
    }

    public <T extends Snap> T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public <T extends Snap> T setFileExtension(String fileExtension) {
        if (fileExtension.equalsIgnoreCase(".mp4"))
            isVideo = true;

        this.fileExtension = fileExtension;
        return (T) this;
    }

    public <T extends Snap> T insert() {
        Timber.d("Inserting: " + key);
        putIntoCache(key, this);
        return (T) this;
    }

    /**
     * ===========================================================================
     * A set of synchronous cache access functions
     * ===========================================================================
     */
    @Nullable
    public static <T extends Snap> T putIntoCache(String key, Snap snap) {
        synchronized (SNAP_CACHE_LOCK) {
            return (T) streamSnapCache.put(key, snap);
        }
    }

    @Nullable
    public static <T extends Snap> T getSnapFromCache(String key) {
        synchronized (SNAP_CACHE_LOCK) {
            return (T) streamSnapCache.get(key);
        }
    }

    public enum SaveState {
        SUCCESS, FAILED, EXISTING, NOT_READY
    }

    public static class SnapTypeDef extends ConstantDefiner<SnapType> {
        public static final SnapTypeDef INST = new SnapTypeDef();

        public static final SnapType RECEIVED = new SnapType(
                1,
                "Received",
                RECEIVED_FOLDER_NAME
        );

        public static final SnapType STORY = new SnapType(
                2,
                "Story",
                STORY_FOLDER_NAME
        );

        public static final SnapType SENT = new SnapType(
                3,
                "Sent",
                SENT_FOLDER_NAME
        );

        public static final SnapType CHAT = new SnapType(
                4,
                "Chat",
                CHAT_FOLDER_NAME
        );

        public static final SnapType GROUP = new SnapType(
                5,
                "Group",
                GROUP_FOLDER_NAME
        );
    }

    public static class SnapType extends Constant {
        private Preference dirPreference;

        public SnapType(int index, String name, Preference dirPreference) {
            super(index, name);
            this.dirPreference = dirPreference;
        }

        public Preference getPreference() {
            return dirPreference;
        }

        @Nullable
        public static SnapType getByFolderName(String folderName) {
            for (SnapType snapType : SnapTypeDef.INST.values()) {
                if (snapType.getFolderName().equals(folderName))
                    return snapType;
            }

            return null;
        }

        public String getFolderName() {
            return getPref(dirPreference);
        }
    }

    /**
     * ===========================================================================
     * A Builder pattern to allow Snap objects to be created easily.
     * Handles cache insertion/retrieval
     * ===========================================================================
     *
     * @param <T> - Allows extending for better type handling
     *            ===========================================================================
     */
    @SuppressWarnings("WeakerAccess")
    public static class Builder<T extends Builder> {
        protected Activity context;
        protected String key;
        protected String username;
        protected String dateTime;
        protected String fileExtension;
        protected boolean isZipped;
        protected SnapType snapType;

        public T setContext(Activity context) {
            this.context = context;
            return (T) this;
        }

        public T setKey(String key) {
            this.key = key;
            return (T) this;
        }

        public T setUsername(String username) {
            this.username = username;
            return (T) this;
        }

        public T setDateTime(long timestamp) {
            return setDateTime(convertTimestamp(timestamp));
        }

        public T setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return (T) this;
        }

        String convertTimestamp(long timestamp) {
            return StringUtils.yyyyMMddHHmmssSSS.format(timestamp);
        }

        public T setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return (T) this;
        }

        public T setIsZipped(boolean isZipped) {
            this.isZipped = isZipped;
            return (T) this;
        }

        public T setSnapType(SnapType snapType) {
            this.snapType = snapType;
            return (T) this;
        }

        public <S extends Snap> S build(Class<S> snapClass) {
            try {
                S snap = getSnapFromCache(key);

                if (snap != null)
                    return snap;

                snap = snapClass.newInstance()
                        .setContext(context)
                        .setKey(key)
                        .setUsername(username)
                        .setDateTime(dateTime)
                        .setFileExtension(fileExtension)
                        .setIsZipped(isZipped)
                        .setSnapType(snapType);

                putIntoCache(key, snap);

                return snap;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
