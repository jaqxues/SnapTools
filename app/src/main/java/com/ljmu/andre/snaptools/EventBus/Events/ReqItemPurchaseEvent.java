package com.ljmu.andre.snaptools.EventBus.Events;


import com.ljmu.andre.snaptools.Databases.Tables.ShopItem;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ReqItemPurchaseEvent {
    private PaymentType paymentType;
    private ShopItem shopItem;

    public ReqItemPurchaseEvent(PaymentType paymentType, ShopItem shopItem) {
        this.paymentType = paymentType;
        this.shopItem = shopItem;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }

    public enum PaymentType {
        ROCKETR
    }
}
