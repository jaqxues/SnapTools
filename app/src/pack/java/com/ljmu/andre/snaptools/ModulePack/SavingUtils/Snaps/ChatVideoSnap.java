package com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps;

import android.net.Uri;

import com.google.common.io.Files;
import com.ljmu.andre.snaptools.ModulePack.Caching.SnapDiskCache;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.SaveTriggerManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatVideoSnap extends Snap {
    private Uri videoPath;

    ChatVideoSnap() {
    }

    private ChatVideoSnap setVideoPath(Uri videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    @Override
    public SaveState providingAlgorithm() {
        synchronized (PROCESSING_LOCK) {
            try {
                File inputFile = new File(videoPath.getPath());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                Files.copy(inputFile, byteArrayOutputStream);

                SnapDiskCache.getInstance().writeToCache(this, byteArrayOutputStream);
            } catch (IOException e) {
                Timber.e(e);
            }

            return SaveState.NOT_READY;
        }
    }

    @Override
    public SaveState copyStream(ByteArrayOutputStream outputStream) {
        synchronized (PROCESSING_LOCK) {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }

            finished();
            return SaveTriggerManager.getTrigger(getSnapType()).setReadySnap(this);
        }
    }

    @Override
    public SaveState finalDisplayEvent() {
        synchronized (PROCESSING_LOCK) {
            return null;
        }
    }

    public static class Builder extends Snap.Builder<Builder> {
        private Uri videoPath;

        public Builder setVideoPath(Uri videoPath) {
            this.videoPath = videoPath;
            return this;
        }

        public ChatVideoSnap build() {
            ChatVideoSnap snap = super.build(ChatVideoSnap.class);
            snap.setVideoPath(videoPath);

            return snap;
        }
    }
}
