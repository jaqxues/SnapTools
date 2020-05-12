package com.ljmu.andre.snaptools.MediaSaving;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.MediaSaving.MediaSaver.Saveable;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

class AdapterHandler {
    private static final HashMap<Class, MediaAdapter> adapterMap = new HashMap<>();

    static {
        adapterMap.put(Bitmap.class, new ImageAdapter());
        adapterMap.put(FileInputStream.class, new FileInputStreamAdapter());
    }

    @SuppressWarnings("unchecked")
    static <T> T getMediaAdapter(Class clazz) {
        return (T) adapterMap.get(clazz);
    }

    interface MediaAdapter<T> {
        void save(
                T t,
                File outputFile,
                @Nullable Saveable savedListener,
                Object boundData
        );
    }
}
