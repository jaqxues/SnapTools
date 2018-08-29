package com.ljmu.andre.snaptools.Networking.Packets;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
public class AuthPaymentPacket extends AuthResultPacket {
    @SerializedName("payment_state")
    public boolean paymentState;

    @SerializedName("payment_message")
    public String paymentMessage;

    @SerializedName("token")
    public String paymentToken;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("token", paymentToken)
                .add("paymentMessage", paymentMessage)
                .add("paymentState", paymentState)
                .add("", super.toString())
                .toString();
    }
}
