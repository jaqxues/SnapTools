package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import androidx.annotation.MainThread;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ljmu.andre.snaptools.R;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.OnViewInflateListener;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ShowcaseFactory {
    public static FancyShowCaseView.Builder getDefaultShowcase(Activity activity, OnDefaultShowcaseInflated defaultShowcaseInflated) {
        return getDefaultShowcase(activity, view -> defaultShowcaseInflated.showcaseInflated(
                (FancyShowCaseView) view.getParent(),
                ResourceUtils.getView(view, R.id.showcase_message_container),
                ResourceUtils.getView(view, R.id.showcase_title),
                ResourceUtils.getView(view, R.id.showcase_message),
                ResourceUtils.getView(view, R.id.showcase_button_close),
                ResourceUtils.getView(view, R.id.showcase_button_next)
        ));
    }

    public static FancyShowCaseView.Builder getDefaultShowcase(Activity activity, OnViewInflateListener inflateListener) {
        return new FancyShowCaseView.Builder(activity)
                .backgroundColor(ContextCompat.getColor(activity, R.color.backgroundTertiaryWashed))
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .focusBorderColor(ContextCompat.getColor(activity, R.color.primaryLight))
                .focusBorderSize(1)

                .customView(R.layout.showcase_basic, inflateListener)

                .closeOnTouch(false);
    }

    @MainThread
    public static void detatchCurrentShowcase(Activity activity) {
        try {
            View androidContent = activity.findViewById(android.R.id.content);
            ViewGroup root = (ViewGroup) androidContent.getParent().getParent();

            FancyShowCaseView visibleView = (FancyShowCaseView) root.findViewWithTag("ShowCaseViewTag");

            if (visibleView != null)
                visibleView.removeView();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public interface OnDefaultShowcaseInflated {
        void showcaseInflated(FancyShowCaseView showCaseView, ViewGroup messageContainer, TextView txtTitle, TextView txtMessage, Button btnClose, Button btnNext);
    }
}
