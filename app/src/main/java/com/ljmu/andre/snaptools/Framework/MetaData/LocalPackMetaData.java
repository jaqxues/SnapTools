package com.ljmu.andre.snaptools.Framework.MetaData;

import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.LoadPackSettingsEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;

import static android.view.View.GONE;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */
public class LocalPackMetaData extends PackMetaData {
    @Override
    public void updateHeaderStateHolder(TextView stateHolder) {
        if (isActive()) {
            stateHolder.setText("Active");
            stateHolder.setBackgroundResource(R.color.success);
            int color = getModuleResources(stateHolder.getContext()).getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
        } else {
            if (PreferenceHelpers.collectionContains(SELECTED_PACKS, getName()))
                stateHolder.setText("Failed");
            else
                stateHolder.setText("Inactive");

            stateHolder.setBackgroundResource(R.color.error);
            int color = getModuleResources(stateHolder.getContext()).getColor(R.color.textPrimary);
            stateHolder.setTextColor(color);
        }
    }

    @Override
    public void updateMessageStateHolder(TextView stateHolder, String child) {
        stateHolder.setVisibility(GONE);
    }

    @Override
    public boolean isActive() {
        return isTutorial() || super.isActive();
    }

    @Override
    public LocalPackMetaData completedBinding() {
        addSubItem(new LocalPackToolbarItem(this));
        super.completedBinding();
        return this;
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
        super.convert(holder, adapter);

        ImageView stateHolder = (ImageView) holder.itemView.findViewById(R.id.img_state);

        if (isActive())
            stateHolder.setImageResource(R.drawable.ok_icon_primary_fill);
        else if (PreferenceHelpers.collectionContains(SELECTED_PACKS, getName()))
            stateHolder.setImageResource(R.drawable.error_coloured);
        else
            stateHolder.setImageDrawable(null);
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public static LocalPackMetaData getTutorialPack(String scVersion) {
        return (LocalPackMetaData) new LocalPackMetaData()
                .setScVersion(scVersion)
                .setPackVersion("1.0.0.0")
                .setLatest(true)
                .setDevelopment(false)
                .setType("Tutorial")
                .setName("TutorialPack v" + scVersion)
                .setTutorial(true)
                .setFlavour("prod")
                .completedBinding();
    }

    public static LocalPackMetaData from(PackMetaData metaData) {
        LocalPackMetaData localMetaData = new LocalPackMetaData();
        localMetaData.setName(metaData.getName());
        localMetaData.setDevelopment(metaData.isDeveloper());
        localMetaData.setPackVersion(metaData.getPackVersion());
        localMetaData.setScVersion(metaData.getScVersion());
        localMetaData.setType(metaData.getType());
        localMetaData.setFlavour(metaData.getFlavour());

        return localMetaData;
    }

    public static class LocalPackToolbarItem extends ExpandableItemEntity {
        public static final int layoutRes = R.layout.pack_toolbar_local;
        public static final int type = 2;
        public final int level;
        private PackMetaData linkedMeta;

        public LocalPackToolbarItem(PackMetaData linkedMeta) {
            this(linkedMeta, 1);
        }

        public LocalPackToolbarItem(PackMetaData linkedMeta, int level) {
            this.linkedMeta = linkedMeta;
            this.level = level;
        }

        @Override
        public int getItemType() {
            return type;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            ImageButton settings = (ImageButton) holder.itemView.findViewById(R.id.btn_settings);
            ImageButton delete = (ImageButton) holder.itemView.findViewById(R.id.btn_delete);
            SwitchCompat toggle = (SwitchCompat) holder.itemView.findViewById(R.id.switch_toggle);

            settings.setVisibility(shouldShowSettingsIcon() ? View.VISIBLE : View.INVISIBLE);

            if (linkedMeta.isTutorial()) {
                toggle.setChecked(true);
                return;
            }

            settings.setOnClickListener(v -> EventBus.getInstance().post(
                    new LoadPackSettingsEvent(
                            linkedMeta.getName()
                    )
            ));

            delete.setOnClickListener(
                    v -> EventBus.getInstance().post(
                            new PackEventRequest(
                                    EventRequest.DELETE,
                                    linkedMeta.getName()
                            )
                    ));

            toggle.setOnCheckedChangeListener(null);
            toggle.setChecked(
                    PreferenceHelpers.collectionContains(
                            SELECTED_PACKS,
                            linkedMeta.getName()
                    ));

            toggle.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> EventBus.getInstance().post(
                            new PackEventRequest(
                                    isChecked ? EventRequest.LOAD : EventRequest.UNLOAD,
                                    linkedMeta.getName()
                            )
                    ));
        }

        private boolean shouldShowSettingsIcon() {
            if (linkedMeta.isTutorial())
                return true;

            ModulePack pack = FrameworkManager.getModulePack(linkedMeta.getName());

            return pack != null;
        }

        @Override
        public int getLevel() {
            return level;
        }
    }
}
