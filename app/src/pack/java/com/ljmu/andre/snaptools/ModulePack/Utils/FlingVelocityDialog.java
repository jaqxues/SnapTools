package com.ljmu.andre.snaptools.ModulePack.Utils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.view.VelocityTrackerCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedDialogExtension;
import com.ljmu.andre.snaptools.Utils.Callable;

import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getStyle;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FlingVelocityDialog implements ThemedDialogExtension {
    private Context context;
    @Nullable
    private String message;
    @Nullable
    private Callable<Integer> callable;
    private FrameLayout flingManager;
    private LinearLayout velocityDisplayLayout;
    private TextView txtStoredVelocity;
    private Button btnUseVelocity;
    private double maxStoredVelocity = 0;
    private VelocityTracker velocityTracker;

    @Override
    public void onCreate(LayoutInflater inflater, View parent, ViewGroup content, ThemedDialog themedDialog) {
        context = content.getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height, 1));

        if (message != null) {
            TextView txtMessage = new TextView(context);
            txtMessage.setTextAppearance(context, getStyle(context, "DefaultText"));
            txtMessage.setText(message);
            txtMessage.setGravity(Gravity.CENTER_HORIZONTAL);

            container.addView(txtMessage);
        }

        txtStoredVelocity = new TextView(context);
        txtStoredVelocity.setTextAppearance(context, getStyle(context, "DefaultText"));
        txtStoredVelocity.setText((int) maxStoredVelocity + "px/s");
        txtStoredVelocity.setGravity(Gravity.CENTER_VERTICAL);

        velocityDisplayLayout = ViewFactory.makeLabelled("Velocity", txtStoredVelocity, LinearLayout.HORIZONTAL, null, null);
        velocityDisplayLayout.setVisibility(View.GONE);
        velocityDisplayLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        container.addView(velocityDisplayLayout);

        flingManager = new FrameLayout(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return handleTouchEvent(event);
            }
        };
        flingManager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
        container.addView(flingManager);

        LinearLayout buttonContainer = new LinearLayout(context);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutParams okayParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        okayParams.gravity = Gravity.CENTER_HORIZONTAL;

        Button cancelButton =
                ViewFactory.getButton(
                        context,
                        "Cancel",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                themedDialog.dismiss();
                            }
                        },
                        okayParams
                );
        cancelButton.setBackgroundResource(getDrawable(context, "error_button"));
        cancelButton.setTextAppearance(context, getStyle(context, "ErrorButton"));
        cancelButton.setGravity(Gravity.CENTER_HORIZONTAL);
        buttonContainer.addView(cancelButton);

        btnUseVelocity =
                ViewFactory.getButton(
                        context,
                        "Use Velocity",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (callable != null)
                                    callable.call((int) maxStoredVelocity);

                                themedDialog.dismiss();
                            }
                        },
                        okayParams
                );
        btnUseVelocity.setGravity(Gravity.CENTER_HORIZONTAL);
        btnUseVelocity.setVisibility(View.GONE);
        buttonContainer.addView(btnUseVelocity);

        container.addView(buttonContainer);
        content.addView(container);
    }

    private boolean handleTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null)
                    velocityTracker = VelocityTracker.obtain();
                else
                    velocityTracker.clear();

                maxStoredVelocity = 0;
                //v.getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker == null)
                    velocityTracker = VelocityTracker.obtain();

                //v.getParent().requestDisallowInterceptTouchEvent(true);
                velocityTracker.addMovement(event);

                velocityTracker.computeCurrentVelocity(1000, 30000);

                float xVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                double velocityHypot = Math.hypot(xVelocity, yVelocity);

                Timber.d("Total Velocity: " + velocityHypot);

                if (velocityHypot > maxStoredVelocity)
                    maxStoredVelocity = velocityHypot;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                triggerFlingFlash();
                velocityTracker.recycle();
                velocityTracker = null;
                velocityDisplayLayout.setVisibility(View.VISIBLE);
                btnUseVelocity.setVisibility(View.VISIBLE);
                txtStoredVelocity.setText((int) maxStoredVelocity + "px/s");
        }

        return true;
    }

    private void triggerFlingFlash() {
        flingManager.setBackgroundResource(getColor(context, "successWashed"));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                flingManager.setBackground(null);
            }
        }, 1000);
    }

    public FlingVelocityDialog setMessage(@Nullable String message) {
        this.message = message;
        return this;
    }

    public FlingVelocityDialog setCallable(@Nullable Callable<Integer> callable) {
        this.callable = callable;
        return this;
    }
}
