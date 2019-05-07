package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.dp;
import static com.ljmu.andre.snaptools.Utils.ResourceMapper.getResId;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class NotificationUtils {

    public static void showLoadedNotification(Activity snapActivity) {
        Context moduleContext = ContextHelper.getModuleContext(snapActivity);

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeResource(
                moduleContext.getResources(),
                getResId(
                        moduleContext,
                        "snaptools_logo",
                        "drawable"
                ),
                bitmapOptions
        );

        snapActivity.runOnUiThread(() -> {
            ImageView view = new ImageView(snapActivity);
            view.setImageBitmap(bitmap);
            view.bringToFront();
            view.setAlpha(0.25F);

            Toast statusToast = new Toast(snapActivity);
            statusToast.setView(view);
            statusToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, dp(3, moduleContext));

            statusToast.setDuration(Toast.LENGTH_LONG);
            statusToast.show();
        });
    }
}
