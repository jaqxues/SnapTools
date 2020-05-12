package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StringUtils {
    public static final SimpleDateFormat ddMyyyy = new SimpleDateFormat("dd-M-yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS", Locale.getDefault());
    public static final SimpleDateFormat HHmmssSSS = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
    public static final SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat dMMMM = new SimpleDateFormat("d MMMM", Locale.ENGLISH);
    public static final SimpleDateFormat hmma = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    public static String getFlavourText(String flavour) {

        if (flavour != null) {
            switch (flavour.toLowerCase()) {
                case "beta":
                    return "Beta";
                case "prod":
                    return "Release";
            }
        }

        Timber.e(new Exception("Unknown Flavour: " + safeNull(flavour)));
        return flavour;
    }

    @NotNull
    public static String safeNull(@Nullable String string) {
        return string == null ? "Null" : string;
    }

    public static String htmlHighlight(String value) {
        return "<b><font color='#efde86'><u>" + value + "</u></font></b>";
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String uppercaseFirst(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String stripMediaKey(String input) {
        String finalOutput = input;

        if (finalOutput.contains("https://app.snapchat.com/bq/auth_story_blobs"))
            finalOutput = finalOutput.replace("https://app.snapchat.com/bq/auth_story_blobs", "");
        else if (finalOutput.contains("encoding=compressed")) {
            String[] split = input.split("encoding=compressed");

            if (split.length > 0)
                finalOutput = split[split.length - 1];
        } else if (finalOutput.contains("https://app.snapchat.com/bq/story_blob")) {
            String[] split = finalOutput.split("&mt=");

            if (split.length > 0) {
                finalOutput = split[split.length - 1];
                finalOutput = finalOutput.substring(1);
            }
        }

        if (finalOutput.contains("#")) {
            String[] split = finalOutput.split("#");

            if (split.length > 0)
                finalOutput = split[0];
        }

        return finalOutput;
    }

    public static String[] obfusArray(String... inputArr) {
        String[] outputArr = new String[inputArr.length];
        int index = 0;

        for (String input : inputArr)
            outputArr[index++] = obfus(input);

        return outputArr;
    }

    public static String obfus(String input) {
        StringBuilder builder = new StringBuilder();
        char[] charArray = input.toCharArray();
        boolean shouldSkip = false;

        for (char character : charArray) {
            if (shouldSkip) {
                builder.append('*');
                shouldSkip = false;
                continue;
            }

            builder.append(character);
            shouldSkip = true;
        }

        return builder.toString();
    }

    @Nullable
    public static String trimFilename(String absolutePath) {
        String[] splitPath = absolutePath.split("/");
        if (splitPath.length > 0)
            return splitPath[splitPath.length - 1];

        return null;
    }

    public static String getDateHeader(long timestamp) {
        long DAY_IN_MS = TimeUnit.DAYS.toMillis(1);
        long timeDiff = MiscUtils.calcTimeDiff(timestamp);

        if (timeDiff < DAY_IN_MS)
            return "Today";
        else if (timeDiff < (DAY_IN_MS * 2))
            return "Yesterday";
        else
            return dMMMM.format(new Date(timestamp));
    }
}
