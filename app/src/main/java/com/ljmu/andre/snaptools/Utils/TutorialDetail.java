package com.ljmu.andre.snaptools.Utils;

import android.app.Activity;
import androidx.annotation.Nullable;
import android.view.View;

import com.google.common.base.MoreObjects;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;

import me.toptas.fancyshowcase.FancyShowCaseView;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.scrollTo;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TutorialDetail {
    private String title;
    private String message;
    private String closeButtonText = "Close";
    private String nextButtonText = "Next";
    private boolean canAdvance = true;
    private View view;
    private Integer viewId;
    private String dslViewId;
    private MessagePosition messagePosition = MessagePosition.DYNAMIC;

    private ViewProcessor viewProcessor;
    private MessageProcessor messageProcessor = ((activity, tutorialDetail) -> message);
    private InflationProcessor inflationProcessor = new InflationProcessor() {
        @Override
        public boolean beforeInflation(FragmentHelper fragment, TutorialDetail tutorialDetail, FancyShowCaseView showcaseView) {
            return true;
        }

        @Override
        public void afterInflation(FragmentHelper fragment, TutorialDetail tutorialDetail, FancyShowCaseView showcaseView) {
        }
    };

    public String getTitle() {
        return title;
    }

    public TutorialDetail setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage(Activity activity) {
        return messageProcessor.getMessage(activity, this);
    }

    public TutorialDetail setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCloseButtonText() {
        return closeButtonText;
    }

    public TutorialDetail setCloseButtonText(String closeButtonText) {
        this.closeButtonText = closeButtonText;
        return this;
    }

    public String getNextButtonText() {
        return nextButtonText;
    }

    public TutorialDetail setNextButtonText(String nextButtonText) {
        this.nextButtonText = nextButtonText;
        return this;
    }

    public boolean canAdvance() {
        return canAdvance;
    }

    public TutorialDetail setCanAdvance(boolean canAdvance) {
        this.canAdvance = canAdvance;
        return this;
    }

    public TutorialDetail setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
        return this;
    }

    public TutorialDetail setView(View view) {
        this.view = view;
        return this;
    }

    public TutorialDetail setViewProcessor(ViewProcessor viewProcessor) {
        this.viewProcessor = viewProcessor;
        return this;
    }

    @Nullable
    public <T> T getTarget(Activity activity) {
        return null;
    }

    @Nullable
    public View getView(Activity activity) {
        if (getViewId() != null) {
            View view = activity.findViewById(getViewId());

            if (view == null)
                Timber.e(new Exception("Couldn't find tutorial view using id: " + toString()));

            return scrollTo(view);
        } else if (getDslViewId() != null) {
            View view = getDSLView(activity, getDslViewId());

            if (view == null)
                Timber.e(new Exception("Couldn't find tutorial view using DSL id: " + toString()));

            return scrollTo(view);
        } else if (view != null) {
            return view;
        } else if (viewProcessor != null) {
            View view = viewProcessor.getView(activity);

            if (view == null)
                Timber.e(new Exception("Couldn't find tutorial view using view processor: " + toString()));

            return view;
        }

        return null;
    }

    public Integer getViewId() {
        return viewId;
    }

    public String getDslViewId() {
        return dslViewId;
    }

    public TutorialDetail setDslViewId(String dslViewId) {
        this.dslViewId = dslViewId;
        return this;
    }

    public TutorialDetail setViewId(Integer viewId) {
        this.viewId = viewId;
        return this;
    }

    public MessagePosition getMessagePosition() {
        return messagePosition;
    }

    public TutorialDetail setMessagePosition(MessagePosition messagePosition) {
        this.messagePosition = messagePosition;
        return this;
    }

    public InflationProcessor getInflationProcessor() {
        return inflationProcessor;
    }

    public TutorialDetail setInflationProcessor(InflationProcessor inflationProcessor) {
        this.inflationProcessor = inflationProcessor;
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("title", title)
                .add("message", message)
                .add("closeButtonText", closeButtonText)
                .add("nextButtonText", nextButtonText)
                .add("canAdvance", canAdvance)
                .add("view", view)
                .add("viewId", viewId)
                .add("dslViewId", dslViewId)
                .add("messagePosition", messagePosition)
                .toString();
    }

    public enum MessagePosition {
        DYNAMIC, BOTTOM, TOP, MIDDLE
    }

    public interface ViewProcessor {
        View getView(Activity activity);
    }

    public interface MessageProcessor {
        String getMessage(Activity activity, TutorialDetail tutorialDetail);
    }

    public interface InflationProcessor {
        /**
         * ===========================================================================
         * Should return FALSE if the default action should also trigger
         * ===========================================================================
         */
        boolean beforeInflation(FragmentHelper fragment, TutorialDetail tutorialDetail, FancyShowCaseView showcaseView);

        void afterInflation(FragmentHelper fragment, TutorialDetail tutorialDetail, FancyShowCaseView showcaseView);
    }
}
