package com.ljmu.andre.snaptools.Networking.Helpers;

import android.content.Context;
import com.android.volley.Request.Method;
import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import timber.log.Timber;

import java.io.File;
import java.util.*;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class DownloadFile {
    private static final byte[] AUTH_ERROR = "auth_error".getBytes();
    private static final byte[] BANNED = "ban".getBytes();
    private static final byte[] NON_DEV_ERROR = "non_dev".getBytes();
    private static final byte[] NON_PURCHASE_ERROR = "non_purchase".getBytes();
    private static final byte[] NON_EXIST_ERROR = "non_exist".getBytes();
    private Context context;
    private Map<String, String> params = new HashMap<>();
    private String url;
    private String directory;
    private String filename;
    private int method = Method.POST;
    private String volleyTag = "file_download";
    private List<DownloadListener> downloadListeners = new ArrayList<>();

    private WebRequest ongoingRequest;

    public DownloadFile() {
    }

    public DownloadFile setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public DownloadFile addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public DownloadFile setContext(Context context) {
        this.context = context;
        return this;
    }

    public DownloadFile setMethod(int method) {
        this.method = method;
        return this;
    }

    public DownloadFile setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownloadFile setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public DownloadFile setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public DownloadFile setVolleyTag(String volleyTag) {
        this.volleyTag = volleyTag;
        return this;
    }

    public DownloadFile addDownloadListener(DownloadListener downloadListener) {
        downloadListeners.add(downloadListener);
        return this;
    }

    public DownloadFile removeDownloadListener(DownloadListener downloadListener) {
        downloadListeners.remove(downloadListener);
        return this;
    }

    public DownloadFile download() {
        Assert.notNull("Missing download parameters: "
                + toString(), directory, filename, url);

        ongoingRequest = new WebRequest.Builder()
                .setContext(context)
                .setUrl(url)
                .setType(RequestType.STREAM)
                .setTag(volleyTag)
                .setMethod(method)
                .shouldClearCache(true)
                .setParams(params)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) {
                        byte[] response = webResponse.getResult();

                        if (response != null) {
                            if (Arrays.equals(response, BANNED)) {
                                downloadFinished(
                                        false,
                                        "User has been banned",
                                        null,
                                        105
                                );
                                return;
                            }
                            if (Arrays.equals(response, AUTH_ERROR)) {
                                downloadFinished(
                                        false,
                                        "User Authentication Error",
                                        null,
                                        104
                                );
                                return;
                            }
                            if (Arrays.equals(response, NON_DEV_ERROR)) {
                                downloadFinished(
                                        false,
                                        "Requested Developer File without being a Developer",
                                        null,
                                        103
                                );
                                return;
                            }
                            if (Arrays.equals(response, NON_PURCHASE_ERROR)) {
                                downloadFinished(
                                        false,
                                        "Requested Premium Pack without purchasing",
                                        null,
                                        102
                                );
                                return;
                            }
                            if (Arrays.equals(response, NON_EXIST_ERROR)) {
                                downloadFinished(
                                        false,
                                        "Requested file does not exist",
                                        null,
                                        101
                                );
                                return;
                            }

                            File outputFile = new File(directory, filename);

                            if (outputFile.exists() && !outputFile.delete()) {
                                downloadFinished(
                                        false,
                                        "Failed to overwrite duplicate file",
                                        null,
                                        -1
                                );
                                return;
                            }

                            outputFile = FileUtils.createFile(directory, filename);

                            if (outputFile != null && FileUtils.tryWrite(response, outputFile)) {
                                downloadFinished(
                                        true,
                                        "Successfully downloaded file",
                                        outputFile,
                                        0
                                );
                                return;
                            }
                        }

                        downloadFinished(
                                false,
                                "Received Null Bytes",
                                null,
                                -1
                        );
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        if (webResponse.getException() != null)
                            Timber.e(webResponse.getException(), webResponse.getMessage());
                        else
                            Timber.w(webResponse.getMessage());

                        downloadFinished(
                                false,
                                "Web Error: " + webResponse.getMessage(),
                                null,
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();

        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("context", context)
                .add("url", url)
                .add("directory", directory)
                .add("filename", filename)
                .add("downloadListeners", downloadListeners)
                .add("ongoingRequest", ongoingRequest)
                .toString();
    }

    private void downloadFinished(boolean state, String message, File outputFile, int responseCode) {
        for (DownloadListener downloadListener : downloadListeners)
            downloadListener.downloadFinished(state, message, outputFile, responseCode);
    }

    public void cancel() {
        ongoingRequest.cancel();
    }

    public interface DownloadListener {
        void downloadFinished(boolean state, String message, File outputFile, int responseCode);
    }
}
