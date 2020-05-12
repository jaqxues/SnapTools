package com.ljmu.andre.snaptools.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class SafeToast {
    public static boolean show(Activity activity, String text) {
        return show(activity, text, Toast.LENGTH_LONG, false);
    }

    public static boolean show(Activity activity, String text, int length, boolean isError) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed())
            return false;

        try {
            showCustomToast(activity, text, length, isError);
            return true;
        } catch (Throwable t) {
            Timber.e(t, "Error displaying custom toast");
            try {
                Toast.makeText(activity, text, length).show();
                return true;
            } catch (Throwable t2) {
                Timber.e(t, "Error displaying default toast");
            }
        }

        return false;
    }

    private static void showCustomToast(Activity activity, String text, @Duration int length, boolean isError) {
        //CustomToast toast = new CustomToast(activity);
        Context context = ContextHelper.getModuleContext(activity);
        LayoutInflater inflater = LayoutInflater.from(context);
        View toastRoot = inflater.inflate(getLayout(context, isError ? "toast_error_view" : "toast_view"), null);

		/*if (isError) {
			View border = ResourceUtils.getView(toastRoot, "border");
			border.setBackgroundResource(getDrawable(context, "border_error"));
		}*/

        TextView textView = ResourceUtils.getView(toastRoot, "text");
        textView.setText(text);

        TextView txtCountdown = ResourceUtils.getView(toastRoot, "countdown");

        Toast toast = Toast.makeText(
                activity,
                text,
                length
        );

        toast.setView(toastRoot);
        //toast.setGravity(Gravity.CENTER, 0, 20);
        toast.show();


        long showTime = System.currentTimeMillis();

        Handler timerHandler = new Handler(Looper.getMainLooper());
        Runnable timerRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (txtCountdown == null)
                    return;

                if (txtCountdown.getVisibility() != View.VISIBLE)
                    txtCountdown.setVisibility(View.VISIBLE);

                double duration = length == Toast.LENGTH_LONG ? 3.5d : 2d;
                double offset = (double) (System.currentTimeMillis() - showTime) / 1000d;

                double displayTime = Math.max(0, duration - offset);

                txtCountdown.setText(String.format("%.1f", displayTime));

                if (displayTime <= 0)
                    return;

                timerHandler.postDelayed(this, 50);
            }
        };

        timerHandler.post(timerRunnable);
    }

    public static boolean show(Activity activity, String text, boolean isError) {
        return show(activity, text, Toast.LENGTH_LONG, isError);
    }

    public static boolean show(Activity activity, String text, int length) {
        return show(activity, text, length, false);
    }
}
