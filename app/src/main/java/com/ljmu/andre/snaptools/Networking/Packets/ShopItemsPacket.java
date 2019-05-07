package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.snaptools.Databases.Tables.ShopItem;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ShopItemsPacket extends AuthResultPacket {
    @SerializedName("shop_items")
    public ShopItem[] shopItems;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("shopItems", shopItems)
                .add("", super.toString())
                .toString();
    }
}
