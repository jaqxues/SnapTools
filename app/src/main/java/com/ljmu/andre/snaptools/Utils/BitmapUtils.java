package com.ljmu.andre.snaptools.Utils;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;


/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class BitmapUtils {
    public static BitmapDrawable resizeDrawable(BitmapDrawable bitmapDrawable, int h, int w) {
        return null;
    }

    public static int getSampleSizeForMaxSize(BitmapFactory.Options options, int maxSize) {
        int inSampleSize = 1;

        int inWidth = options.outWidth;
        int inHeight = options.outHeight;

        if (inWidth > maxSize || inHeight > maxSize) {
            double ratio = (double) inHeight / (double) inWidth;

            if (ratio > 1) {
                inHeight = maxSize;
                inWidth = (int) ((double) maxSize / ratio);
            } else if (ratio < 1) {
                inWidth = maxSize;
                inHeight = (int) ((double) maxSize * ratio);
            } else {
                inWidth = maxSize;
                inHeight = maxSize;
            }

            inSampleSize = calculateInSampleSize(options, inWidth, inHeight);
        }

        return inSampleSize;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
