package com.ljmu.andre.snaptools.EventBus.Events;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;

import java.io.File;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackDownloadEvent {
    private DownloadState state;
    private PackMetaData metaData;
    private String message;
    private File outputFile;
    private int responseCode;

    public DownloadState getState() {
        return state;
    }

    public PackDownloadEvent setState(DownloadState state) {
        this.state = state;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public PackDownloadEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public PackMetaData getMetaData() {
        return metaData;
    }

    public PackDownloadEvent setMetaData(PackMetaData metaData) {
        this.metaData = metaData;
        return this;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public PackDownloadEvent setOutputFile(File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public PackDownloadEvent setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("state", state)
                .add("metadata", metaData != null ? metaData.toString() : null)
                .add("message", message)
                .toString();
    }

    public enum DownloadState {
        SUCCESS, FAIL, SKIP
    }
}
