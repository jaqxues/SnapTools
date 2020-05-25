package com.ljmu.andre.snaptools.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import com.fujiyuu75.sequent.Sequent;
import com.ljmu.andre.snaptools.R;

import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SHOW_TRANSITION_ANIMATIONS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class AnimationUtils {
    // Used to verify when a pack auth has been triggered ========================
    public static boolean hiddenAuthTriggeredBool;
    public static boolean shouldTriggerAuthVerifier;

    public static void sequentGroup(ViewGroup viewGroup) {
        sequentGroup(viewGroup.getContext(), viewGroup);
    }

    public static void sequentGroup(Context context, ViewGroup viewGroup) {
        sequentGroup(context, viewGroup, 400, 10, 0);
    }

    public static void sequentGroup(Context context, ViewGroup viewGroup, int duration, int offset, int delay) {
        if (!getPref(SHOW_TRANSITION_ANIMATIONS))
            return;

        Sequent.origin(viewGroup)
                .duration(duration)
                .offset(offset)
                .delay(delay)
                .flow(com.fujiyuu75.sequent.Direction.FORWARD)
                .anim(context, R.animator.sequent_enter)
                .start();
    }

    public static void pulseWRemove(View view, int duration) {

        Animation animation = new AlphaAnimation(0, 1);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        animation.setDuration(duration);
        view.clearAnimation();

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                ViewGroup parent = (ViewGroup) view.getParent();

                if (parent != null)
                    parent.removeView(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(animation);
    }

    public static void fadeInOutWRemove(View view) {

    }

    public static void fadeOutWRemove(View view, boolean remove) {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);

        if (remove) {
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ViewGroup parent = (ViewGroup) view.getParent();

                    if (parent != null)
                        parent.removeView(view);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Timber.e("REPEATED");
                }
            });
        }
        view.startAnimation(animation);
    }

    public static void slide(View view, Direction direction, boolean remove) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, direction.getFromX(),
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, direction.getFromY());

        animation.setDuration(500);

        if (remove) {
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ViewGroup parent = (ViewGroup) view.getParent();

                    if (parent != null)
                        parent.removeView(view);
                    else
                        throw new IllegalStateException("No parent found for view in animator");
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Timber.e("REPEATED");
                }
            });
        }
        view.startAnimation(animation);
    }

    public static void scaleUp(View v) {
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(400);
        v.startAnimation(animation);
    }

    public static void fade(View v, boolean fadeIn) {
        fade(v, 500, fadeIn);
    }

    public static void fade(View v, int duration, boolean fadeIn) {
        Animation animation = new AlphaAnimation(fadeIn ? 0 : 1, fadeIn ? 1 : 0);
        animation.setDuration(duration);
        v.clearAnimation();

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (fadeIn)
                    v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!fadeIn)
                    v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        v.startAnimation(animation);
    }

    public static void expand(View v) {
        expand(v, 1);
    }

    public static void expand(View v, float modifier) {
        expand(v, false, modifier);
    }

    public static void expand(View v, boolean horizontal, float modifier) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetSize = !horizontal ? v.getMeasuredHeight() : v.getMeasuredWidth();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        if (!horizontal)
            v.getLayoutParams().height = 1;
        else
            v.getLayoutParams().width = 1;

        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (!horizontal) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? LayoutParams.WRAP_CONTENT
                            : (int) (targetSize * interpolatedTime);
                } else
                    v.getLayoutParams().width = interpolatedTime == 1
                            ? LayoutParams.WRAP_CONTENT
                            : (int) (targetSize * interpolatedTime);
                v.requestLayout();
            }


        };

        // 1dp/ms
        a.setDuration((int) (((float) targetSize / v.getContext().getResources().getDisplayMetrics().density) * modifier));
        v.startAnimation(a);
    }

    public static void collapse(View v) {
        collapse(v, false, 1);
    }

    public static void collapse(View v, boolean horizontal, float modifier) {
        int initialSize = !horizontal ? v.getMeasuredHeight() : v.getMeasuredWidth();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    if (!horizontal)
                        v.getLayoutParams().height = initialSize - (int) (initialSize * interpolatedTime);
                    else
                        v.getLayoutParams().width = initialSize - (int) (initialSize * interpolatedTime);

                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (((float) initialSize / v.getContext().getResources().getDisplayMetrics().density) * modifier));
        v.startAnimation(a);
    }

    public static void collapse(View v, float modifier) {
        collapse(v, false, modifier);
    }

    public static void rotate(View v, boolean flip) {
        rotate(v, flip, 400);
    }

    public static void rotate(View v, boolean flip, int duration) {
        int start = flip ? 0 : 180;
        int end = flip ? 180 : 360;

        RotateAnimation rotate = new RotateAnimation(
                start,
                end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotate.setDuration(duration);
        rotate.setFillEnabled(true);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        v.startAnimation(rotate);
    }

    public enum Direction {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        private int fromX;
        private int fromY;

        Direction(int fromX, int fromY) {
            this.fromX = fromX;
            this.fromY = fromY;
        }

        public int getFromX() {
            return fromX;
        }

        public int getFromY() {
            return fromY;
        }
    }
}
