package com.ljmu.andre.snaptools;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.CropImageView.CropMode;
import com.isseiaoki.simplecropview.CropImageView.RotateDegrees;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.ljmu.andre.GsonPreferences.Preferences;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;
import com.ljmu.andre.snaptools.Utils.SafeToast;
import com.ljmu.andre.snaptools.Utils.SharedVideoFormatStrategy;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.MediaTranscoder.Listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.COMPRESSION_QUALITY;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LOCK_SHARING_RATIO;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.RESIZE_SHARING_IMAGE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHARED_IMAGE_PATH;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_VIDEO_COMPRESSION_DIALOG;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_VIDEO_SHARING_ADVICE;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

public class SharedImageActivity extends AppCompatActivity {
    @BindView(R.id.cropImageView)
    CropImageView cropImageView;
    @BindView(R.id.img_resize_icon)
    ImageView imgResizeIcon;
    @BindView(R.id.img_lock_ratio)
    ImageView imgLockRatio;
    Unbinder unbinder;

    private File sharedMediaDir;
    private Uri sourceImageUri;
    private File tempMediaFile;
    private int rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_image);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Timber.d("Shared Media Intent: " + intent.toString());

        if (!Intent.ACTION_SEND.equals(action) || type == null)
            return;

        /**
         * ===========================================================================
         * Preference System Initialisation
         * ===========================================================================
         */
        try {
            Preferences.init(
                    Preferences.getExternalPath() + "/" + STApplication.MODULE_TAG + "/");
        } catch (Exception e) {

            Timber.e(e);

            DialogFactory.createErrorDialog(
                    this,
                    "Error Initialising Preferences",
                    "Preference system not loaded. The reason is likely to be permission issues. The application will terminate to preserve data integrity"
                            + "\n\nReason: " + e.getMessage(),
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            themedDialog.dismiss();
                            finish();
                        }
                    }
            ).setDismissable(false).show();

            return;
        }

        /**
         * ===========================================================================
         * Fabric Setup
         * ===========================================================================
         */
