package com.ljmu.andre.snaptools.Databases.Tables;

import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.g00fy2.versioncompare.Version;
import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ReqItemPurchaseEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ReqItemPurchaseEvent.PaymentType;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static com.ljmu.andre.snaptools.Utils.StringUtils.uppercaseFirst;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName(value = "ShopItems", VERSION = 2)
public class ShopItem extends ExpandableItemEntity<MultiItemEntity> implements CBIObject, Comparable<ShopItem> {
    public static final int LAYOUT_RES = R.layout.item_shop_header;
    public static final int TYPE = 0;
    private static final int LEVEL = 0;

    @TableField(value = "identifier")
    @PrimaryKey
    @SerializedName("identifier")
    public String identifier;

    @TableField(value = "type")
    @SerializedName("type")
    public String type;

    @TableField(value = "price")
    @SerializedName("price")
    public double price;

    @TableField(value = "description")
    @SerializedName("description")
    public String description;

    @TableField(value = "rocketr_link")
    @SerializedName("rocketr_link")
    public String rocketrLink;

    @TableField(value = "purchased")
    @SerializedName("purchased")
    public boolean purchased;

    private boolean isTutorial;
    private Boolean isPack;

    public static ShopItem generateTutorialItem(String identifier) {
        ShopItem tutorialItem = new ShopItem();
        tutorialItem.isTutorial = true;
        tutorialItem.identifier = identifier;
        tutorialItem.type = "Tutorial";
        return tutorialItem;
    }

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        Timber.d("Table Upgrade");

        List<SQLCommand> sqlCommandList = new ArrayList<>();

        if (oldVersion < 2 || !table.columnExists("rocketr_link")) {
            sqlCommandList.add(
                    new SQLCommand(
                            "ALTER TABLE ShopItems ADD COLUMN rocketr_link TEXT"
                    )
            );
        }

        linkedDBCore.runCommands(sqlCommandList);
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
        if (getSubItems() == null || getSubItems().isEmpty()) {
            if (description == null || description.isEmpty())
                description = "No description provided";

            if (!purchased)
                addSubItem(new ShopItemToolbar(this));

            addSubItem(new TextItemEntity(description));
        }

        ImageView imgArrow = (ImageView) holder.itemView.findViewById(R.id.img_arrow);
        imgArrow.setRotation(isExpanded() ? 180f : 0);

        Timber.d("Working on view: " + identifier);
        holder.itemView.setOnClickListener(v -> {
            defaultClickOperation(holder, adapter, v);
            AnimationUtils.rotate(imgArrow, isExpanded());
        });

        TextView txtHeader = (TextView) holder.itemView.findViewById(R.id.txt_listable);
        txtHeader.setText(String.format("%s: %s", uppercaseFirst(type), identifier));

        ImageView imgIcon = (ImageView) holder.itemView.findViewById(R.id.img_icon);

        switch (type.toLowerCase()) {
            case "pack":
                imgIcon.setImageResource(R.drawable.module_96);
                imgIcon.setVisibility(View.VISIBLE);
                break;

            default:
                imgIcon.setVisibility(View.GONE);
        }

        ImageView imgState = (ImageView) holder.itemView.findViewById(R.id.img_state);
        TextView txtPrice = (TextView) holder.itemView.findViewById(R.id.lbl_price);

        if (purchased) {
            imgState.setImageResource(R.drawable.ok_icon);
            imgState.setVisibility(View.VISIBLE);
            txtPrice.setVisibility(View.GONE);
        } else {
            imgState.setVisibility(View.GONE);
            txtPrice.setVisibility(price > 0.0d ? View.VISIBLE : View.GONE);
            txtPrice.setText("£" + getStringPrice());
        }

        Timber.d("ShopItem: " + identifier);
        //AnimationUtils.scaleUp(holder.itemView);
    }

    public String getStringPrice() {
        return String.format(Locale.ENGLISH, "%.2f", price);
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getItemType() {
        return TYPE;
    }

    @Override
    public int compareTo(@NonNull ShopItem o) {
        if (isPack()) {
            Version o1Version = new Version(identifier);
            Version o2Version = new Version(o.identifier);

            if (o1Version.isLowerThan(o2Version.getOriginalString())) {
                return 1;
            } else if (o1Version.isHigherThan(o2Version.getOriginalString())) {
                return -1;
            }

            return 0;
        } else {
            if (price > o.price) {
                return 1;
            } else if (price < o.price) {
                return -1;
            }

            return 0;
        }
    }

    public boolean isPack() {
        if (isPack == null) {
            isPack = type != null && type.equals("pack");
        }

        return isPack;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("identifier", identifier)
                .add("type", type)
                .add("price", price)
                .add("description", description)
                .add("purchased", purchased)
                .toString();
    }

    public static class ShopItemToolbar extends ExpandableItemEntity {
        public static final int LAYOUT_RES = R.layout.shop_item_toolbar;
        public static final int TYPE = 5;
        public static final int LEVEL = 1;
        private ShopItem linkedShopItem;

        public ShopItemToolbar(ShopItem linkedShopItem) {
            this.linkedShopItem = linkedShopItem;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            TextView lblPurchase = getView(holder.itemView, R.id.lbl_purchase);

            if (!linkedShopItem.isPack()) {
                lblPurchase.setText("Donate");
            } else {
                lblPurchase.setText("Purchase");
            }

            ImageButton rocketrBtn = getView(holder.itemView, R.id.btn_rocketr);

            if (linkedShopItem.rocketrLink == null || linkedShopItem.rocketrLink.isEmpty())
                rocketrBtn.setVisibility(View.GONE);
            else {
                rocketrBtn.setVisibility(View.VISIBLE);

                rocketrBtn.setOnClickListener(
                        v -> EventBus.getInstance().post(
                                new ReqItemPurchaseEvent(
                                        PaymentType.ROCKETR,
                                        linkedShopItem
                                ))
                );
            }
        }

        @Override
        public int getLevel() {
            return LEVEL;
        }

        @Override
        public int getItemType() {
            return TYPE;
        }
    }
}