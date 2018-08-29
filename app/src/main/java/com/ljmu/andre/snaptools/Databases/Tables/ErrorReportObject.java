package com.ljmu.andre.snaptools.Databases.Tables;

import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import timber.log.Timber;

import java.util.concurrent.TimeUnit;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
@TableName(value = "ErrorReports", VERSION = 1)
public class ErrorReportObject implements CBIObject {
    private final static int REPORT_ATTEMPT_CAP = 5;
    private final static long REPORT_ATTEMPT_COOLDOWN = TimeUnit.MINUTES.toMillis(5);

    @TableField(value = "ErrorHash")
    @PrimaryKey
    public String hash;

    @TableField(value = "ReportBody")
    public String reportBody;

    @TableField(value = "ReportAttempts", SQL_DEFAULT = "'0'")
    public int reportAttempts;

    @TableField(value = "DeleteReport")
    public boolean deleteReport;

    @TableField(value = "LastReportTime")
    public long lastReportTimestamp;

    public ErrorReportObject() {
    }

    public ErrorReportObject(String hash, String reportBody) {
        this.hash = hash;
        this.reportBody = reportBody;
    }

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        Timber.d("Table Upgrade");

    }

    public ErrorReportObject setDeleteReport(boolean deleteReport) {
        this.deleteReport = deleteReport;
        return this;
    }

    public ErrorReportObject failedReport() {
        incrementAttempts();
        setLastReportTimestamp(System.currentTimeMillis());
        return this;
    }

    public ErrorReportObject incrementAttempts() {
        if (!deleteReport && ++reportAttempts > REPORT_ATTEMPT_CAP)
            deleteReport = true;

        return this;
    }

    public ErrorReportObject setLastReportTimestamp(long lastReportTimestamp) {
        this.lastReportTimestamp = lastReportTimestamp;
        return this;
    }

    public boolean shouldReport() {
        return getCooldownRemaining() > REPORT_ATTEMPT_COOLDOWN;
    }

    public long getCooldownRemaining() {
        return System.currentTimeMillis() - lastReportTimestamp;
    }
}
