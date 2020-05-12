package com.ljmu.andre.snaptools.Utils;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BitmapCache extends LruCache<String, WeakReference<Bitmap>> {
    private static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static BitmapCache masterCache;
    private static int cacheSize;
    private int currentMemUsage = 0;

    public BitmapCache(int memoryModifier) {
        super(maxMemory / memoryModifier);
        cacheSize = maxMemory / memoryModifier;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key,
                                WeakReference<Bitmap> oldValue, WeakReference<Bitmap> newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);

        if (oldValue == null) {
            Timber.w("Null Bitmap WeakReference");
            return;
        }

        Bitmap oldBmp = oldValue.get();

        if (oldBmp != null) {
            currentMemUsage -= sizeOf(null, oldValue);

            Timber.d("Bitmap removed from cache [MemUsage:%s/%s]", currentMemUsage, cacheSize);

            if (!oldBmp.isRecycled()) {
                oldBmp.recycle();
                Timber.d("Recycled removed bitmap");
            }
        }
    }

    @Override
    protected int sizeOf(String key, WeakReference<Bitmap> reference) {
        if (reference == null)
            return 0;

        return sizeOf(reference.get());
    }

    private int sizeOf(@Nullable Bitmap bitmap) {
        if (bitmap == null)
            return 0;

        return bitmap.getByteCount() / 1024;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            this.put(key, new WeakReference<>(bitmap));

            currentMemUsage += sizeOf(bitmap);
            Timber.d("Added image to memory cache [MemUsage:%s/%s]", currentMemUsage, cacheSize);
        }
    }

    @Nullable
    public Bitmap getBitmapFromMemCache(String key) {
        WeakReference<Bitmap> reference = get(key);

        if (reference == null)
            return null;

        return reference.get();
    }

    public void clearCache() {
        this.evictAll();
        Timber.d("Evicted %s bitmaps from cache", this.evictionCount());
        currentMemUsage = 0;
    }

    public static BitmapCache getMasterCache() {
        if (masterCache == null)
            masterCache = new BitmapCache(8);

        return masterCache;
    }
}
