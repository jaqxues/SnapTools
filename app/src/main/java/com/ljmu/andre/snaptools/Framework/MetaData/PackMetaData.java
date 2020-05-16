package com.ljmu.andre.snaptools.Framework.MetaData;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.g00fy2.versioncompare.Version;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.Framework.FrameworkManager;
import com.ljmu.andre.snaptools.Framework.ModulePack;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.StatefulEListAdapter.StatefulListable;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.StringUtils.getFlavourText;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public abstract class PackMetaData extends ExpandableItemEntity<MultiItemEntity> implements StatefulListable<String>, Comparable<PackMetaData> {
    public static final int layoutRes = R.layout.item_listable_head_stateful;
    public static final int type = 0;
    protected final PackEventRequest.EventHandler eventDispatcher;

    public PackMetaData(PackEventRequest.EventHandler dispatcher) {
        eventDispatcher = dispatcher;
    }

    /**
     * ===========================================================================
     * A list of comparators that are used to sequentially determine the ordering
     * that all PackMetaData should adhere to.
     * See {@link this#compareTo(PackMetaData)} for implementation details
     * ===========================================================================
     */
    @SuppressWarnings("Convert2Lambda")
    private static final List<? extends Comparator<PackMetaData>> comparators = Arrays.asList(
            // Check Active State First ==================================================
            new Comparator<PackMetaData>() {
                @Override
                public int compare(PackMetaData o1, PackMetaData o2) {
                    if (!o1.isActive() && o2.isActive()) {
                        return 1;
                    } else if (o1.isActive() && !o2.isActive()) {
                        return -1;
                    }

                    return 0;
                }
            },
            // ===========================================================================

            // Check Snapchat Version ====================================================
            new Comparator<PackMetaData>() {
                @Override
                public int compare(PackMetaData o1, PackMetaData o2) {
                    Version o1Version = new Version(o1.getScVersion());
                    Version o2Version = new Version(o2.getScVersion());

                    if (o1Version.isLowerThan(o2Version.getOriginalString())) {
                        return 1;
                    } else if (o1Version.isHigherThan(o2Version.getOriginalString())) {
                        return -1;
                    }

                    return 0;
                }
            },

            // Check Pack Version ========================================================
            new Comparator<PackMetaData>() {
                @Override
                public int compare(PackMetaData o1, PackMetaData o2) {
                    Version o1Version = new Version(o1.getPackVersion());
                    Version o2Version = new Version(o2.getPackVersion());

                    if (o1Version.isLowerThan(o2Version.getOriginalString())) {
                        return 1;
                    } else if (o1Version.isHigherThan(o2Version.getOriginalString())) {
                        return -1;
                    }

                    return 0;
                }
            }
    );

    private boolean isTutorial;
    private boolean shouldAnimate;
    private Boolean isPremium;

    @SerializedName("name")
    private String name;

    @SerializedName("pack_type")
    private String pack_type;

    @SerializedName("sc_version")
    private String sc_version;

    @SerializedName("mod_version")
    private String mod_version;

    @SerializedName("development")
    private boolean development;

    @SerializedName("latest")
    private boolean latest;

    @SerializedName("flavour")
    private String flavour;

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getType(), getScVersion(), getPackVersion(), development, getFlavour());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackMetaData)) return false;

        PackMetaData metaData = (PackMetaData) o;
        return development == metaData.development &&
                Objects.equal(getName(), metaData.getName()) &&
                Objects.equal(getType(), metaData.getType()) &&
                Objects.equal(getScVersion(), metaData.getScVersion()) &&
                Objects.equal(getFlavour(), metaData.getFlavour());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("pack_type", pack_type)
                .add("sc_version", sc_version)
                .add("mod_version", mod_version)
                .add("development", development)
                .add("flavour", flavour)
                .toString();
    }

    public String getName() {
        return name;
    }

    public PackMetaData setName(String name) {
        Assert.notNull("Null Pack MetaData Name!", name);
        this.name = name;
        return this;
    }

    public String getType() {
        return pack_type;
    }

    public PackMetaData setType(String type) {
        Assert.notNull("Null Pack MetaData Type!", type);
        this.pack_type = type;
        return this;
    }

    public String getScVersion() {
        return sc_version;
    }

    public PackMetaData setScVersion(String scVersion) {
        Assert.notNull("Null Pack MetaData SCVersion!", scVersion);
        this.sc_version = scVersion;
        return this;
    }

    public String getPackVersion() {
        return mod_version;
    }

    public PackMetaData setPackVersion(String packVersion) {
        Assert.notNull("Null Pack MetaData PackVersion!", packVersion);
        this.mod_version = packVersion;
        return this;
    }

    public String getFlavour() {
        return flavour;
    }

    public PackMetaData setFlavour(String flavour) {
        this.flavour = flavour;
        return this;
    }

    public boolean isTutorial() {
        return isTutorial;
    }

    public PackMetaData setTutorial(boolean tutorial) {
        isTutorial = tutorial;
        return this;
    }

    @RequiresFramework(73)
    public boolean isPremium() {
        if (isPremium == null) {
            isPremium = pack_type != null
                    && pack_type.equals("Premium");
        }

        return isPremium;
    }

    @Override
    public int getItemType() {
        return type;
    }

    /**
     * ===========================================================================
     * An override to iterate through the {@link this#compareTo(PackMetaData)}
     * in an attempt to order two PackMetaData objects.
     * <p>
     * This function will sequentially iterate through the comparators until it
     * finds the first ordering difference between the two objects.
     * ===========================================================================
     */
    @Override
    public int compareTo(PackMetaData o) {
        for (Comparator<PackMetaData> comparator : comparators) {
            int comparison = comparator.compare(this, o);

            if (comparison != 0)
                return comparison;
        }

        return 0;
    }

    public PackMetaData setDevelopment(Boolean development) {
        Assert.notNull("Null Pack MetaData Development!",
                development);
        this.development = development;
        return this;
    }

    public boolean isLatest() {
        return latest;
    }

    public PackMetaData setLatest(boolean latest) {
        this.latest = latest;
        return this;
    }

    public boolean isActive() {
        ModulePack pack = FrameworkManager.getModulePack(getName());

        return pack != null && !pack.getPackLoadState().hasFailed();
    }

    @Override
    public List<String> getChildren() {
        return Arrays.asList(
                "Pack Type: " + getType(),
                "Snapchat Version: " + getScVersion(),
                "Pack Version: " + getPackVersion(),
                "Flavour: " + getFlavourText(getFlavour())
        );
    }

    @Override
    public void updateHeaderText(View container, TextView messageHolder) {
        messageHolder.setText(getDisplayName());
    }

    public String getDisplayName() {
        return (!flavour.equals("prod")
                ? getFlavourText(flavour) : "")
                + pack_type + "Pack v"
                + sc_version;
    }

    @Override
    public void updateMessageText(View container, TextView messageHolder, String child) {
        messageHolder.setText(child);
    }

    public Boolean isDeveloper() {
        return development;
    }

    protected String getFailReason() {
        return FrameworkManager.getFailReason(getName());
    }

    /**
     * ===========================================================================
     * Mark the MetaData as completed so that it can generate the UI elements
     * with their appropriate values.
     * <p>
     * This function allows us to create much more efficient code as it will
     * only update the UI once with all item entities, instead of having to scan
     * through existing elements to determine order.
     * ===========================================================================
     */
    public PackMetaData completedBinding() {
        this.addSubItem(new PackErrorEntity(this));
        this.addSubItem(new TextItemEntity("Pack Type: " + getType()));
        this.addSubItem(new TextItemEntity("Snapchat Version: " + getScVersion()));
        this.addSubItem(new TextItemEntity("Pack Version: " + getPackVersion()));
        this.addSubItem(new TextItemEntity("Flavour: " + getFlavourText(getFlavour())));

        return this;
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
        holder.itemView.setOnClickListener(v -> defaultClickOperation(holder, adapter, v));

        TextView txtHeader = (TextView) holder.itemView.findViewById(R.id.txt_listable);
        txtHeader.setText(getDisplayName());

        if (!shouldAnimate)
            shouldAnimate = true;
        else {
            ImageView imgArrow = (ImageView) holder.itemView.findViewById(R.id.img_arrow);
            AnimationUtils.rotate(imgArrow, isExpanded());
        }
    }

    @Override
    public int getItemId() {
        return getIdFromString(getDisplayName());
    }

    /**
     * ===========================================================================
     * A utility method to generalise filename generation instead of having
     * duplicate code throughout the project.
     * ===========================================================================
     *
     * @return A string that can be used to determine the filename of a ModulePack
     * based on some MetaData
     */
    public static String getFileNameFromTemplate(String type, String scVersion, String flavour, @Nullable String packVersion) {
        return "STModulePack"
                + (!"prod".equals(flavour) ? getFlavourText(flavour) : "")
                + (packVersion == null ? "" : "_" + packVersion)
                + "_" + type
                + "_" + scVersion;
    }

    public static class PackErrorEntity extends TextItemEntity {
        private PackMetaData linkedMeta;

        public PackErrorEntity(PackMetaData linkedMeta) {
            super(null);
            this.linkedMeta = linkedMeta;
        }

        @Override
        public String getText() {
            return linkedMeta.getFailReason();
        }

        @Override
        protected int getTextColorRes() {
            return R.color.error;
        }
    }
}
