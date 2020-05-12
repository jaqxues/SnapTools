package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ModuleEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.Tutorials.GeneralSettingsTutorial;
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.DISABLED_MODULES;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.setContainerPadding;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.addToCollection;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.removeFromCollection;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class GeneralSettingsFragment extends FragmentHelper {
    private final List<ModuleDisplayHolder> displayHolders = new ArrayList<>();
    private final List<Pair<String, String>> moduleNameDescPairs = new ArrayList<>();
    private String packName;
    private RecyclerView recyclerView;
    private ExpandableItemAdapter<ExpandableItemEntity> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.soleRegister(this);

        LinearLayout layoutContainer = new LinearLayout(getContext());
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        setContainerPadding(layoutContainer);

        initModuleStatusList(layoutContainer);

        if (Constants.getApkVersionCode() >= 69)
            tutorialDetails = GeneralSettingsTutorial.getTutorials(adapter, recyclerView);

        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        assignModuleDetails();
    }

    @Override
    public void onPause() {
        super.onPause();
        displayHolders.clear();
    }

    private void assignModuleDetails() {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean hasAnimated = false;

            @Override
            public void onGlobalLayout() {

                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.

                if (!hasAnimated && Constants.getApkVersionCode() >= 66) {
                    hasAnimated = true;
                    Timber.d("Animating view");
                    AnimationUtils.sequentGroup(recyclerView);
                }

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        displayHolders.clear();

        for (Pair<String, String> moduleNameDescPair : moduleNameDescPairs)
            displayHolders.add(new ModuleDisplayHolder(packName, moduleNameDescPair.first, moduleNameDescPair.second));

        adapter.notifyDataSetChanged();
    }

    private void initModuleStatusList(LinearLayout layoutContainer) {
        layoutContainer.addView(
                ViewFactory.getHeaderLabel(
                        getActivity(),
                        "Module Management"
                )
        );

        recyclerView = ViewFactory.getRecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutContainer.addView(recyclerView);

        Timber.d("DisplayHolders: " + displayHolders.size());

        adapter = new ExpandableItemAdapter(displayHolders);
        adapter.bindToRecyclerView(recyclerView);
        adapter.addType(ModuleDisplayHolder.type, getLayout(getContext(), "item_module_holder_header"));
        adapter.addType(TextItemEntity.type, getLayout(getContext(), "item_listable_msg"));
        adapter.setEmptyView(getLayout(getContext(), "layout_empty_packs"));

        TextView longPressReminder = new TextView(getContext());
        longPressReminder.setText("Long press a module for a description");
        longPressReminder.setGravity(Gravity.CENTER);
        longPressReminder.setTextColor(Color.parseColor("#666666"));
        layoutContainer.addView(longPressReminder);
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public void addDisplayHolder(Pair<String, String> moduleDescPair) {
        moduleNameDescPairs.add(moduleDescPair);
    }

    @Subscribe
    public void handleModuleEventRequest(ModuleEventRequest eventRequest) {
        switch (eventRequest.getEventRequest()) {
            case UNLOAD:
                addToCollection(
                        DISABLED_MODULES,
                        eventRequest.getModuleName(),
                        getActivity()
                );

                int unloadIndex = getIndexFromName(eventRequest.getModuleName());
                adapter.notifyItemChanged(unloadIndex);
                break;
            case LOAD:
                removeFromCollection(
                        DISABLED_MODULES,
                        eventRequest.getModuleName(),
                        getActivity()
                );

                int loadIndex = getIndexFromName(eventRequest.getModuleName());
                adapter.notifyItemChanged(loadIndex);
                break;
            default:
                Timber.d("Ignoring Unhandled Request");
        }
    }

    public int getIndexFromName(String packName) {
        int index = -1;
        Timber.d("Adapter Count: " + adapter.getItemCount());

        for (Object displayObj : displayHolders) {
            index++;

            if (!(displayObj instanceof ModuleDisplayHolder))
                continue;

            ModuleDisplayHolder displayHolder = (ModuleDisplayHolder) displayObj;

            if (displayHolder.getName().equals(packName))
                return index;
        }

        return -1;
    }

    public static class ModuleDisplayHolder extends ExpandableItemEntity<MultiItemEntity> {
        public static final int type = 0;
        private static ModuleDisplayHolder currentlyExpanded;
        private String packName;
        private String moduleName;
        private boolean shouldAnimate;

        public ModuleDisplayHolder(String packName, String moduleName, String description) {
            this.packName = packName;
            this.moduleName = moduleName;
            addSubItem(new TextItemEntity(description));
        }

        public String getName() {
            return moduleName;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            Context context = holder.itemView.getContext();

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventRequest eventRequest = isActive() ? EventRequest.UNLOAD : EventRequest.LOAD;

                    ModuleEventRequest moduleEventRequest;

                    if (Constants.getApkVersionCode() <= 65)
                        moduleEventRequest = new ModuleEventRequest(eventRequest, moduleName);
                    else
                        moduleEventRequest = new ModuleEventRequest(eventRequest, packName, moduleName);

                    EventBus.getInstance().post(moduleEventRequest);
                }
            });

            holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (adapter.toggleItem(holder, ModuleDisplayHolder.this)) {
                        int index = adapter.getData().indexOf(currentlyExpanded);
                        if (index != -1 && currentlyExpanded != null)
                            adapter.collapse(adapter.getData().indexOf(currentlyExpanded));

                        currentlyExpanded = ModuleDisplayHolder.this;
                    } else if (currentlyExpanded == ModuleDisplayHolder.this)
                        currentlyExpanded = null;

                    return true;
                }
            });

            ImageView imgArrow = ResourceUtils.getView(holder.itemView, "img_arrow");
            if (imgArrow.getVisibility() != View.GONE)
                imgArrow.setVisibility(View.GONE);

            TextView txtHeader = ResourceUtils.getView(holder.itemView, "txt_listable");
            txtHeader.setText(moduleName);

            int drawableResId = isActive() ?
                    getDrawable(context, "ok_icon")
                    : getDrawable(context, "cancel_fill_error");

            ImageView imgState = (ResourceUtils.getView(holder.itemView, "img_state"));
            imgState.setImageResource(drawableResId);

            if (!shouldAnimate) {
                shouldAnimate = true;

                return;
            }

            AnimationUtils.scaleUp(imgState);
        }

        @Override
        public int getItemId() {
            return getIdFromString(moduleName);
        }

        boolean isActive() {
            return !collectionContains(DISABLED_MODULES, moduleName);
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getItemType() {
            return type;
        }
    }

    @Override
    public boolean hasTutorial() {
        return Constants.getApkVersionCode() >= 69;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.soleUnregister(this);
    }


    @Override
    public String getName() {
        return "General";
    }


    @Override
    public Integer getMenuId() {
        return (getName() + packName).hashCode();
    }


}
