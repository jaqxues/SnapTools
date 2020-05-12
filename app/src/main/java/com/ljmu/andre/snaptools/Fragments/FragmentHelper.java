package com.ljmu.andre.snaptools.Fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.Translation.Translator;
import com.ljmu.andre.snaptools.Databases.CacheDatabase;
import com.ljmu.andre.snaptools.Databases.Tables.CompletedTutorialObject;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.TutorialFinishedEvent;
import com.ljmu.andre.snaptools.Framework.Utils.CompatibilityRedirector;
import com.ljmu.andre.snaptools.Framework.Utils.RedirectDispatcher;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.ljmu.andre.snaptools.Utils.TutorialDetail;
import com.ljmu.andre.snaptools.Utils.TutorialDetail.InflationProcessor;
import com.ljmu.andre.snaptools.Utils.TutorialDetail.MessagePosition;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.toptas.fancyshowcase.AnimationListener;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.addRelativeParamRule;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDimen;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class FragmentHelper extends Fragment implements RedirectDispatcher {
    protected int currentTutorialIndex;
    protected boolean runningTutorial;
    protected boolean isFullTutorial;
    protected boolean isForcedTutorial;
    protected FancyShowCaseView showcaseView;
    protected List<TutorialDetail> tutorialDetails = Collections.emptyList();
    protected CompatibilityRedirector redirector;
    private CBITable<CompletedTutorialObject> completedTutorialTable;

    public abstract String getName();

    public abstract Integer getMenuId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Translator.translateFragment(this);
        AnimationUtils.sequentGroup((ViewGroup) view);

        CacheDatabase.init(getActivity());
        completedTutorialTable = CacheDatabase.getTable(CompletedTutorialObject.class);

        if (!runningTutorial && shouldForceTutorial() && hasTutorial()
                && !completedTutorialTable.contains(getName())) {
            triggerOnVisible((fragmentHelper, v) -> {
                if (runningTutorial)
                    return;

                isForcedTutorial = true;
                triggerTutorial(false);
            }, 25);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetTutorial();
    }

    public void resetTutorial() {
        isFullTutorial = false;
        runningTutorial = false;
        currentTutorialIndex = -1;

        if (showcaseView != null && showcaseView.isShown())
            showcaseView.hide();
    }

    protected boolean shouldForceTutorial() {
        return true;
    }

    public FragmentHelper setTutorialDetails(List<TutorialDetail> tutorialDetails) {
        this.tutorialDetails = tutorialDetails;
        return this;
    }

    public List<TutorialDetail> getTutorials() {
        return tutorialDetails;
    }

    private void createShowcaseIfNotExist() {
        if (showcaseView == null) {
			/*showcaseView = new ShowcaseView.Builder(getActivity())
					.withNewStyleShowcase()
					.setStyle(R.style.SnapToolsShowcaseTheme)
					.build();
			showcaseView.overrideButtonClick(v -> progressTutorial());*/

			/*showcaseView = new FancyShowCaseView.Builder(getActivity())
					.focusShape(FocusShape.ROUNDED_RECTANGLE)
					.build()*/
        }
    }

    public void triggerOnVisible(OnFragmentVisibleListener visibleListener) {
        triggerOnVisible(visibleListener, 1000L);
    }

    public void triggerOnVisible(OnFragmentVisibleListener visibleListener, long delay) {
        triggerOnVisible(visibleListener, delay, 25);
    }

    public void triggerOnVisible(OnFragmentVisibleListener visibleListener, long delay, long scanDelay) {
        Observable.fromCallable(() -> {
            while (getView() == null) {
                Thread.sleep(scanDelay);
            }

            if (delay > 0)
                Thread.sleep(delay);

            return new Object();
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        visibleListener.fragmentViewVisible(FragmentHelper.this, getView());
                    }
                });
    }

    public boolean hasTutorial() {
        return false;
    }

    public void triggerTutorial(boolean isFullTutorial) {
        if (hasTutorial())
            createShowcaseIfNotExist();

        runningTutorial = true;
        currentTutorialIndex = -1;
        this.isFullTutorial = isFullTutorial;
        progressTutorial();
    }

    public void progressTutorial() {
        if (!hasTutorial())
            throw new IllegalStateException("Tried to progress a tutorial on a fragment without one");

        createShowcaseIfNotExist();

        if (++currentTutorialIndex >= getTutorials().size()) {
            Timber.i("Ended");
            finishTutorial();
            return;
        }

        TutorialDetail tutorialDetail = getTutorials().get(currentTutorialIndex);

        Timber.d("Tutorial: " + tutorialDetail);
        View targetView = tutorialDetail.getView(getActivity());

		/*if (showcaseView != null && showcaseView.isShown())
			showcaseView.removeView();*/
        InflationProcessor inflationProcessor = tutorialDetail.getInflationProcessor();

        showcaseView = new FancyShowCaseView.Builder(getActivity())
                .backgroundColor(ContextCompat.getColor(getActivity(), R.color.spotlightBackground))
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .focusBorderColor(ContextCompat.getColor(getActivity(), R.color.primaryLight))
                .focusBorderSize(1)
                .focusOn(targetView)

                .animationListener(new AnimationListener() {
                    @Override
                    public void onEnterAnimationEnd() {
                    }

                    @Override
                    public void onExitAnimationEnd() {
                        Timber.d("Exit animation ended: " + currentTutorialIndex + " | " + runningTutorial);

                        if (runningTutorial)
                            progressTutorial();
                    }
                })

                .customView(R.layout.showcase_basic, view -> {
                    if (inflationProcessor != null)
                        inflationProcessor.beforeInflation(this, tutorialDetail, (FancyShowCaseView) view.getParent());

                    ResourceUtils.<TextView>getView(view, R.id.showcase_title)
                            .setText(tutorialDetail.getTitle());
                    ResourceUtils.<TextView>getView(view, R.id.showcase_message)
                            .setText(tutorialDetail.getMessage(getActivity()));

                    Button closeButton = ResourceUtils.getView(view, R.id.showcase_button_close);
                    closeButton.setText(tutorialDetail.getCloseButtonText());
                    closeButton.setOnClickListener(v -> closeTutorial());

                    ViewGroup slideCountContainer = ResourceUtils.getView(view, R.id.showcase_slide_count_container);

                    if (isForcedTutorial) {
                        closeButton.setVisibility(View.GONE);
                        slideCountContainer.setVisibility(View.VISIBLE);

                        ResourceUtils.<TextView>getView(slideCountContainer, R.id.showcase_slide_current)
                                .setText("" + (currentTutorialIndex + 1));
                        ResourceUtils.<TextView>getView(slideCountContainer, R.id.showcase_slide_total)
                                .setText("" + getTutorials().size());
                    } else {
                        closeButton.setVisibility(View.VISIBLE);
                        slideCountContainer.setVisibility(View.GONE);
                    }

                    Button nextButton = ResourceUtils.getView(view, R.id.showcase_button_next);
                    nextButton.setEnabled(tutorialDetail.canAdvance());
                    nextButton.setText(tutorialDetail.getNextButtonText());
                    nextButton.setOnClickListener(v -> {
                        v.setEnabled(false);
                        showcaseView.hide();
                    });

                    handleTutorialMessagePosition(
                            tutorialDetail.getMessagePosition(),
                            ResourceUtils.getView(view, "showcase_message_container"),
                            showcaseView
                    );

                    if (inflationProcessor != null)
                        inflationProcessor.afterInflation(this, tutorialDetail, (FancyShowCaseView) view.getParent());
                })

                .closeOnTouch(false)
                .build();

        showcaseView.show();
    }

    private void handleTutorialMessagePosition(MessagePosition messagePosition, ViewGroup messageContainer, FancyShowCaseView showcaseView) {
        if (messagePosition == MessagePosition.BOTTOM) {
            addRelativeParamRule(messageContainer, RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (messagePosition == MessagePosition.MIDDLE) {
            addRelativeParamRule(messageContainer, RelativeLayout.CENTER_VERTICAL);
        } else if (messagePosition == MessagePosition.DYNAMIC) {
            View windowContent = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            int buttonPanelHeight = getActivity().getResources().getDimensionPixelSize(getDimen(getActivity(), "showcase_button_panel_height"));
            int heightPixels = windowContent.getHeight() - buttonPanelHeight;

            int showcaseCenter = heightPixels / 2;

            int focusYPos = showcaseView.getFocusCenterY();
            int focusHeight = showcaseView.getFocusHeight();

            Timber.d("FocusYPos: " + focusYPos);

            int focusMargin = getResources().getDimensionPixelSize(R.dimen.showcase_focus_margin);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageContainer.getLayoutParams();

            boolean alignTop = focusYPos > showcaseCenter;

            if (alignTop) {
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = heightPixels + focusMargin + (focusHeight / 2) - focusYPos;

                Timber.d("Bottom Margin: " + params.bottomMargin);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.topMargin = focusYPos + (focusHeight / 2) + focusMargin;

                Timber.d("Top Margin: " + params.topMargin);
            }

            messageContainer.setLayoutParams(params);
        }
    }

    public void finishTutorial() {
        completedTutorialTable.insert(new CompletedTutorialObject().setFragmentName(getName()));
        EventBus.getInstance().post(new TutorialFinishedEvent(getMenuId(), isFullTutorial));
        resetTutorial();
    }

    public void closeTutorial() {
        EventBus.getInstance().post(new TutorialFinishedEvent(getMenuId(), false));
        resetTutorial();
    }

    /**
     * ===========================================================================
     * Dispatched from the MainActivity
     * Should call TRUE to consume MainActivity event
     * ===========================================================================
     */
    public boolean onBackPressed() {
        if (runningTutorial) {
            return true;
        }

        return dispatchRedirection("back_press", false);
    }

    @Override
    public CompatibilityRedirector getRedirector() {
        Timber.d("Getting redirector");
        return redirector;
    }

    public interface OnFragmentVisibleListener {
        void fragmentViewVisible(FragmentHelper fragmentHelper, View v);
    }
}
