package com.ljmu.andre.snaptools.Framework.MetaData;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.BuildConfig;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ErrorTextItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.HtmlTextItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleResources;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ServerPackMetaData extends PackMetaData {
    private boolean hasUpdate;
    private boolean isInstalled;

    @SerializedName("min_apk_code")
    private int minApkCode;
    @SerializedName("min_apk_name")
    private String minApkName;
    @SerializedName("description")
    private String description;
    @SerializedName("is_purchased")
    private Boolean isPurchased;

    public static ServerPackMetaData getTutorialPack(String scVersion) {
        return (ServerPackMetaData) new ServerPackMetaData()
                .setPurchased(false)
                .setDescription(htmlHighlight("Tutorial Item"))
                .setHasUpdate(false)
                .setInstalled(true)
                .setScVersion(scVersion)
                .setPackVersion("1.0.0.0")
                .setDevelopment(false)
                .setType("Tutorial")
                .setName("TutorialPack v" + scVersion)
                .setFlavour("prod")
                .setTutorial(true)
                .completedBinding();
    }

    public int getMinApkCode() {
        return minApkCode;
    }

    public ServerPackMetaData setMinApkCode(int minApkCode) {
        this.minApkCode = minApkCode;
        return this;
    }

    public String getMinApkName() {
        return minApkName;
    }

    public ServerPackMetaData setMinApkName(String minApkName) {
        this.minApkName = minApkName;
        return this;
    }

    @Override
    public void updateHeaderStateHolder(TextView stateHolder) {
        if (!isInstalled) {
		    /*stateHolder.setText("Inactive");
		    stateHolder.setBackgroundResource(R.color.error);
            int color = ContextHelper.getModuleContext().getResources().getColor(R.color.textPrimary);
            stateHolder.setTextColor(color);*/
            stateHolder.setVisibility(View.GONE);
        } else if (hasUpdate) {
            stateHolder.setText("Update");
            stateHolder.setBackgroundResource(R.color.success);
            int color = getModuleResources(stateHolder.getContext())
                    .getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
            stateHolder.setVisibility(View.VISIBLE);
        } else {
            stateHolder.setText("Latest");
            stateHolder.setBackgroundResource(R.color.primary);
            int color = getModuleResources(stateHolder.getContext())
                    .getColor(R.color.textSecondary);
            stateHolder.setTextColor(color);
            stateHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateMessageStateHolder(TextView stateHolder, String child) {
        stateHolder.setVisibility(View.GONE);
    }

    @Override
    protected String getFailReason() {
        return null;
    }

    @Override
    public PackMetaData completedBinding() {
        addSubItem(new ServerPackToolbarItem(this));

        if (description != null) {
            addSubItem(new HtmlTextItemEntity(description));
        }

        Boolean isPurchased = isPurchased();
        if (isPurchased != null) {
            addSubItem(new TextItemEntity(
                    isPurchased ? "Purchased"
                            : "Not Purchased"
            ).setColourRes(
                    isPurchased ? R.color.successLight : R.color.errorLight
            ));
        }

        super.completedBinding();

        if (minApkCode > BuildConfig.VERSION_CODE)
            addSubItem(new ErrorTextItemEntity("This update requires a minimum apk version of: " + minApkName));

        return this;
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
        super.convert(holder, adapter);

        ImageView stateHolder = (ImageView) holder.itemView.findViewById(R.id.img_state);

        if (hasUpdate())
            stateHolder.setImageResource(R.drawable.update_icon);
        else if (isInstalled())
            stateHolder.setImageResource(R.drawable.ok_icon_primary_fill);
        else
            stateHolder.setImageDrawable(null);
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public ServerPackMetaData setInstalled(boolean installed) {
        isInstalled = installed;
        return this;
    }

    @Nullable
    @RequiresFramework(73)
    public Boolean isPurchased() {
        if (!isPremium())
            return null;

        return isPurchased;
    }

    @RequiresFramework(73)
    public String getDescription() {
        return description;
    }

    @RequiresFramework(73)
    public ServerPackMetaData setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public ServerPackMetaData setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
        return this;
    }

    @RequiresFramework(73)
    public ServerPackMetaData setPurchased(@Nullable Boolean isPurchased) {
        this.isPurchased = isPurchased;
        return this;
    }

    public static class ServerPackToolbarItem extends ExpandableItemEntity {
        public static final int layoutRes = R.layout.pack_toolbar_server;
        public static final int type = 2;
        public final int level;
        private ServerPackMetaData linkedMeta;

        public ServerPackToolbarItem(ServerPackMetaData linkedMeta) {
            this(linkedMeta, 1);
        }

        public ServerPackToolbarItem(ServerPackMetaData linkedMeta, int level) {
            this.linkedMeta = linkedMeta;
            this.level = level;
        }

        @Override
        public int getItemType() {
            return type;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            ImageButton download = getView(holder.itemView, R.id.btn_download);
            ImageButton rollback = getView(holder.itemView, R.id.btn_history);
            ImageButton changelog = getView(holder.itemView, R.id.btn_changelog);

            EventRequest eventType = linkedMeta.isTutorial() ? EventRequest.DOWNLOAD_TUTORIAL : EventRequest.DOWNLOAD;
            Timber.d("EventType: " + eventType + ": " + linkedMeta.isTutorial() + ": " + linkedMeta.toString());

            if (BuildConfig.VERSION_CODE >= linkedMeta.getMinApkCode()) {
                download.setImageTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.textPrimary));

                download.setOnClickListener(
                        v -> EventBus.getInstance().post(
                                new PackEventRequest(
                                        eventType,
                                        linkedMeta.getName()
                                )
                        )
                );
            } else {
                download.setImageTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.error));
            }

            if (!linkedMeta.isTutorial()) {
                rollback.setOnClickListener(
                        v -> EventBus.getInstance().post(
                                new PackEventRequest(
                                        EventRequest.SHOW_ROLLBACK,
                                        linkedMeta.getName()
                                )
                        )
                );

                changelog.setOnClickListener(
                        v -> EventBus.getInstance().post(
                                new PackEventRequest(
                                        EventRequest.SHOW_CHANGELOG,
                                        linkedMeta.getName()
                                )
                        )
                );
            }
        }

        @Override
        public int getLevel() {
            return level;
        }
    }
}
