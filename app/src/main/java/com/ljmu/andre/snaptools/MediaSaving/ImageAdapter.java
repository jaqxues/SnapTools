package com.ljmu.andre.snaptools.MediaSaving;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import androidx.annotation.Nullable;

import com.google.common.io.Closer;
import com.ljmu.andre.snaptools.Exceptions.MediaNotSavedException;
import com.ljmu.andre.snaptools.MediaSaving.AdapterHandler.MediaAdapter;
import com.ljmu.andre.snaptools.MediaSaving.MediaSaver.MediaSaveState;
import com.ljmu.andre.snaptools.MediaSaving.MediaSaver.Saveable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class ImageAdapter implements MediaAdapter<Bitmap> {
    @Override
    public void save(
            Bitmap bitmap,
            File outputFile,
            @Nullable Saveable savedListener,
            @Nullable Object boundData) {

        if (bitmap.isRecycled()) {
            if (savedListener != null) {
                savedListener.mediaSaveFinished(
                        MediaSaveState.FAILED,
                        new MediaNotSavedException("Bitmap has been recycled!"),
                        boundData
                );
            }

            return;
        }

        Closer closer = Closer.create();

        try {
            FileOutputStream imageStream = closer.register(new FileOutputStream(outputFile));
            boolean success = bitmap.compress(CompressFormat.JPEG, 100, imageStream);

            if (!success)
                throw new Exception("Couldn't compress bitmap!");

            if (savedListener != null)
                savedListener.mediaSaveFinished(MediaSaveState.SUCCESS, null, boundData);
        } catch (Throwable e) { // must catch Throwable
            MediaNotSavedException mediaNotSavedException =
                    new MediaNotSavedException("Exception saving image outputFile", e);

            if (savedListener != null)
                savedListener.mediaSaveFinished(MediaSaveState.FAILED, mediaNotSavedException, boundData);
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }
}
