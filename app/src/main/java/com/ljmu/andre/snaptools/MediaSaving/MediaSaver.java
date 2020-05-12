package com.ljmu.andre.snaptools.MediaSaving;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Exceptions.MediaNotSavedException;
import com.ljmu.andre.snaptools.MediaSaving.AdapterHandler.MediaAdapter;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.ThreadUtils;

import java.io.File;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MediaSaver {
    @Nullable
    private Object boundData;
    private boolean threaded;
    private boolean overwrite;
    private File outputFile;
    private Saveable saveable;

    public MediaSaver setBoundData(@Nullable Object boundData) {
        this.boundData = boundData;
        return this;
    }

    public MediaSaver setThreaded(boolean threaded) {
        this.threaded = threaded;
        return this;
    }

    public MediaSaver shouldOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    public MediaSaver setOutputFile(String directory, String filename) {
        setOutputFile(new File(directory, filename));
        return this;
    }

    public MediaSaver setOutputFile(@NonNull File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public MediaSaver setSaveable(@NonNull Saveable saveable) {
        this.saveable = saveable;
        return this;
    }

    public <T> void save() {
        Assert.notNull("Missing MediaSaver parameters: " + toString(), saveable, outputFile);

        if (!overwrite && outputFile.exists()) {
            saveable.mediaSaveFinished(MediaSaveState.EXISTED, null, boundData);

            return;
        }

        if (!outputFile.exists()) {
            outputFile = FileUtils.createFile(outputFile);

            if (outputFile == null) {
                if (saveable != null) {
                    MediaNotSavedException mnsException = new MediaNotSavedException("Couldn't create output file");
                    saveable.mediaSaveFinished(
                            MediaSaveState.FAILED,
                            mnsException,
                            boundData
                    );
                }
                return;
            }
        }

        T content = saveable.getSaveableContent();
        MediaAdapter<T> mediaAdapter = AdapterHandler.getMediaAdapter(content.getClass());
        Assert.notNull("MediaAdapter not found for: " + content.getClass().getSimpleName(), mediaAdapter);

        if (!threaded) {
            mediaAdapter.save(content, outputFile, saveable, boundData);
            return;
        }

        ThreadUtils.getThreadPool().execute(() -> mediaAdapter.save(content, outputFile, saveable, boundData));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("boundData", boundData)
                .add("threaded", threaded)
                .add("overwrite", overwrite)
                .add("outputFile", outputFile)
                .add("saveable", saveable)
                .toString();
    }

    public enum MediaSaveState {
        SUCCESS, FAILED, EXISTED
    }

    public interface Saveable {
        <T> T getSaveableContent();

        void mediaSaveFinished(
                @NonNull MediaSaveState state,
                @Nullable Throwable error,
                @Nullable Object boundData);
    }
}
