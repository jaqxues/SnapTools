package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.io.Files;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.FiltersDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.FilterObject;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.FiltersSelectionView;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.ListedViewPageAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.EditTextListener;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.FileUtils;
import com.ljmu.andre.snaptools.Utils.GlideApp;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTERS_PATH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTER_BACKGROUND_SAMPLE_PATH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTER_SCALING_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTER_SHOW_SAMPLE_BACKGROUND;
import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getFilterScaleType;
import static com.ljmu.andre.snaptools.Utils.FileUtils.createReadme;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FiltersManagerFragment extends FragmentHelper {
    public static final int SAMPLE_BACKGROUND_REQUEST_CODE = 400;

    private LinearLayout mainContainer;
    private ViewGroup emptyFiltersView;
    private SwipeRefreshLayout filtersContainerView;
    private ViewGroup previewContainerView;
    private ViewGroup settingsContainer;
    private Adapter<FilterItemViewHolder> filterAdapter;
    private RecyclerView recyclerView;
    private CBITable<FilterObject> filterObjectTable;
    private List<FilterObject> adapterFilterList = new ArrayList<>();
    private boolean hasReleasedPreview = true;
    @Nullable
    private String filterSearchField = null;
    private int totalFilters;

    @Nullable
    private BitmapDrawable sampleFilterBackground;

    private Handler longPressHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (previewContainerView == null)
                return;

            switch (msg.what) {
                case 0:
                    if (!hasReleasedPreview)
                        return;

                    hasReleasedPreview = false;

                    FilterObject filterObject = (FilterObject) msg.obj;
                    ViewGroup decor = (ViewGroup) getActivity().getWindow().getDecorView();

                    if (previewContainerView.getParent() != null) {
                        ((ViewGroup) previewContainerView.getParent()).removeView(previewContainerView);
                    }

                    boolean assignedSampleBackground = loadSampleBackgroundIntoView(
                            getDSLView(previewContainerView, "preview_sample_background")
                    );

                    File filterDir = getCreateDir(FILTERS_PATH);
                    File filterFile = new File(filterDir, filterObject.getFileName());
                    ImageView previewImageView = ResourceUtils.getDSLView(previewContainerView, "preview_imageview");
                    loadFilterIntoView(filterFile, previewImageView);
                    previewImageView.setAdjustViewBounds(!assignedSampleBackground);

                    decor.addView(previewContainerView, -1);
                    AnimationUtils.fade(previewContainerView, true);
                    previewContainerView.requestFocus();

                    Timber.d("Should Show");
                    break;
                case 1:
                    Timber.d("Cancel");
                case 2:
                    hasReleasedPreview = true;
                    AnimationUtils.fadeOutWRemove(previewContainerView, true);

                    Timber.d("Should Hide");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("RequestCode: " + requestCode);

        if (requestCode != SAMPLE_BACKGROUND_REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode != RESULT_OK) {
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Cancelled Filter Background Sample Selection"
            );

            return;
        }

        List<Uri> selectedSamplePaths = Utils.getSelectedFilesFromResult(data);

        if (selectedSamplePaths.isEmpty()) {
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "No Filter Background Samples Selected"
            );
            return;
        }

        Uri imageUri = data.getData();
        Timber.d("ImageUri: " + imageUri);

        if (imageUri == null) {
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Couldn't find Filter Background Path"
            );
            return;
        }

        String resolvedPath = null;
        try {
            resolvedPath = FileUtils.getPathFromUri(getActivity(), imageUri);
        } catch (URISyntaxException e) {
            Timber.w(e, "Couldn't resolve filter sample path");
        }

        if (resolvedPath == null) {
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Couldn't find Filter Background Path"
            );
            return;
        }

        putPref(FILTER_BACKGROUND_SAMPLE_PATH, resolvedPath);

        forceFullRecyclerRedraw();

        ImageView samplePreviewView = ResourceUtils.getDSLView(settingsContainer, "img_sample_preview");
        if (loadSampleBackgroundIntoView(samplePreviewView, true)) {
            samplePreviewView.setVisibility(View.VISIBLE);
            ResourceUtils.<TextView>getDSLView(settingsContainer, "txt_sample_preview").setText("Filter Background Sample");
        } else {
            getDSLView(settingsContainer, "img_sample_preview").setVisibility(View.GONE);
            ResourceUtils.<TextView>getDSLView(settingsContainer, "txt_sample_preview")
                    .setText("No Filter Background Sample Assigned");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.soleRegister(this);
        FiltersDatabase.init(getActivity());
        filterObjectTable = FiltersDatabase.getTable(FilterObject.class);
        FiltersSelectionView filterViews = new FiltersSelectionView();
        mainContainer = filterViews.getMainContainer(getActivity());
        previewContainerView = filterViews.getFilterPreviewContainer(getActivity());

        FrameLayout contentContainer = getDSLView(mainContainer, "content_container");

        DachshundTabLayout tabLayout = getDSLView(mainContainer, "tab_layout");
        LinearLayout.LayoutParams pagerParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ViewPager viewPager = new ViewPager(getContext());
        viewPager.setLayoutParams(pagerParams);

        List<Pair<String, View>> viewList = new ArrayList<>();
        viewList.add(Pair.create("Filters", initFiltersView(filterViews)));
        viewList.add(Pair.create("Settings", initSettingsView(filterViews)));

        viewPager.setAdapter(
                new ListedViewPageAdapter(
                        viewList
                )
        );

        tabLayout.setupWithViewPager(viewPager);
        contentContainer.addView(viewPager);

        return mainContainer;
    }

    private View initFiltersView(FiltersSelectionView filtersSelectionView) {
        ViewGroup filterMainContainer = filtersSelectionView.getFiltersView(getActivity());
        filterMainContainer.requestFocus();
        filterMainContainer.requestFocusFromTouch();
        filtersContainerView = getDSLView(filterMainContainer, "filter_container");
        emptyFiltersView = getDSLView(filterMainContainer, "empty_filters_container");

        recyclerView = (RecyclerView) LayoutInflater.from(getActivity()).inflate(
                getLayout(getActivity(), "recyclerview"), filtersContainerView, false
        );

        filtersContainerView.setOnRefreshListener(this::generateFilterData);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        File filterDir = getCreateDir(FILTERS_PATH);
        if (filterDir == null) {
            setAreFiltersEmpty(true);
            return filterMainContainer;
        }

        filterAdapter = new Adapter<FilterItemViewHolder>() {
            @Override
            public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new FilterItemViewHolder(filtersSelectionView.getFilterItemHolder(getActivity()));
            }

            @Override
            public void onBindViewHolder(FilterItemViewHolder holder, int position) {
                FilterObject filterObject = adapterFilterList.get(position);

                boolean assignedSampleBackground =
                        loadSampleBackgroundIntoView(
                                ResourceUtils.getDSLView(holder.itemView, "filter_sample_background")
                        );

                File filterFile = new File(filterDir, filterObject.getFileName());
                ImageView filterThumbnailView = ResourceUtils.getDSLView(holder.itemView, "filter_thumbnail");
                loadFilterIntoView(filterFile, filterThumbnailView);
                filterThumbnailView.setAdjustViewBounds(!assignedSampleBackground);

                ResourceUtils.<ViewGroup>getDSLView(holder.itemView, "filter_background_layout").setBackgroundColor(
                        ContextCompat.getColor(getActivity(),
                                filterObject.isActive() ? getColor(getActivity(), "success") : getColor(getActivity(), "errorWashed")
                        )
                );

                String fileDisplayName = filterObject.getFileName().replace(".png", "");
                int fullDisplayNameLength = fileDisplayName.length();
                fileDisplayName = fileDisplayName.substring(0, Math.min(fullDisplayNameLength, 20));

                if (fullDisplayNameLength > 20)
                    fileDisplayName += "...";

                ResourceUtils.<TextView>getDSLView(holder.itemView, "txt_message").setText(
                        fileDisplayName
                );

                holder.itemView.setOnClickListener(v -> {
                    filterObject.toggleActive();

                    if (filterObject.isActive())
                        filterObjectTable.insert(filterObject);
                    else
                        filterObjectTable.delete(filterObject);

                    filterAdapter.notifyItemChanged(position);
                    updateFilterStatistics();

                    PackUtils.killSCService(getActivity());
                });

                holder.itemView.setOnTouchListener((v, event) -> {
                    Timber.d("MotionEvent: " + event);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Message message = Message.obtain(longPressHandler);
                        message.what = 0;
                        message.obj = filterObject;
                        longPressHandler.sendMessageDelayed(message, 250);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (longPressHandler.hasMessages(0)) {
                            longPressHandler.removeMessages(0);
                            v.performClick();
                            return false;
                        } else {
                            Message message = Message.obtain(longPressHandler);
                            message.what = 2;
                            longPressHandler.sendMessage(message);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        longPressHandler.removeMessages(0);

                        Message message = Message.obtain(longPressHandler);
                        message.what = 1;
                        longPressHandler.sendMessage(message);
                    }

                    return true;
                });
            }

            @Override
            public int getItemCount() {
                return adapterFilterList.size();
            }
        };

        recyclerView.setAdapter(filterAdapter);
        filtersContainerView.addView(recyclerView);

        ResourceUtils.<EditText>getDSLView(filterMainContainer, "txt_search_filters").addTextChangedListener(
                new EditTextListener() {
                    @Override
                    protected void textChanged(@Nullable EditText source, Editable editable) {
                        String input = editable.toString().toLowerCase();
                        if (input.equals(filterSearchField))
                            return;

                        filterSearchField = editable.toString().toLowerCase();
                        generateFilterData();
                    }
                }
        );

        return filterMainContainer;
    }

    private View initSettingsView(FiltersSelectionView filtersSelectionView) {
        settingsContainer = filtersSelectionView.getSettingsContainer(getActivity());

        /**
         * ===========================================================================
         * Set the SELECT SAMPLE button Click Listener
         * ===========================================================================
         */
        ResourceUtils.<Button>getDSLView(settingsContainer, "button_select_sample").
                setOnClickListener(v -> {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SAMPLE_BACKGROUND_REQUEST_CODE);
                });
        // ===========================================================================

        /**
         * ===========================================================================
         * Set up the Switch to toggle the Sample background
         * ===========================================================================
         */
        ResourceUtils.<SwitchCompat>getDSLView(settingsContainer, "switch_show_sample").setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked != (boolean) getPref(FILTER_SHOW_SAMPLE_BACKGROUND)) {
                        putPref(FILTER_SHOW_SAMPLE_BACKGROUND, isChecked);
                        filterAdapter.notifyDataSetChanged();
                    }
                }
        );
        // ===========================================================================

        /**
         * ===========================================================================
         * Set the sample image
         * ===========================================================================
         */
        ImageView samplePreviewView = ResourceUtils.getDSLView(settingsContainer, "img_sample_preview");
        if (loadSampleBackgroundIntoView(samplePreviewView, true)) {
            samplePreviewView.setVisibility(View.VISIBLE);
            ResourceUtils.<TextView>getDSLView(settingsContainer, "txt_sample_preview").setText("Filter Background Sample");
        } else {
            getDSLView(settingsContainer, "img_sample_preview").setVisibility(View.GONE);
            ResourceUtils.<TextView>getDSLView(settingsContainer, "txt_sample_preview")
                    .setText("No Filter Background Sample Assigned");
        }
        // ===========================================================================

        /**
         * ===========================================================================
         * Set up the De/Activate All Filters buttons
         * ===========================================================================
         */
        getDSLView(settingsContainer, "btn_activate_filters").setOnClickListener(v -> {
            for (FilterObject filterObject : adapterFilterList)
                filterObject.setActive(true);

            if (!filterObjectTable.insertAll(adapterFilterList))
                SafeToastAdapter.showErrorToast(getActivity(), "Failed to activate all filters");

            if (filterAdapter != null)
                filterAdapter.notifyDataSetChanged();

            updateFilterStatistics();
        });
        getDSLView(settingsContainer, "btn_deactivate_filters").setOnClickListener(v -> {
            for (FilterObject filterObject : adapterFilterList)
                filterObject.setActive(false);

            if (!filterObjectTable.insertAll(adapterFilterList))
                SafeToastAdapter.showErrorToast(getActivity(), "Failed to deactivate all filters");

            if (filterAdapter != null)
                filterAdapter.notifyDataSetChanged();

            updateFilterStatistics();
        });
        // ===========================================================================

        /**
         * ===========================================================================
         * Set up the ScaleType Spinner Listener
         * ===========================================================================
         */
        ResourceUtils.<Spinner>getDSLView(settingsContainer, "spinner_scale_type").setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedType = (String) parent.getItemAtPosition(position);

                        if (getPref(FILTER_SCALING_TYPE).equals(selectedType))
                            return;

                        putAndKill(FILTER_SCALING_TYPE, selectedType, getActivity());

                        forceFullRecyclerRedraw();
                        ResourceUtils.<ImageView>getDSLView(previewContainerView, "preview_imageview").setScaleType(
                                getFilterScaleType()
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
        // ===========================================================================

        return settingsContainer;
    }

    private void setAreFiltersEmpty(boolean isEmpty) {
        if (filtersContainerView != null)
            filtersContainerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (emptyFiltersView != null)
            emptyFiltersView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private boolean loadSampleBackgroundIntoView(ImageView target) {
        return loadSampleBackgroundIntoView(target, false);
    }

    private void loadFilterIntoView(File imageFile, ImageView target) {
        GlideApp.with(getActivity())
                .load(imageFile)
                .placeholder(getDrawable(getActivity(), "spinner_fill_primary"))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
                .error(getDrawable(getActivity(), "delete2_96"))
                .into(target);
    }

    public void updateFilterStatistics() {
        long activeFilters = filterObjectTable.getRowCount("is_active = ?", new String[]{"1"});

        if (settingsContainer == null)
            return;

        TextView txtTotalFilters = getDSLView(settingsContainer, "txt_total_filters");
        TextView txtActiveFilters = getDSLView(settingsContainer, "txt_active_filters");

        if (txtTotalFilters != null) txtTotalFilters.setText(String.valueOf(totalFilters));
        if (txtActiveFilters != null) txtActiveFilters.setText(String.valueOf(activeFilters));
    }

    private void generateFilterData() {
        File filtersDir = getCreateDir(FILTERS_PATH);

        if (filtersDir == null || !filtersDir.exists()) {
            Timber.w("Couldn't create Filters folder");
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Couldn't find and create the Filters folder. Are you sure you have read/write permissions?"
            );

            setAreFiltersEmpty(true);

            return;
        }

        createReadme(
                filtersDir,
                "FolderInfo",
                "Used to store your Filters."
                        + "\nTo Use: Copy your .png files into this folder and they will be recognised by the SnapTools Filter system."
                        + "\nRecommendations: It is advised to use a filter that has an aspect ratio that is the same as your screen resolution to stop any stretching of the filters."

        );

        Observable.fromCallable(() -> {
            Iterable<File> filtersIterator = Files.fileTraverser().breadthFirst(filtersDir);

            List<FilterObject> filterObjects = new ArrayList<>();
            totalFilters = 0;

            for (File filterFile : filtersIterator) {
                if (filterFile.isDirectory() || !filterFile.getName().endsWith(".png")) {
                    continue;
                }

                totalFilters++;

                String filename = filterFile.getAbsolutePath().replace(filtersDir.getAbsolutePath() + "/", "");

                if (filterSearchField != null && !filename.toLowerCase().contains(filterSearchField))
                    continue;

                FilterObject filterMetaData = filterObjectTable.getFirst(filename);

                if (filterMetaData == null) {
                    filterMetaData = new FilterObject()
                            .setFileName(filename)
                            .setActive(false);
                }

                filterObjects.add(filterMetaData);
            }

            return filterObjects;
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<List<FilterObject>>() {
                    @Override
                    public void onNext(List<FilterObject> filterObjects) {
                        filtersContainerView.setRefreshing(false);

                        adapterFilterList.clear();

                        boolean isEmpty = filterObjects.isEmpty();
                        setAreFiltersEmpty(isEmpty);

                        if (isEmpty) {
                            updateFilterStatistics();
                            return;
                        }

                        adapterFilterList.addAll(filterObjects);

                        if (filterAdapter != null) {
                            filterAdapter.notifyDataSetChanged();
                        }

                        updateFilterStatistics();

                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //At this point the layout is complete and the
                                //dimensions of recyclerView and any child views are known.

                                if (Constants.getApkVersionCode() >= 66)
                                    AnimationUtils.sequentGroup(recyclerView);

                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        filterSearchField = null;
        generateFilterData();
    }

    private void forceFullRecyclerRedraw() {
        if (filterAdapter == null || recyclerView == null) {
            Timber.i("Not performing Filter Recycler Redraw");
            return;
        }

        filterAdapter.notifyDataSetChanged();
        recyclerView.getRecycledViewPool().clear();
        recyclerView.removeAllViews();
    }

    private boolean loadSampleBackgroundIntoView(ImageView target, boolean bypassPreference) {
        if (!(boolean) getPref(FILTER_SHOW_SAMPLE_BACKGROUND) && !bypassPreference) {
            target.setImageDrawable(null);
            target.setBackgroundColor(Color.BLACK);
            return false;
        }

        String sampleBackgroundPath = getPref(FILTER_BACKGROUND_SAMPLE_PATH);

        if (sampleBackgroundPath == null || sampleBackgroundPath.isEmpty()) {
            Timber.i("No sample background assigned");

            target.setImageDrawable(null);
            target.setBackgroundColor(Color.BLACK);
            return false;
        }

        File sampleBackgroundFile = new File(sampleBackgroundPath);

        GlideApp.with(getActivity())
                .load(sampleBackgroundFile)
                .placeholder(getDrawable(getActivity(), "spinner_fill_primary"))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
                .error(getDrawable(getActivity(), "delete2_96"))
                .into(target);

        target.setBackgroundColor(Color.BLACK);
        return true;
    }

    private class FilterItemViewHolder extends ViewHolder {
        public FilterItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.soleUnregister(this);
    }


    @Override
    public String getName() {
        return "Custom Filters";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
