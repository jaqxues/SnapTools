package com.ljmu.andre.snaptools.MediaSaving;

import androidx.annotation.Nullable;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.ljmu.andre.snaptools.Exceptions.MediaNotSavedException;
import com.ljmu.andre.snaptools.MediaSaving.AdapterHandler.MediaAdapter;
import com.ljmu.andre.snaptools.MediaSaving.MediaSaver.MediaSaveState;
import com.ljmu.andre.snaptools.MediaSaving.MediaSaver.Saveable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class FileInputStreamAdapter implements MediaAdapter<FileInputStream> {
    @Override
    public void save(
            FileInputStream inputStream,
            File outputFile,
            @Nullable Saveable savedListener,
            @Nullable Object boundData) {
        Closer closer = Closer.create();

        try {
            InputStream videoInStream = closer.register(inputStream);
            OutputStream videoOutStream =
                    closer.register(new FileOutputStream(outputFile));

            long transferred = ByteStreams.copy(videoInStream, videoOutStream);

            videoOutStream.flush();

            if (transferred <= 0)
                throw new Exception("Transferred <= 0 bytes!");

            if (savedListener != null)
                savedListener.mediaSaveFinished(MediaSaveState.SUCCESS, null, boundData);
        } catch (Throwable e) {
            MediaNotSavedException mediaNotSavedException =
                    new MediaNotSavedException("Exception saving video outputFile", e);

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
