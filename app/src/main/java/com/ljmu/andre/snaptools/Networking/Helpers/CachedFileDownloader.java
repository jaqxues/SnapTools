package com.ljmu.andre.snaptools.Networking.Helpers;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request.Method;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * ===========================================================================
 * A file downloader with caching capabilities to increase file handling
 * flexibilities
 * ===========================================================================
 */
public abstract class CachedFileDownloader {
    private boolean bypassCache;
    private boolean prioritiseCache;

    /**
     * ===========================================================================
     * Uses the best method to fetch the file result
     * ===========================================================================
     */
    public void smartFetch(Activity activity, ObjectResultListener<byte[]> resultListener) {
        Timber.d("Using smart fetch for cached file");

        if (prioritiseCache || (!bypassCache && shouldUseCache())) {
            readFromCache(activity, new ObjectResultListener<byte[]>() {
                @Override
                public void success(String message, byte[] object) {
                    resultListener.success(message, object);
                }

                @Override
                public void error(String message, Throwable t, int errorCode) {
                    getFromServer(activity, resultListener);
                }
            });

            return;
        }

        // ===========================================================================

        getFromServer(activity, new ObjectResultListener<byte[]>() {
            @Override
            public void success(String message, byte[] object) {
                resultListener.success(message, object);
            }

            @Override
            public void error(String message, Throwable t, int errorCode) {
                readFromCache(activity, resultListener);
            }
        });
    }

    protected abstract boolean shouldUseCache();

    public void readFromCache(Activity activity, ObjectResultListener<byte[]> resultListener) {
        Timber.d("Fetching file from disk cache");

        File cacheDir = getCacheDir(activity);
        File cachedFile = new File(cacheDir, getCachedFilename());

        if (!cachedFile.exists()) {
            Timber.d("Cached file doesn't exist");
            resultListener.error("Cached file doesn't exist",
                    null,
                    -1
            );

            return;
        }

        try {
            ByteSource cachedByteSource = Files.asByteSource(cachedFile);
            byte[] cachedFileBytes = cachedByteSource.read();

            Timber.d("Successfully fetched file from cache");
            resultListener.success(null, cachedFileBytes);
        } catch (IOException e) {
            Timber.e(e);

            resultListener.error(
                    "Failed to read from cache",
                    e,
                    -1
            );
        }
    }

    public void getFromServer(Activity activity, ObjectResultListener<byte[]> resultListener) {
        Timber.d("Fetching file from server");

        new WebRequest.Builder()
                .setUrl(getURL())
                .setContext(activity)
                .setMethod(Method.GET)
                .setType(RequestType.STREAM)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        if (webResponse.getResult() == null) {
                            Timber.d("Failed to fetch file from server.");

                            resultListener.error(
                                    null,
                                    null,
                                    -1
                            );
                            return;
                        }

                        Timber.d("Successfully fetched file from server");

                        byte[] responsePayload = webResponse.getResult();

                        if (writeToCache(activity, responsePayload)) {
                            Timber.d("Successfully cached file");
                            updateCacheTime();
                        } else {
                            Timber.e("Failed to write cache file %s",
                                    getCachedFilename());
                        }

                        resultListener.success(null, responsePayload);
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        Timber.d("Failed to fetch file from server.");

                        if (webResponse.getException() != null)
                            Timber.e(webResponse.getException(), webResponse.getMessage());
                        else
                            Timber.w(webResponse.getMessage());

                        resultListener.error(
                                webResponse.getMessage(),
                                webResponse.getException(),
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();
    }

    protected File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    protected abstract String getCachedFilename();

    protected abstract String getURL();

    private boolean writeToCache(Context context, byte[] cachedPayload) {
        File cacheDir = getCacheDir(context);
        File cachedFile = new File(cacheDir, getCachedFilename());

        return FileUtils.tryCreate(cachedFile) &&
                FileUtils.tryWrite(cachedPayload, cachedFile);

    }

    protected abstract void updateCacheTime();

    public CachedFileDownloader setBypassCache(boolean bypassCache) {
        if (prioritiseCache)
            throw new RuntimeException("Cannot bypass cache while prioritising cache too");

        this.bypassCache = bypassCache;
        return this;
    }

    @RequiresFramework(73)
    public CachedFileDownloader setShouldPrioritiseCache(boolean prioritiseCache) {
        if (this.bypassCache)
            throw new RuntimeException("Cannot prioritise cache while bypassing cache too");

        this.prioritiseCache = prioritiseCache;
        return this;
    }
}
