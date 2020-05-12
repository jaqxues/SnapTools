package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.Dialogs.Content.Options.OptionsButtonData;
import com.ljmu.andre.snaptools.Dialogs.Content.Progress;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.LensDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensObject;
import com.ljmu.andre.snaptools.ModulePack.Events.LensEventRequest;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.LensViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.InterruptFlag;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result;
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.EditTextListener;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.KILL_SC_ON_CHANGE;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LENS_SELECTOR_SPAN;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LensSettingsFragment extends FragmentHelper {
    private static final int MERGE_LENSES_REQUEST = 107;

    /**
     * ===========================================================================
     * View Bindings
     * ===========================================================================
     */
    private ViewGroup emptyLensesView;
    private ViewGroup settingsView;
    private ViewGroup lensesView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView txtActiveLenses;
    private TextView txtTotalLenses;

    // ===========================================================================

    private CBITable<LensObject> lensTable;
    private List<ExpandableItemEntity> lensTypes = new ArrayList<>();
    private ExpandableItemAdapter<ExpandableItemEntity> adapter;
    private String lensNameFilter;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MERGE_LENSES_REQUEST) {
            if (resultCode != RESULT_OK) {
                SafeToast.show(getActivity(), "Cancelled merge request", Toast.LENGTH_LONG, true);
                return;
            }

            List<String> lensPaths = new ArrayList<>();

            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri lensUri = clipData.getItemAt(i).getUri();
                    String lensPath = lensUri.getPath();

                    if (!lensPath.endsWith(".db"))
                        continue;

                    lensPaths.add(lensUri.getPath());
                }
            } else if (data.getData() != null) {
                String lensPath = data.getData().getPath();

                if (!lensPath.endsWith(".db")) {
                    SafeToast.show(getActivity(), "Incorrect file type", Toast.LENGTH_LONG, true);
                    return;
                }

                lensPaths.add(data.getData().getPath());
            }

            if (lensPaths.isEmpty()) {
                SafeToast.show(getActivity(), "No valid lens databases selected", Toast.LENGTH_LONG, true);
                return;
            }

            InterruptFlag interruptFlag = new InterruptFlag();

            ThemedDialog progressDialog = new ThemedDialog(getActivity())
                    .setTitle("Merging Lenses")
                    .setExtension(
                            new Progress()
                                    .setMessage(
                                            "Currently merging " + lensPaths.size() + " lens databases."
                                                    + "\nDepending on how many lenses are in each database this can take quite some time as there is a lot of work going on in the background"
                                    )
                                    .setCancelable(true)
                                    .setCancelCallback(aVoid -> interruptFlag.interrupt())
                    );
            progressDialog.show();

            Observable.fromCallable(() ->
                    LensDatabase.mergeLensDatabases(getActivity(), lensPaths, interruptFlag)
            ).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new SimpleObserver<Result<Boolean, Point>>() {
                        @Override
                        public void onError(Throwable e) {
                            progressDialog.dismiss();
                            Timber.e(e, "Fatal error merging lenses");

                            SafeToastAdapter.showErrorToast(
                                    getActivity(),
                                    "Fatal error merging lenses... Reason unknown"
                            );
                        }

                        @Override
                        public void onNext(Result<Boolean, Point> mergedLensResult) {
                            progressDialog.dismiss();
                            Point successDuplicatePoint = mergedLensResult.getValue();

                            if (!mergedLensResult.getKey()) {

                                SafeToastAdapter.showErrorToast(
                                        getActivity(),
                                        String.format("Couldn't insert all lenses...\nInserted %s lenses\nRemoved %s duplicates",
                                                successDuplicatePoint.x, successDuplicatePoint.y)
                                );
                            } else {

                                SafeToastAdapter.showDefaultToast(
                                        getActivity(),
                                        String.format("Inserted %s lenses\nRemoved %s duplicates",
                                                successDuplicatePoint.x, successDuplicatePoint.y)
                                );
                            }

                            if (successDuplicatePoint.x > 0 || successDuplicatePoint.y > 0)
                                generateMetaData();
                        }
                    });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.soleRegister(this);
        LensDatabase.init(getActivity());
        lensTable = LensDatabase.getTable(LensObject.class);
        ViewGroup mainContainer = new LensViewProvider(
                getActivity(),
                this::handleUIEvent,
                view -> settingsView = (ViewGroup) view,
                view -> lensesView = (ViewGroup) view
        ).getMainContainer();

        bindViews();

        initRecyclerView();
        initToolbar();

        return mainContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        generateMetaData();
        lensNameFilter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        lensTypes.clear();
    }

    private void bindViews() {
        emptyLensesView = getDSLView(lensesView, "empty_lenses_container");
        swipeRefreshLayout = getDSLView(lensesView, "swipe_refresh_lenses");
        recyclerView = getDSLView(lensesView, "recycler_lenses");
        txtActiveLenses = getDSLView(settingsView, "txt_active_lenses");
        txtTotalLenses = getDSLView(settingsView, "txt_total_lenses");
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener(this::generateMetaData);

        // Statement to block invalid span sizes =====================================
        if ((Integer) getPref(LENS_SELECTOR_SPAN) <= 0)
            putPref(LENS_SELECTOR_SPAN, 1);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getPref(LENS_SELECTOR_SPAN)));
        recyclerView.setVerticalScrollBarEnabled(true);

        adapter = new ExpandableItemAdapter<>(lensTypes);
        adapter.bindToRecyclerView(recyclerView);
        adapter.addType(LensTypeEntity.type, getLayout(getContext(), "item_listable_head"));
        adapter.addType(LensObject.type, getLayout(getContext(), "item_lens"));
        adapter.setEmptyView(getLayout(getContext(), "layout_empty_lenses"));

        updateSpanSizeLookup();
    }

    private void initToolbar() {
        ResourceUtils.<EditText>getDSLView(lensesView, "txt_lens_filter")
                .addTextChangedListener(new EditTextListener() {
                    @Override
                    protected void textChanged(@Nullable EditText source, Editable editable) {
                        String s = editable.toString();
                        lensNameFilter = s.isEmpty() ? null : s.replace(" ", "_");
                        generateMetaData();
                    }
                });

        getDSLView(lensesView, "img_toolbar_arrow")
                .setRotation(180);
    }

    private void updateSpanSizeLookup() {
        int lensSpan = getPref(LENS_SELECTOR_SPAN);
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(lensSpan);

        adapter.setSpanSizeLookup((gridLayoutManager, position) -> {
            int level = lensTypes.get(position).getLevel();

            switch (level) {
                case 0:
                    return lensSpan;
                default:
                    return 1;
            }
        });
    }

    private void generateMetaData() {
        swipeRefreshLayout.setRefreshing(true);

        Observable.fromCallable(
                () -> {
                    Map<String, LensTypeEntity> lensTypeMap = new HashMap<>();

                    Collection<LensObject> lenses;

                    if (lensNameFilter == null) {
                        lenses = lensTable.getAll(
                                new QueryBuilder()
                                        .addSort("isActive", "DESC")
                        );
                    } else {
                        lenses = lensTable.getAll(
                                new QueryBuilder()
                                        .addSelection("code", "LIKE", "%" + lensNameFilter + "%")
                                        .addSort("isActive", "DESC")
                        );
                    }

                    for (LensObject lens : lenses) {
                        if (lens.code == null || lens.id == null)
                            continue;

                        LensTypeEntity typeEntity;
                        String lensCategory = getLensCategory(lens);

                        switch (lensCategory) {
                            case "favourited":
                                typeEntity = lensTypeMap.get("favourited");

                                if (typeEntity == null) {
                                    typeEntity = new LensTypeEntity("favourited", "FAVOURITED", new ArrayList<>());
                                    lensTypeMap.put("favourited", typeEntity);
                                }
                                break;
                            case "bitmoji":
                                typeEntity = lensTypeMap.get("bitmoji");

                                if (typeEntity == null) {
                                    typeEntity = new LensTypeEntity("bitmoji", "AVATARS", new ArrayList<>());
                                    lensTypeMap.put("bitmoji", typeEntity);
                                }
                                break;
                            default:
                                typeEntity = lensTypeMap.get(lens.mType);

                                if (typeEntity == null) {
                                    typeEntity = new LensTypeEntity(lens.mType, lens.mType, new ArrayList<>());
                                    lensTypeMap.put(lens.mType, typeEntity);
                                }
                        }

                        typeEntity.addSubItem(lens);
                    }

                    return lensTypeMap.values();
                }
        ).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Collection<LensTypeEntity>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(@NonNull Collection<LensTypeEntity> lensTypeEntities) {
                        swipeRefreshLayout.setRefreshing(false);

                        setLensList(lensTypeEntities);
                    }
                });
    }

    private String getLensCategory(LensObject lens) {
        if (lens.isFavourited()) {
            return "favourited";
        } else if (lens.code.contains("bitmoji")) {
            return "bitmoji";
        } else {
            return lens.mType;
        }
    }

    private void setLensList(Collection<LensTypeEntity> lensList) {
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

        lensTypes.clear();
        lensTypes.addAll(lensList);
        //Collections.sort(lensTypes);
        setAreLensesEmpty(lensTypes.isEmpty());

        if (adapter != null && !lensTypes.isEmpty()) {
            adapter.notifyDataSetChanged();
        }

        refreshStatistics();
    }

    private void setAreLensesEmpty(boolean isEmpty) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (emptyLensesView != null)
            emptyLensesView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void refreshStatistics() {
        if (isRemoving())
            return;

        txtActiveLenses.setText("" + lensTable.getRowCount("isActive = ?", new String[]{"1"}));
        txtTotalLenses.setText("" + lensTable.getRowCount());
    }

    private void handleUIEvent(LensUIEvent lensUIEvent) {
        switch (lensUIEvent) {
            case RELOAD_LENSES:
                generateMetaData();
                break;
            case RELOAD_STATISTICS:
                refreshStatistics();
                break;
            case UPDATE_LENS_SPAN:
                updateSpanSizeLookup();
                break;
            case ENABLE_ALL:
                int totalLensCount = getLensListSize();
                if (totalLensCount > 25) {
                    try {
                        DialogFactory.createConfirmation(
                                getActivity(),
                                "Large Lens List",
                                "You are about to enable " + totalLensCount + " lenses.\n" +
                                        "This can cause Snapchat to download and store large amounts of data in your MediaCache.\n\n" +
                                        "Are you sure you want to continue?",
                                new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        setAllLensActiveStates(true);
                                        themedDialog.dismiss();
                                    }
                                }
                        ).show();
                        return;
                    } catch (Throwable t) {
                        Timber.e(t);
                    }
                }
                break;
            case DISABLE_ALL:
                setAllLensActiveStates(false);
                break;
            case SHOW_LENS_NAMES:
                if (adapter.getItemCount() < 1000) {
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        if (adapter.getItem(i) instanceof LensObject)
                            adapter.notifyItemChanged(i);
                    }
                } else
                    adapter.notifyDataSetChanged();

                break;
            case MERGE:
                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(
                        Intent.createChooser(intent, "Choose source lens database to merge"),
                        MERGE_LENSES_REQUEST
                );

                break;
            default:
                throw new IllegalArgumentException("Unknown LensUIEvent: " + lensUIEvent);
        }
    }

    private int getLensListSize() {
        return (int) lensTable.getRowCount();
    }

    private void setAllLensActiveStates(boolean state) {
        lensTable.updateAll("isActive", state);
        generateMetaData();
    }

    private void notifyItemChanged(int index) {
        if (index < 0 || index >= adapter.getItemCount() - 1)
            return;

        adapter.notifyItemChanged(index);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleLensEventRequest(LensEventRequest eventRequest) {
        LensObject lens = eventRequest.getLens();

        switch (eventRequest.getLensEvent()) {
            case LOAD:
            case UNLOAD:
                lensTable.insert(lens);
                int index = getIndexFromName(lens.code);

                if (index == -1)
                    adapter.notifyDataSetChanged();
                else
                    adapter.notifyItemChanged(index);

                if (getPref(KILL_SC_ON_CHANGE))
                    PackUtils.killSCService(getActivity());

                refreshStatistics();
                break;
            case DELETE:
                if (lensTable.delete(lens)) {
                    int lensIndex = getIndexFromName(lens.code);
                    adapter.notifyItemRemoved(lensIndex);
                } else
                    adapter.notifyDataSetChanged();

                if (getPref(KILL_SC_ON_CHANGE))
                    PackUtils.killSCService(getActivity());

                refreshStatistics();
                break;
            case ACTION_MENU:
                boolean isFavourited = lens.isFavourited();

                DialogFactory.createOptions(
                        getActivity(),
                        "Lens Action Menu",
                        new OptionsButtonData("fav", isFavourited ? "Unfavourite" : "Favourite", new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                themedDialog.dismiss();

                                toggleLensFavourite(lens);
                            }
                        })
                ).show();
                break;
            default:
                Timber.d("Ignoring Unhandled Event");
        }
    }

    private int getIndexFromName(String code) {
        int index = -1;
        Timber.d("Adapter Count: " + adapter.getItemCount());

        for (MultiItemEntity packObj : adapter.getData()) {
            index++;

            if (!(packObj instanceof LensObject))
                continue;

            LensObject lens = (LensObject) packObj;

            if (lens.code.equals(code))
                return index;
        }

        return -1;
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    private void toggleLensFavourite(LensObject lens) {
        String sourceCategoryId = getLensCategory(lens);

        boolean isFavourited = lens.isFavourited();

        lens.favourited = !isFavourited;

        if (!lensTable.insert(lens)) {
            lens.favourited = isFavourited;
            SafeToastAdapter.showErrorToast(
                    getActivity(),
                    "Failed to mark lens as favourite"
            );
            return;
        }

        int lensIndex = getIndexFromName(lens.code);

        if (lensIndex == -1) {
            generateMetaData();
            adapter.notifyDataSetChanged();
            return;
        }

        String targetCategoryId = getLensCategory(lens);

        // ===========================================================================
        LensTypeEntity currentCategoryItem = null;
        int currentCategoryIndex = -1;
        // ===========================================================================
        LensTypeEntity sourceCategoryItem = null;
        int sourceCategoryIndex = -1;
        // ===========================================================================
        LensTypeEntity targetCategoryItem = null;
        int targetCategoryIndex = -1;
        boolean targetExpanded = false;
        // ===========================================================================
        int sourceLensIndex = -1;

        int adapterIndex = -1;

        List<ExpandableItemEntity> adapterDataClone = new ArrayList<>(adapter.getData());
        for (ExpandableItemEntity adapterItem : adapterDataClone) {
            adapterIndex++;

            if (adapterItem instanceof LensTypeEntity) {
                currentCategoryItem = (LensTypeEntity) adapterItem;
                currentCategoryIndex = adapterIndex;

                if (currentCategoryItem.getId().equals(sourceCategoryId)) {
                    sourceCategoryItem = currentCategoryItem;
                    sourceCategoryIndex = currentCategoryIndex;
                    Timber.d("Source Category Index: " + sourceCategoryIndex);
                } else if (currentCategoryItem.getId().equals(targetCategoryId)) {
                    targetCategoryItem = currentCategoryItem;
                    targetCategoryIndex = currentCategoryIndex;
                    targetExpanded = targetCategoryItem.isExpanded();
                    Timber.d("Target Category Index: " + targetCategoryIndex + " IsExpanded: " + targetExpanded);
                }
            } else if (adapterItem instanceof LensObject) {
                LensObject currentLensItem = (LensObject) adapterItem;
                if (currentLensItem.equals(lens))
                    sourceLensIndex = adapterIndex;
            }

            if (sourceLensIndex != -1 && sourceCategoryIndex != -1 && targetCategoryIndex != -1)
                break;
        }

        if (sourceLensIndex != -1 && sourceCategoryIndex != -1) {
            if (targetCategoryIndex == -1) {
                targetCategoryItem = new LensTypeEntity(
                        targetCategoryId,
                        LensTypeEntity.getHeaderFromId(targetCategoryId),
                        new ArrayList<>()
                );
                targetCategoryIndex = adapterIndex + 1;
                lensTypes.add(targetCategoryItem);
                adapter.notifyItemInserted(targetCategoryIndex);
            }

            sourceCategoryItem.removeSubItem(lens);
            targetCategoryItem.addSubItem(0, lens);

            adapter.remove(sourceLensIndex);

            if (targetExpanded) {
                adapter.addData(targetCategoryIndex + 1, lens);
            }

            List sourceSubItems = sourceCategoryItem.getSubItems();
            if (sourceSubItems == null || sourceSubItems.isEmpty()) {
                lensTypes.remove(sourceCategoryItem);
                adapter.notifyItemRemoved(sourceCategoryIndex);
            }

            Timber.d("Removing %s, Adding %s", sourceLensIndex, targetCategoryIndex + 1);
        }
    }

    public enum LensUIEvent {
        RELOAD_LENSES, RELOAD_STATISTICS, UPDATE_LENS_SPAN, ENABLE_ALL, DISABLE_ALL,
        SHOW_LENS_NAMES, MERGE
    }

    private static class LensTypeEntity extends ExpandableItemEntity<MultiItemEntity> {
        public static final int type = 0;
        private final String headerText;
        private final String id;
        private boolean shouldAnimate;

        LensTypeEntity(String id, String headerText, Collection<LensObject> lenses) {
            this.id = id;
            this.headerText = headerText;

            for (LensObject lens : lenses)
                addSubItem(lens);
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            holder.itemView.setOnClickListener(v -> defaultClickOperation(holder, adapter, v));

            if (!shouldAnimate)
                shouldAnimate = true;
            else {
                AnimationUtils.rotate(
                        ResourceUtils.getView(holder.itemView, "img_arrow"),
                        isExpanded()
                );
            }

            ResourceUtils.<TextView>getView(holder.itemView, "txt_listable")
                    .setText(headerText);
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getItemType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public static String getHeaderFromId(String id) {
            switch (id) {
                case "favourited":
                    return "FAVOURITED";
                case "bitmoji":
                    return "AVATARS";
                default:
                    return id;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.soleUnregister(this);
    }


    @Override
    public String getName() {
        return "Lens Manager";
    }


    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }
}
