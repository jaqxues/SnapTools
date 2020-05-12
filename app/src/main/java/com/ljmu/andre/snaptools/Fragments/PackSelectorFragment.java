package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent.DownloadState;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackLoadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackUnloadEvent;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.MetaData.FailedPackMetaData.FailedPackToolbar;
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Framework.Utils.PackLoadState;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackSelectorFragment extends FragmentHelper {
    public static final String TAG = "Pack Selector";
    private final List<LocalPackMetaData> packs = new ArrayList<>();

    Unbinder unbinder;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_pack_selector)
    RecyclerView list_packs;
    private ExpandableItemAdapter<ExpandableItemEntity> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_pack_selector, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        Timber.d("Create");

        EventBus.soleRegister(this);

        list_packs.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpandableItemAdapter(packs);
        adapter.bindToRecyclerView(list_packs);
        adapter.addType(PackMetaData.type, PackMetaData.layoutRes);
        adapter.addType(TextItemEntity.type, TextItemEntity.layoutRes);
        adapter.addType(LocalPackMetaData.LocalPackToolbarItem.type, LocalPackMetaData.LocalPackToolbarItem.layoutRes);
        adapter.addType(FailedPackToolbar.type, FailedPackToolbar.layoutRes);
        adapter.setEmptyView(R.layout.layout_empty_packs);

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    if (runningTutorial) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }

                    generateMetaData();
                }
        );

        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (runningTutorial)
            generateTutorialData();
        else
            generateMetaData();
    }

    @Override
    public void onPause() {
        super.onPause();
        packs.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void generateMetaData() {
        if (runningTutorial) {
            generateTutorialData();

            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(false);
            return;
        }

        PackUtils.getAllMetaData()
                .subscribe(new DisposableObserver<Map<String, LocalPackMetaData>>() {
                    @Override
                    public void onNext(@NonNull Map<String, LocalPackMetaData> metaDataMap) {
                        if (runningTutorial)
                            return;

                        setPacks(metaDataMap.values());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (swipeRefreshLayout != null)
                            swipeRefreshLayout.setRefreshing(false);

                        if (runningTutorial)
                            return;

                        setPacks(Collections.emptyList());
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {
                        if (swipeRefreshLayout != null)
                            swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public void generateTutorialData() {
        List<String> tutorialVersions = new ImmutableList.Builder<String>()
                .add("10.0.0.0")
                .add("10.1.0.1")
                .add("10.12.1.0")
                .add("10.16.0.0")
                .build();

        List<LocalPackMetaData> tutorialDataList = new ArrayList<>(tutorialVersions.size());

        for (String tutorialVersion : tutorialVersions)
            tutorialDataList.add(LocalPackMetaData.getTutorialPack(tutorialVersion));

        setPacks(tutorialDataList);
    }

    public void setPacks(Collection<LocalPackMetaData> newPacks) {
        if (list_packs != null) {
            list_packs.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (list_packs == null)
                        return;

                    //At this point the layout is complete and the
                    //dimensions of recyclerView and any child views are known.

                    AnimationUtils.sequentGroup(list_packs);

                    list_packs.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        packs.clear();
        packs.addAll(newPacks);
        Collections.sort(packs);

        if (adapter != null) {
            if (packs.isEmpty())
                adapter.setEmptyView(R.layout.layout_empty_packs);
            else
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Integer getMenuId() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.soleUnregister(this);
    }

    @Override
    public void progressTutorial() {
    }

    @Subscribe
    public void handlePackDownloadEvent(PackDownloadEvent downloadEvent) {
        Timber.d("Pack Download Event: " + downloadEvent.toString());

        if (downloadEvent.getState() == DownloadState.SUCCESS) {
            if (downloadEvent.getMetaData() == null)
                return;

            generateMetaData();
        }
    }

    @Nullable
    public LocalPackMetaData getMetaDataFromName(String packName) {
        Pair<LocalPackMetaData, Integer> result = getMetaDataAndIndexFromName(packName);

        return result == null ? null : result.first;
    }

    @Nullable
    public Pair<LocalPackMetaData, Integer> getMetaDataAndIndexFromName(String packName) {
        int index = -1;
        for (Object packObj : packs) {
            index++;
            if (!(packObj instanceof LocalPackMetaData))
                continue;

            LocalPackMetaData metaData = (LocalPackMetaData) packObj;

            if (metaData.getName().equals(packName))
                return new Pair<>(metaData, index);
        }

        return null;
    }

    public RecyclerView getRecyclerView() {
        return list_packs;
    }

    public ExpandableItemAdapter<ExpandableItemEntity> getAdapter() {
        return adapter;
    }

    @Subscribe
    public void handlePackEventRequest(PackEventRequest eventRequest) {
        EventRequest request = eventRequest.getRequest();
        Timber.d("New Event Request: " + request.toString());

        switch (request) {
            case UNLOAD:
                PreferenceHelpers.removeFromCollection(
                        SELECTED_PACKS,
                        eventRequest.getPackName(),
                        getActivity()
                );

                // If there was no pack to remove, manually update ===========================
                if (!FrameworkManager.unloadModPack(eventRequest.getPackName()))
                    updatePackState(eventRequest.getPackName());
                break;
            case LOAD:
                FrameworkManager.disableAllPacks();

                PreferenceHelpers.addToCollection(
                        SELECTED_PACKS,
                        eventRequest.getPackName(),
                        getActivity()
                );

                Observable.fromCallable(
                        () -> FrameworkManager.loadModPack(
                                getActivity(),
                                eventRequest.getPackName(),
                                new PackLoadState(eventRequest.getPackName())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<PackLoadEvent>() {
                            @Override
                            public void onNext(@NonNull PackLoadEvent loadEvent) {
                                EventBus.getInstance().post(loadEvent);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Timber.w(e);
                                FrameworkManager.addFailReason(eventRequest.getPackName(), e.getMessage());
                                updatePackState(eventRequest.getPackName());
                            }

                            @Override
                            public void onComplete() {
                            }
                        });

                break;
            case DELETE:
                DialogFactory.createConfirmation(
                        getActivity(),
                        "Confirm Action",
                        "Are you sure you wish to delete this pack?",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                FrameworkManager.deleteModPack(eventRequest.getPackName(), getActivity());

                                if (collectionContains(SELECTED_PACKS, eventRequest.getPackName())) {
                                    String message = "Failed to disable deleted pack: " + eventRequest.getPackName();
                                    Timber.e(message);
                                    SafeToast.show(getActivity(), message, Toast.LENGTH_LONG);
                                }

                                themedDialog.dismiss();
                            }
                        }
                ).show();
                break;
            default:
                Timber.d("Ignoring Unhandled Request");
        }
    }

    public void updatePackState(String packName) {
        int packIndex = getIndexFromName(packName);
        if (packIndex != -1) {
            adapter.notifyItemChanged(packIndex);
            ExpandableItemEntity entity = adapter.getItem(packIndex);

            if (entity == null || !entity.isExpanded())
                return;

            while (++packIndex < adapter.getItemCount()) {
                entity = adapter.getItem(packIndex);

                Timber.d("Entity: " + entity);
                if (!(entity instanceof PackMetaData)) {
                    adapter.notifyItemChanged(packIndex);
                } else
                    break;
            }
        }
    }

    public int getIndexFromName(String packName) {
        int index = -1;
        Timber.d("Adapter Count: " + adapter.getItemCount());

        for (Object packObj : packs) {
            index++;

            if (!(packObj instanceof LocalPackMetaData))
                continue;

            LocalPackMetaData metaData = (LocalPackMetaData) packObj;

            if (metaData.getName().equals(packName))
                return index;
        }

        return -1;
    }

    @Subscribe
    public void handlePackLoadEvent(PackLoadEvent loadEvent) {
        updatePackState(loadEvent.getModulePack().getPackName());
    }

    @Subscribe
    public void handlePackUnloadEvent(PackUnloadEvent unloadEvent) {
        updatePackState(unloadEvent.getPackMetaData().getName());
    }

    @Subscribe
    public void handlePackDeleteEvent(PackDeleteEvent deleteEvent) {
        int index = getIndexFromName(deleteEvent.getPackName());
        adapter.collapse(index);
        adapter.remove(index);

        FrameworkManager.deleteModPack(deleteEvent.getPackName(), null);
    }
}
