package com.ljmu.andre.snaptools.Utils;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import net.ypresto.androidtranscoder.format.MediaFormatStrategy;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SharedVideoFormatStrategy implements MediaFormatStrategy {
    private static final int MAX_SIZE = 3840;
    private static final int DEFAULT_BITRATE = 4000 * 1000;
    private final int outBitrate;

    public SharedVideoFormatStrategy() {
        outBitrate = DEFAULT_BITRATE;
    }

    public SharedVideoFormatStrategy(int bitrate) {
        outBitrate = bitrate * 1000;
    }

    @Override
    public MediaFormat createVideoOutputFormat(MediaFormat inputFormat) {
        int width = inputFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = inputFormat.getInteger(MediaFormat.KEY_HEIGHT);

        int outWidth = width;
        int outHeight = height;

        if (outWidth > MAX_SIZE || outHeight > MAX_SIZE) {
            double ratio = (double) outWidth / (double) outHeight;

            if (ratio > 1) {
                outWidth = MAX_SIZE;
                outHeight = (int) ((double) MAX_SIZE / ratio);
            } else if (ratio < 1) {
                outHeight = MAX_SIZE;
                outWidth = (int) ((double) MAX_SIZE * ratio);
            } else {
                outWidth = MAX_SIZE;
                outHeight = MAX_SIZE;
            }
        }

        MediaFormat format = MediaFormat.createVideoFormat("video/avc", outWidth, outHeight);
        // From Nexus 4 Camera in 720p
        format.setInteger(MediaFormat.KEY_BIT_RATE, outBitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        return format;
    }

    @Override
    public MediaFormat createAudioOutputFormat(MediaFormat inputFormat) {
        return null;
    }
}
