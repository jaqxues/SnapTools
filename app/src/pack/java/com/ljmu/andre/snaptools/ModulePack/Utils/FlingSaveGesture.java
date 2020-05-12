package com.ljmu.andre.snaptools.ModulePack.Utils;

import androidx.core.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.ljmu.andre.snaptools.ModulePack.SavingUtils.Snaps.Snap.SnapType;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getFlingVelocity;


/**
 * Created by Andre on 07/09/2016.
 */
public class FlingSaveGesture implements GestureEvent {
    private int minVelocityThreshhold;
    private boolean hasAssignedStart = false;
    private boolean hasMoved = false;
    private boolean hasSaved = false;
    private VelocityTracker velocityTracker;

    public FlingSaveGesture(SnapType snapType) {
        minVelocityThreshhold = getFlingVelocity(snapType);
    }

    public ReturnType onTouch(View v, MotionEvent event) {
        Timber.d("Touch: " + event.getAction());

        Timber.d("Position: " + event.getRawX() + " " + event.getRawY());

        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null)
                    velocityTracker = VelocityTracker.obtain();
                else
                    velocityTracker.clear();

                //v.getParent().requestDisallowInterceptTouchEvent(true);
                hasAssignedStart = true;
                return ReturnType.PROCESSING;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker == null)
                    velocityTracker = VelocityTracker.obtain();

                //v.getParent().requestDisallowInterceptTouchEvent(true);
                velocityTracker.addMovement(event);

                velocityTracker.computeCurrentVelocity(1000, minVelocityThreshhold);

                float xVelocity = VelocityTrackerCompat.getXVelocity(velocityTracker, pointerId);
                float yVelocity = VelocityTrackerCompat.getYVelocity(velocityTracker, pointerId);
                double velocityHypot = Math.hypot(xVelocity, yVelocity);

                Timber.d("XVel: " + xVelocity + "  YVel: " + yVelocity);
                Timber.d("Total Velocity: " + velocityHypot + " / " + minVelocityThreshhold);

                hasMoved = velocityHypot > 100;

                if (hasAssignedStart && !hasSaved && velocityHypot > minVelocityThreshhold) {
                    Timber.d("Performed swipe: " + velocityHypot);
                    hasSaved = true;

                    return ReturnType.SAVE;
                }

                return ReturnType.PROCESSING;
            case MotionEvent.ACTION_UP:
                if (!hasMoved) {
                    reset();
                    return ReturnType.TAP;
                }

            case MotionEvent.ACTION_CANCEL:
                reset();
        }

        return ReturnType.FAILED;
    }

    public void reset() {
        if (velocityTracker != null)
            velocityTracker.recycle();

        velocityTracker = null;
        hasAssignedStart = false;
        hasMoved = false;
        hasSaved = false;
    }
}


