package com.ljmu.andre.snaptools.ModulePack.Notifications;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore.Video.Thumbnails;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.SnapPreviewView;
import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.BitmapUtils;

import java.io.File;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PreviewNotification extends SaveNotification {
    @Override
    protected void showNotification(Activity activity, ToastType type, int duration, @Nullable Snap snap) {
        if (type == ToastType.BAD || snap == null) {
            Timber.w("Snap type bad");
            handleErrorNotification(activity);
            return;
        }

        File snapOutputFile = snap.getOutputFile();

        if (!snapOutputFile.exists() || snapOutputFile.length() == 0L) {
            Timber.w("Snap file doesn't exist?");
            handleErrorNotification(activity);
            return;
        }

        ViewGroup previewView = new SnapPreviewView().getContainer(activity);
        ImageView previewImageView = getDSLView(previewView, "preview_image");

        Bitmap previewMedia;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(snapOutputFile.getAbsolutePath(), options);

        Pair<Integer, Integer> bestImageRes = calculateBestImageRes(activity, options.outWidth, options.outHeight);

        if (!snap.isVideo()) {
            int bestSampleSize = BitmapUtils.calculateInSampleSize(options, bestImageRes.first, bestImageRes.second);
            Timber.d("Best sample size: " + bestSampleSize);

            options.inSampleSize = bestSampleSize;
            options.inJustDecodeBounds = false;
            previewMedia = BitmapFactory.decodeFile(snapOutputFile.getAbsolutePath(), options);
            Timber.d("Loaded image at [W: %s][H: %s]", previewMedia.getWidth(), previewMedia.getHeight());
        } else {
            previewMedia = ThumbnailUtils.createVideoThumbnail(snapOutputFile.getAbsolutePath(), Thumbnails.MINI_KIND);
        }

        if (previewMedia != null) {
            previewImageView.setImageBitmap(previewMedia);

            previewImageView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("file://" + snapOutputFile.getAbsolutePath());
                intent.setDataAndType(data, (snap.isVideo() ? "video" : "image") + "/*");
                activity.startActivity(intent);
            });

            LayoutParams layoutParams = new LayoutParams(bestImageRes.first, bestImageRes.second);
            layoutParams.gravity = Gravity.BOTTOM;
            previewImageView.setLayoutParams(layoutParams);
            previewView.setLayoutParams(layoutParams);

            handleBuiltPreview(activity, previewView);
        }
    }

    private void handleErrorNotification(Activity activity) {
        Timber.w("FAILED");
    }

    private Pair<Integer, Integer> calculateBestImageRes(Activity activity, int inWidth, int inHeight) {
        int outWidth;
        int outHeight;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int maxWidth = (int) ((float) displayMetrics.widthPixels * 0.15f);
        int maxHeight = (int) ((float) displayMetrics.heightPixels * 0.15f);

        Timber.d("Max preview image sizes [W: %s][H: %s]", maxWidth, maxHeight);

        double inRatio = (double) inWidth / (double) inHeight;

        Timber.d("Snap Image Ratio: " + inRatio);

        if (inRatio > 1) {
            outWidth = maxWidth;
            outHeight = (int) ((double) maxWidth / inRatio);
        } else if (inRatio < 1) {
            outHeight = maxHeight;
            outWidth = (int) ((double) maxHeight * inRatio);
        } else {
            outWidth = maxWidth;
            outHeight = maxHeight;
        }

        Timber.d("Appropriate preview image size [W: %s][H: %s]", outWidth, outHeight);

        return Pair.create(outWidth, outHeight);
    }

    private void handleBuiltPreview(Activity activity, ViewGroup previewView) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        decor.addView(previewView, -1);
        AnimationUtils.fade(previewView, true);

        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> AnimationUtils.fadeOutWRemove(previewView, true), 3000);
    }
}
