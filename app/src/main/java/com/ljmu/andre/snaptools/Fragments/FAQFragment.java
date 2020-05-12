package com.ljmu.andre.snaptools.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Networking.Helpers.GetFAQs;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_FAQS;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;
import static com.ljmu.andre.snaptools.Utils.StringUtils.safeNull;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FAQFragment extends FragmentHelper {
    @BindView(R.id.faq_container)
    ViewGroup faqContainer;
    @BindView(R.id.txt_last_checked)
    TextView txtLastChecked;
    @BindView(R.id.scroll_container)
    ScrollView scrollContainer;
    @BindView(R.id.txt_main_header)
    TextView txtMainHeader;
    @BindView(R.id.faq_search)
    EditText faqSearch;
    @BindView(R.id.search_container)
    LinearLayout searchContainer;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar_content)
    LinearLayout toolbarContent;
    @BindView(R.id.toolbar_img_arrow)
    ImageView toolbarImgArrow;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_main_container)
    LinearLayout toolbarMainContainer;
    @BindView(R.id.toolbar_viewholder)
    LinearLayout toolbarViewholder;
    private FAQViewProvider viewProvider;
    private Unbinder unbinder;

    private List<FAQHeaderItem> faqHeaderItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup layoutContainer = (ViewGroup) inflater.inflate(R.layout.frag_faq, container, false);
        EventBus.soleRegister(this);
        unbinder = ButterKnife.bind(this, layoutContainer);
        viewProvider = new FAQViewProvider(getActivity());

        buildFAQList();

        faqSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            putPref(LAST_CHECK_FAQS, 0L);
            buildFAQList();
        });

        return layoutContainer;
    }

    private void buildFAQList() {
        swipeRefreshLayout.setRefreshing(true);

        new GetFAQs().smartFetch(
                getActivity(), new ObjectResultListener<byte[]>() {
                    @Override
                    public void success(String message, byte[] faqByteArray) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (faqByteArray == null || faqByteArray.length <= 0) {
                            SafeToast.show(
                                    getActivity(),
                                    "Failed to retrieve FAQs list",
                                    true
                            );

                            return;
                        }

                        String faqString = new String(faqByteArray, Charset.defaultCharset());

                        Timber.d("Success: " + faqString);

                        try {
                            String[] splitString = faqString.split("\n");
                            Timber.d("Split: " + Arrays.toString(splitString));

                            handleFAQResults(splitString);
                        } catch (Exception e) {
                            Timber.e(e);
                        }

                        updateLastChecked();
                    }

                    @Override
                    public void error(String message, Throwable t, int errorCode) {
                        if (swipeRefreshLayout != null)
                            swipeRefreshLayout.setRefreshing(false);

                        SafeToast.show(
                                getActivity(),
                                "Failed to retrieve FAQs list",
                                true
                        );
                    }
                });
    }

    private void performSearch(String searchTermCased) {
        String searchTerm = searchTermCased.toLowerCase();
        Observable.fromCallable(() -> {
            List<FAQItem> validItems = new ArrayList<>();
            List<FAQItem> invalidItems = new ArrayList<>();

            for (FAQHeaderItem headerItem : faqHeaderItems) {
                boolean isHeaderValid = false;
                boolean isAChildValid = false;

                if (headerItem.getText().toLowerCase().contains(searchTerm))
                    isHeaderValid = true;

                for (FAQItem faqItem : headerItem.faqChildren) {
                    boolean isSubHeaderValid = false;
                    boolean isCurrentItemValid = false;

                    if (faqItem.getText().toLowerCase().contains(searchTerm)) {
                        isAChildValid = true;
                        isCurrentItemValid = true;

                        if (faqItem instanceof FAQSubHeaderItem)
                            isSubHeaderValid = true;
                    }

                    if (faqItem instanceof FAQSubHeaderItem) {
                        for (FAQItem subItem : ((FAQSubHeaderItem) faqItem).getFaqChildren()) {
                            Timber.d("SubFAQItem: " + subItem + " | " + subItem.getText());

                            if (isHeaderValid || isSubHeaderValid || subItem.getText().toLowerCase().contains(searchTerm)) {
                                isAChildValid = true;
                                isSubHeaderValid = true;
                                validItems.add(subItem);
                            } else {
                                invalidItems.add(subItem);
                            }
                        }
                    }

                    if (isHeaderValid || isCurrentItemValid || isSubHeaderValid) {
                        isAChildValid = true;
                        validItems.add(faqItem);
                    } else {
                        invalidItems.add(faqItem);
                    }

                    Timber.d("FAQItem: " + faqItem + " | " + faqItem.getText());
                }

                if (isHeaderValid || isAChildValid) {
                    validItems.add(headerItem);
                } else {
                    invalidItems.add(headerItem);
                }

                Timber.d("FAQHeader: " + headerItem + " | " + headerItem.getText());
            }

            return Pair.create(validItems, invalidItems);
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Pair<List<FAQItem>, List<FAQItem>>>() {
                    @Override
                    public void onNext(Pair<List<FAQItem>, List<FAQItem>> pair) {
                        List<FAQItem> validItems = pair.first;
                        List<FAQItem> invalidItems = pair.second;

                        for (FAQItem validItem : validItems) {
                            Timber.d("ValidPathitem: " + validItem + " | " + validItem.getText());
                            validItem.getMainContainer().setVisibility(View.VISIBLE);
                        }

                        for (FAQItem invalidItem : invalidItems) {
                            Timber.d("InvalidPathitem: " + invalidItem + " | " + invalidItem.getText());
                            invalidItem.getMainContainer().setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void handleFAQResults(String[] faqArray) {
        faqContainer.removeAllViews();
        toolbarViewholder.removeAllViews();

        FAQHeaderItem currentHeaderItem = null;
        FAQSubHeaderItem currentSubHeaderItem = null;
        boolean parsingToolbar = false;

        for (String faqItemText : faqArray) {
            if (faqItemText == null || faqItemText.isEmpty())
                continue;

            if (faqItemText.startsWith("---")) {
                ViewGroup answerLayout = viewProvider.getAnswerLayout(faqItemText.replaceFirst("---", ""));

                FAQAnswerItem answerItem = new FAQAnswerItem(answerLayout);

                if (currentSubHeaderItem != null) {
                    currentSubHeaderItem.addFAQChild(answerItem);
                } else if (currentHeaderItem != null) {
                    currentHeaderItem.addFAQChild(answerItem);
                }
            } else if (faqItemText.startsWith("--")) {
                if (currentHeaderItem == null)
                    throw new IllegalStateException("No header present");

                ViewGroup subHeader = viewProvider.getSubHeader(faqItemText.replaceFirst("--", ""));
                FAQSubHeaderItem subHeaderItem = new FAQSubHeaderItem(subHeader);
                currentHeaderItem.addFAQChild(subHeaderItem);

                currentSubHeaderItem = subHeaderItem;
            } else if (faqItemText.startsWith("-")) {
                ViewGroup header = viewProvider.getHeader(faqItemText.replaceFirst("-", ""));

                FAQHeaderItem headerItem = new FAQHeaderItem(header);
                faqContainer.addView(header);
                faqHeaderItems.add(headerItem);

                currentHeaderItem = headerItem;
                currentSubHeaderItem = null;
            } else if (faqItemText.startsWith("/---")) {
                if (!parsingToolbar)
                    continue;

                ViewGroup answerLayout = viewProvider.getAnswerLayout(faqItemText.replaceFirst("/---", ""));

                FAQAnswerItem answerItem = new FAQAnswerItem(answerLayout);

                if (currentSubHeaderItem != null)
                    currentSubHeaderItem.addFAQChild(answerItem);
                else {
                    toolbarViewholder.addView(answerItem.getMainContainer());
                }
            } else if (faqItemText.startsWith("/--")) {
                if (!parsingToolbar)
                    continue;

                ViewGroup subHeader = viewProvider.getSubHeader(faqItemText.replaceFirst("/--", ""));
                FAQSubHeaderItem subHeaderItem = new FAQSubHeaderItem(subHeader);
                toolbarViewholder.addView(subHeaderItem.getMainContainer());

                currentSubHeaderItem = subHeaderItem;
            } else if (faqItemText.startsWith("/-")) {
                parsingToolbar = true;
                currentSubHeaderItem = null;

                toolbarTitle.setText(faqItemText.replaceFirst("/-", ""));
                toolbarMainContainer.setVisibility(View.VISIBLE);
            }
        }

        AnimationUtils.sequentGroup(faqContainer);
    }

    private void updateLastChecked() {
        Long lastCheckedTimestamp = getPref(LAST_CHECK_FAQS);

        if (lastCheckedTimestamp == 0L) {
            txtLastChecked.setVisibility(View.GONE);
            return;
        }

        String formattedTime = (String) DateUtils.getRelativeDateTimeString(
                getActivity(),
                lastCheckedTimestamp,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );

        txtLastChecked.setText(getSpannedHtml(
                "Last Checked: " + htmlHighlight(formattedTime)
        ));
        txtLastChecked.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public String getName() {
        return "FAQs";
    }

    @Override
    public Integer getMenuId() {
        return R.id.nav_faq;
    }

    @OnClick(R.id.img_search)
    public void onSearchClicked() {
        if (searchContainer.getVisibility() == View.VISIBLE) {
            performSearch("");

            AnimationUtils.collapse(searchContainer, true, 2f);
            AnimationUtils.expand(txtMainHeader, true, 2f);
        } else {
            faqSearch.requestFocus();
            faqSearch.requestFocusFromTouch();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Assert.notNull("Null InputMethodManager", imm);
            imm.showSoftInput(
                    faqSearch,
                    InputMethodManager.SHOW_IMPLICIT
            );

            AnimationUtils.expand(searchContainer, true, 2f);
            AnimationUtils.collapse(txtMainHeader, true, 2f);
        }
    }

    @OnClick(R.id.toolbar)
    public void onToolbarClicked() {
        if (toolbarContent.getVisibility() == View.VISIBLE) {
            AnimationUtils.collapse(toolbarContent);
            AnimationUtils.rotate(toolbarImgArrow, true);

            viewProvider.collapseLastHeader();
            viewProvider.setExpandedHeader(null);
            viewProvider.setExpandedHeaderArrow(null);
            viewProvider.setExpandedSubHeader(null);
            viewProvider.setExpandedSubHeaderArrow(null);
        } else {
            AnimationUtils.expand(toolbarContent);
            AnimationUtils.rotate(toolbarImgArrow, false);

            viewProvider.collapseLastHeader();
            viewProvider.setExpandedHeader(toolbarContent);
            viewProvider.setExpandedHeaderArrow(toolbarImgArrow);
        }
    }

    public abstract static class FAQItem {
        TextView txtView;
        private ViewGroup container;

        FAQItem(ViewGroup container) {
            this.container = container;
        }

        public abstract ViewGroup getMainContainer();

        public ViewGroup getContainer() {
            return container;
        }

        public String getText() {
            return safeNull(txtView != null ? txtView.getText().toString() : null);
        }
    }

    public class FAQHeaderItem extends FAQItem {
        ViewGroup headerView;
        ImageView dropdownArrow;
        private List<FAQItem> faqChildren = new ArrayList<>();

        FAQHeaderItem(ViewGroup headerView) {
            super(getDSLView(headerView, "header_faq_container"));
            this.headerView = headerView;
            txtView = getDSLView(headerView, "text_view");
            dropdownArrow = getDSLView(headerView, "faq_img_arrow");
        }

        @Override
        public ViewGroup getMainContainer() {
            return headerView;
        }

        void addFAQChild(FAQItem faqItem) {
            faqChildren.add(faqItem);
            getContainer().addView(faqItem.getMainContainer());
        }

        // Unused for now... Was intended for threading purposes
        void attachChildren() {
            for (FAQItem child : getFaqChildren()) {
                getContainer().addView(child.getMainContainer());

                if (child instanceof FAQHeaderItem)
                    ((FAQHeaderItem) child).attachChildren();
            }
        }

        List<FAQItem> getFaqChildren() {
            return faqChildren;
        }
    }

    public class FAQSubHeaderItem extends FAQHeaderItem {
        FAQSubHeaderItem(ViewGroup container) {
            super(container);
        }
    }

    public class FAQAnswerItem extends FAQItem {
        FAQAnswerItem(ViewGroup container) {
            super(container);
            txtView = getDSLView(container, "text_view");
        }

        @Override
        public ViewGroup getMainContainer() {
            return getContainer();
        }
    }
}