//		Fabric.with(this, this, new Crashlytics());
//		String email = getPref(ST_EMAIL);
//		if (email != null)
//			Crashlytics.setUserEmail(email);
//
//		String displayName = getPref(ST_DISPLAY_NAME_OBFUS);
//		if (displayName != null)
//			Crashlytics.setUserName(displayName);
//
//		Set<String> selectedPacks = getPref(SELECTED_PACKS);
//		Crashlytics.setString("Selected Packs", String.valueOf(selectedPacks));
//		Crashlytics.setString("User", email);

        refreshSharedMediaDir();

        tempMediaFile = tryCreateTempFile();

        if (tempMediaFile == null)
            return;

        if (type.startsWith("video/")) {
            Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

            if (videoUri == null) {
                showErrorDialog(
                        "Error Handling Shared Video",
                        "Shared video not found"
                );

                Timber.e(new Exception("Shared Video has no extra_stream: " + intent.toString()));
                return;
            }

            if (videoUri.getAuthority().equals("com.google.android.apps.photos.contentprovider") &&
                    videoUri.getPath().contains("mediakey")) {
                DialogFactory.createConfirmation(
                        this,
                        "Cloud Video Detected",
                        "Google Photos has provided us with an external cloud link to the video."
                                + "\nIt is advised to use a different Gallery app, however you may continue."
                                + "\n\nDownload the video now?"
                                + "\n<i>(This will use wifi/mobile data)</i>",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                showVideoSharingAdvice(videoUri);
                                themedDialog.dismiss();
                            }
                        },
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                showErrorDialog(
                                        "Cloud Video Not Downloaded",
                                        "As Google Photos only provided us with a Cloud Link, you cannot share the video without downloading it."
                                                + "\nIt is advised to retry sharing using a different Gallery"
                                );
                                themedDialog.dismiss();
                            }
                        }
                ).show();
                return;
            }

            showVideoSharingAdvice(videoUri);
        }
        // Check if our intent is an image ===========================================
        else if (type.startsWith("image/")) {
            boolean isRatioLocked = getPref(LOCK_SHARING_RATIO);
            imgResizeIcon.setSelected(getPref(RESIZE_SHARING_IMAGE));
            imgLockRatio.setSelected(isRatioLocked);

            cropImageView.setOutputMaxSize(4096, 4096);

            if (isRatioLocked) {
                cropImageView.setCropMode(CropMode.CUSTOM);
                cropImageView.setCustomRatio(100, (int) (screenRatio() * 100));
            } else
                cropImageView.setCropMode(CropMode.FREE);

            // Pull the path from the intent and load the image ==========================
            sourceImageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            Timber.d("ImageUri: " + sourceImageUri);

            if (sourceImageUri == null) {
                showErrorDialog(
                        "Error Handling Shared Image",
                        "Shared image not found"
                );

                Timber.e(new Exception("Shared Image has no extra_stream: " + intent.toString()));

                return;
            }


            refreshCropView();
        } else {
            showErrorDialog(
                    "Error With Share Request",
                    "SnapTools has received an invalid share request"
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
//		Crashlytics.dispose();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        cropImageView.setImageBitmap(null);

        super.onSaveInstanceState(outState);
    }

    private void refreshSharedMediaDir() {
        String sharedMediaPath = getPref(SHARED_IMAGE_PATH);
        sharedMediaDir = new File(sharedMediaPath);

        if (sharedMediaDir.exists()) {
            FileUtils.deleteRecursive(
                    sharedMediaDir,
                    true,
                    Sets.newHashSet(
                            FileUtils.getReadmeFilename("FolderInfo"),
                            ".nomedia"
                    )
            );
        } else {
            FileUtils.createReadme(
                    sharedMediaDir,
                    "FolderInfo",
                    "This folder is used primarily to store the modified images that are shared."
                            + "\nIt is necessary so that we don't directly modify the source image."
                            + "\nThe folder is cleared every time you share an image so it SHOULD only contain a single image at a time"
            );
        }

        //noinspection ResultOfMethodCallIgnored
        sharedMediaDir.mkdirs();

        File nomediaFile = new File(sharedMediaDir, ".nomedia");

        try {
            if (!nomediaFile.exists() && !nomediaFile.createNewFile()) {
                Timber.w("Failed to create SharedMedia.nomedia file");
            }
        } catch (IOException e) {
            Timber.w("Failed to create SharedMedia.nomedia file");
        }
    }

    private File tryCreateTempFile(@Nullable String suffix) {
        try {
            return File.createTempFile("Shared", suffix, sharedMediaDir);
        } catch (IOException e) {
            Timber.e(e);

            showErrorDialog(
                    "Error Saving Cropped Image",
                    "There was an issue saving the cropped image to send to Snapchat"
            );
        }

        return null;
    }

    /**
     * ===========================================================================
     * Show an error dialog that kills this activity after viewing
     * ===========================================================================
     */
    private void showErrorDialog(String title, String message) {
        DialogFactory.createErrorDialog(
                this,
                title,
                message,
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        SharedImageActivity.this.finish();
                        themedDialog.dismiss();
                    }
                }
        ).setDismissable(false).show();
    }

    private void showVideoSharingAdvice(Uri videoUri) {
        if (getPref(SHOW_VIDEO_SHARING_ADVICE)) {
            DialogFactory.createBasicMessage(
                    this,
                    "Video Sharing Advice",
                    "When sharing a video, it is highly advised that you <b>record a video around "
                            + htmlHighlight("8 or 9 seconds long") + ".</b>"
                            + "\nDoing this makes it much more likely that the video will successfully send",
                    new ThemedClickListener(SHOW_VIDEO_SHARING_ADVICE, false) {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            handleSharedVideo(videoUri);
                            themedDialog.dismiss();
                        }
                    }
            ).show();
        } else
            handleSharedVideo(videoUri);
    }

    private float screenRatio() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return getHeightWidthRatio(metrics.heightPixels, metrics.widthPixels);
    }

    private void refreshCropView() {
        if (getPref(RESIZE_SHARING_IMAGE))
            createBoundsAdjustedBitmap();
        else
            loadSourceImage();
    }

    private void handleSharedVideo(Uri videoUri) {
        Timber.d("Video URI: " + videoUri.getEncodedPath());

        ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                this,
                "Copying Source Video",
                "Currently copying the shared video from the source location provided from the Gallery",
                false
        );

        progressDialog.show();

        Observable.fromCallable(() -> {
            copyVideoStream(videoUri);
            return tempMediaFile.length() > 0;
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!SharedImageActivity.this.isFinishing())
                            progressDialog.dismiss();

                        if (!aBoolean) {
                            showErrorDialog("Shared Video Error", "Video failed to copy to SharedImages folder");
                            return;
                        }

                        checkFramerateAndShare();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);

                        if (!SharedImageActivity.this.isFinishing())
                            progressDialog.dismiss();

                        showErrorDialog(
                                "Video Copy Failed",
                                "The supplied video could not be copied to the SharedImages folder"
                        );
                    }
                });
    }

    private float getHeightWidthRatio(int height, int width) {
        return ((float) height / (float) width);
    }

    private void createBoundsAdjustedBitmap() {
        ThemedDialog progressDialog = new ThemedDialog(this)
                .setTitle("Processing Image")
                .setExtension(
                        new Progress()
                                .setMessage("Creating scaled image copy")
                                .setCancelable(false)
                ).setDismissable(false);

        progressDialog.show();

        //noinspection Guava
        Observable.fromCallable(
                (Callable<Optional<String>>) () -> {
                    Bitmap initialBitmap = FileUtils.loadBitmap(sourceImageUri, SharedImageActivity.this);

                    if (initialBitmap == null) {
                        return Optional.absent();
                    }

                    Bitmap adjustedBitmap = modifyBounds(initialBitmap);
                    Closer closer = Closer.create();

                    try {
                        BufferedOutputStream outputStream = closer.register(
                                new BufferedOutputStream(new FileOutputStream(tempMediaFile))
                        );

                        adjustedBitmap.compress(CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        initialBitmap.recycle();
                        adjustedBitmap.recycle();
                    } finally {
                        try {
                            closer.close();
                        } catch (IOException ignored) {
                        }
                    }

                    return Optional.of("Success");
                }
        ).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Optional<String>>() {
                    @Override
                    public void onNext(@NonNull Optional<String> bitmapOptional) {
                        progressDialog.dismiss();

                        if (!bitmapOptional.isPresent()) {
                            SafeToast.show(
                                    SharedImageActivity.this,
                                    "Shared image cannot be found or scaled",
                                    true
                            );

                            return;
                        }

                        MediaScannerConnection.scanFile(
                                SharedImageActivity.this,
                                new String[]{tempMediaFile.getAbsolutePath()},
                                new String[]{"image/jpeg"},
                                (path, uri) -> Timber.d("Scanned %s", path)
                        );

                        reloadCropView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        progressDialog.dismiss();

                        DialogFactory.createErrorDialog(
                                SharedImageActivity.this,
                                "Error while processing shared image",
                                "Could not load Bitmap from shared image"
                        ).show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void loadSourceImage() {
        cropImageView.startLoad(
                sourceImageUri,
                new LoadCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        showErrorDialog(
                                "Error loading shared image",
                                "There has been an error loading the shared image into the cropping utility"
                        );
                    }
                }
        );
    }

    private void copyVideoStream(Uri videoUri) {
        Closer closer = Closer.create();
        try {
            InputStream inputStream = closer.register(getContentResolver().openInputStream(videoUri));
            FileOutputStream outputStream = closer.register(new FileOutputStream(tempMediaFile));

            ByteStreams.copy(
                    inputStream,
                    outputStream
            );

            outputStream.flush();
        } catch (FileNotFoundException e) {
            Timber.e(new Exception("Video Stream not found: " + videoUri, e));
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void checkFramerateAndShare() {
        //int framerate = getVideoFramerate(tempMediaFile.getAbsolutePath());
        //Timber.d("Framerate:  " + framerate);

        if (getPref(SHOW_VIDEO_COMPRESSION_DIALOG)) {
            DialogFactory.createConfirmation(
                    this,
                    "Video Framerate Issue",
                    "Snapchat appears to have issues with videos over 30fps and so the video may not send."
                            + "\n\nIf your video " + htmlHighlight("HAS AUDIO") + " you can attempt to compress the video for a greater chance of success."
                            + "\n<i>(Compression can only happen on videos with audio)"
                            + "\n\n" + htmlHighlight("NO") + " - Use original copied video"
                            + "\n" + htmlHighlight("YES") + " - Attempt to compress video",
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            compressAndShare();
                            themedDialog.dismiss();
                        }
                    },
                    new ThemedClickListener() {
                        @Override
                        public void clicked(ThemedDialog themedDialog) {
                            sendToSC(tempMediaFile.getAbsolutePath(), true);
                            themedDialog.dismiss();
                            finish();
                        }
                    }
            ).show();
        } else {
            sendToSC(tempMediaFile.getAbsolutePath(), true);
            finish();
        }
    }

    /**
     * ===========================================================================
     * Take a bitmap and project it onto a black canvas that matches the screen
     * aspect ratio.
     * This function will make its best effort to maintain image aspect ratio
     * by fitting the canvas to the largest direction of the image.
     * <p>
     * Image size clamping is also performed so that the canvas will never exceed
     * 4096px in either direction, so not to run into OOM exceptions.
     * <p>
     * Finally {@link this#rotation} is also applied to the modified image as it
     * is much more efficient to batch the rotation with the matrix during resize,
     * compared to afterwards or before
     * ===========================================================================
     */
    private Bitmap modifyBounds(Bitmap source) {
        int canvasHeight = source.getHeight();
        int canvasWidth = source.getWidth();

        int imageHeight = source.getHeight();
        int imageWidth = source.getWidth();

        Matrix matrix = new Matrix();

        float imageRatio = getHeightWidthRatio(imageHeight, imageWidth);
        float screenRatio = screenRatio();
        float offsetRatio = (screenRatio / imageRatio);

        Timber.d("ScreenRatio %s, ImgRatio %s, Offset %s", screenRatio, imageRatio, offsetRatio);

        if (imageRatio < screenRatio) {
            canvasHeight = (int) (imageHeight * offsetRatio);
            canvasWidth = (int) (canvasHeight / screenRatio);
        } else if (imageRatio >= screenRatio) {
            canvasWidth = (int) (imageWidth * offsetRatio);
            canvasHeight = (int) (canvasWidth * screenRatio);
        }

        Timber.d("PreSize [%sx%s]", canvasWidth, canvasHeight);

        // Clamp the maximum size to 4096px whiLe still maintaining aspect ratio =====
        if (canvasHeight > 4096 && canvasHeight > canvasWidth) {
            canvasHeight = 4096;
            canvasWidth = (int) (canvasHeight / screenRatio);
        }
        if (canvasWidth > 4096 && canvasWidth > canvasHeight) {
            canvasWidth = 4096;
            canvasHeight = (int) (canvasWidth / screenRatio);
        }

        Timber.d("Sizing Img[%sx%s] Canv[%sx%s]", imageWidth, imageHeight, canvasWidth, canvasHeight);

        if (rotation == 90 || rotation == -90) {
            imageHeight = canvasWidth;
            imageWidth = (int) ((float) imageHeight / imageRatio);

            Timber.d("ImageRotation: %sx%s", imageWidth, imageHeight);
        } else {
            if (offsetRatio > 1) {
                imageHeight = (int) (canvasHeight / offsetRatio);
                imageWidth = (int) (imageHeight / imageRatio);
            } else {
                imageWidth = (int) (canvasWidth * offsetRatio);
                imageHeight = (int) (imageWidth * imageRatio);
            }
        }

        Timber.d("PostSize [%sx%s]", canvasWidth, canvasHeight);

        Bitmap targetBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.RGB_565);

        RectF sourceRect = new RectF(0, 0, source.getWidth(), source.getHeight());
        RectF targetRect = new RectF(0, 0, imageWidth, imageHeight);

        Timber.d("SourceRect: " + sourceRect.toString());
        Timber.d("TargetRect: " + targetRect.toString());

        matrix.setRectToRect(sourceRect, targetRect, ScaleToFit.FILL);
        Timber.d("Matrix: " + matrix.toString());

        matrix.postTranslate((canvasWidth - imageWidth) / 2, (canvasHeight - imageHeight) / 2);
        matrix.postRotate(rotation, canvasWidth / 2, canvasHeight / 2);

        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(source, matrix, null);

        return targetBitmap;
    }

    private void reloadCropView() {
        cropImageView.startLoad(
                Uri.fromFile(tempMediaFile),
                new LoadCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        showErrorDialog(
                                "Error loading shared image",
                                "There has been an error loading the shared image into the cropping utility"
                        );
                    }
                }
        );
    }

    private int getVideoFramerate(String path) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            //Adjust data source as per the requirement if file, URI, etc.
            extractor.setDataSource(path);
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; ++i) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        return format.getInteger(MediaFormat.KEY_FRAME_RATE);
                    }
                }
            }
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            //Release stuff
            extractor.release();
        }

        return -1;
    }

    private void compressAndShare() {
        Progress progressExtension = new Progress()
                .setMessage("Compressing video to a format that Snapchat " + htmlHighlight("SHOULD") + " accept. This should only take a few moments")
                .setCancelable(true)
                .enableProgress();

        ThemedDialog progressDialog = new ThemedDialog(this)
                .setTitle("Compressing Video")
                .setExtension(progressExtension);

        progressDialog.show();

        try {
            int videoBitrate = getVideoBitrate(tempMediaFile.getAbsolutePath());
            int outputBitrate = getPref(COMPRESSION_QUALITY);

            if (videoBitrate > 0 && outputBitrate > videoBitrate)
                outputBitrate = videoBitrate;

            File compressedVideoFile = tryCreateTempFile(".compressed");

            if (compressedVideoFile == null)
                return;

            Future<Void> transcodeFuture = MediaTranscoder.getInstance().transcodeVideo(
                    tempMediaFile.getAbsolutePath(),
                    compressedVideoFile.getAbsolutePath(),
                    new SharedVideoFormatStrategy(outputBitrate),
                    new Listener() {
                        @Override
                        public void onTranscodeProgress(double progress) {
                            if (progressExtension != null)
                                progressExtension.setProgress((int) (progress * 100));
                        }

                        @Override
                        public void onTranscodeCompleted() {
                            Activity activity = SharedImageActivity.this;

                            if (!activity.isFinishing() && progressDialog.isShowing())
                                progressDialog.dismiss();

                            sendToSC(compressedVideoFile.getAbsolutePath(), true);
                        }

                        @Override
                        public void onTranscodeCanceled() {
                            Activity activity = SharedImageActivity.this;

                            if (!activity.isFinishing()) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();

                                DialogFactory.createConfirmation(
                                        SharedImageActivity.this,
                                        "Compression Cancelled",
                                        "Video compression has been cancelled, would you like to share the original video?",
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                sendToSC(tempMediaFile.getAbsolutePath(), true);
                                                themedDialog.dismiss();
                                                finish();
                                            }
                                        },
                                        new ThemedClickListener() {
                                            @Override
                                            public void clicked(ThemedDialog themedDialog) {
                                                themedDialog.dismiss();
                                                finish();
                                            }
                                        }
                                ).show();
                            }
                        }

                        @Override
                        public void onTranscodeFailed(Exception exception) {
                            Timber.e(exception);

                            Activity activity = SharedImageActivity.this;

                            if (!activity.isFinishing()) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                            transcodeFailed();
                        }
                    }
            );

            progressExtension.setCancelCallback(
                    aVoid -> {
                        if (transcodeFuture != null && !transcodeFuture.isCancelled() && !transcodeFuture.isDone())
                            transcodeFuture.cancel(true);
                    }
            );
        } catch (IOException e) {
            Timber.e(e, "Failed to transcode shared video");

            transcodeFailed();
        } catch (RuntimeException e) {
            Timber.e(e, "Fatal error while transcoding shared video");

            transcodeFailed();
        }
    }

    private void transcodeFailed() {
        DialogFactory.createConfirmation(
                SharedImageActivity.this,
                "Compression Failed",
                "Video compression has failed, would you like to share the original video?",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        sendToSC(tempMediaFile.getAbsolutePath(), true);
                        themedDialog.dismiss();
                        finish();
                    }
                },
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        finish();
                    }
                }
        ).setHeaderDrawable(R.drawable.error_header)
                .show();
    }

    private void sendToSC(String filePath, boolean isVideo) {
        Timber.d("Sending '%s' to snapchat", filePath);

        try {
            // Send the path of the cropped image to snapchat ============================
            Intent shareIntent = new Intent();
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setAction(Intent.ACTION_MAIN);
            shareIntent.putExtra("IS_SHARE", true);

            if (!isVideo)
                shareIntent.putExtra("image_url", filePath);
            else
                shareIntent.putExtra("video_url", filePath);

            shareIntent.setComponent(ComponentName.unflattenFromString("com.snapchat.android/.LandingPageActivity"));
            startActivity(shareIntent);
            finish();
        } catch (ActivityNotFoundException e) {
            Timber.e(e);

            DialogFactory.createErrorDialog(
                    SharedImageActivity.this,
                    "Snapchat Not Installed",
                    "Snapchat could not be found, seems like it isn't installed!"
            ).show();
        }
    }

    private int getVideoBitrate(String path) {
        int bitrate = -1;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(path);
            String metadataBitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            bitrate = Integer.parseInt(metadataBitrate);
        } catch (Throwable e) {
            Timber.e(e, "Issue retrieving video metadata");
        } finally {
            retriever.release();
        }

        return bitrate;
    }

    private File tryCreateTempFile() {
        return tryCreateTempFile(null);
    }

    @OnClick({R.id.btn_check, R.id.btn_rotate_left, R.id.btn_resize, R.id.btn_lock_ratio, R.id.btn_rotate_right, R.id.btn_cross})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                sendCurrentImage();
                break;
            case R.id.btn_rotate_left:
                if (!(boolean) getPref(RESIZE_SHARING_IMAGE)) {
                    cropImageView.rotateImage(RotateDegrees.ROTATE_M90D);
                    return;
                }

                rotation -= 90;

                if (rotation < -180)
                    rotation = 90;

                Timber.d("Rotation: " + rotation);
                createBoundsAdjustedBitmap();
                break;
            case R.id.btn_rotate_right:
                if (!(boolean) getPref(RESIZE_SHARING_IMAGE)) {
                    cropImageView.rotateImage(RotateDegrees.ROTATE_90D);
                    return;
                }

                rotation += 90;

                if (rotation > 180)
                    rotation = -90;

                Timber.d("Rotation: " + rotation);

                createBoundsAdjustedBitmap();
                break;
            case R.id.btn_cross:
                // Kill the activity =========================================================
                SharedImageActivity.this.finish();
                break;
            case R.id.btn_resize:
                boolean isResizeEnabled = PreferenceHelpers.togglePreference(RESIZE_SHARING_IMAGE);
                imgResizeIcon.setSelected(isResizeEnabled);
                refreshCropView();
                break;
            case R.id.btn_lock_ratio:
                boolean isRatioLocked = PreferenceHelpers.togglePreference(LOCK_SHARING_RATIO);
                imgLockRatio.setSelected(isRatioLocked);

                if (isRatioLocked) {
                    cropImageView.setCropMode(CropMode.CUSTOM);
                    cropImageView.setCustomRatio(100, (int) (screenRatio() * 100));
                } else
                    cropImageView.setCropMode(CropMode.FREE);
                break;
        }
    }

    private void sendCurrentImage() {
        ThemedDialog progressDialog = new ThemedDialog(this)
                .setTitle("Processing Image")
                .setExtension(
                        new Progress()
                                .setMessage("Performing image manipulation, saving, and sending")
                                .setCancelable(false)
                ).setDismissable(false);

        progressDialog.show();

        /** =========================================================================== **/

        cropImageView.startCrop(
                Uri.fromFile(tempMediaFile),
                null,
                new SaveCallback() {
                    @Override
                    public void onSuccess(Uri outputUri) {
                        sendToSC(outputUri.getPath(), false);
                        // Kill the activity =========================================================
                        progressDialog.dismiss();
                        SharedImageActivity.this.finish();
                    }

                    @Override
                    public void onError() {
                        progressDialog.dismiss();

                        showErrorDialog(
                                "Error Saving Cropped Image",
                                "There was an issue saving the cropped image to send to Snapchat"
                        );
                    }
                }
        );
    }
}
