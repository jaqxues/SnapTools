package com.ljmu.andre.snaptools.EventBus.Events;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ShopPurchaseEvent {
    private String title;
    private String message;
    private boolean state;

    public ShopPurchaseEvent(String title, String message, boolean state) {
        this.title = title;
        this.message = message;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean getState() {
        return state;
    }
}
