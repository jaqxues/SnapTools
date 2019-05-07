package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;

import java.util.List;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName("ChatMessages")
public class ChatObject implements CBIObject {
    @PrimaryKey
    @TableField("message_id")
    @SerializedName("message_id")
    public String chat_message_id;

    @TableField("conversation_id")
    @SerializedName("conversation_id")
    public String conv_id;

    @TableField("message")
    @SerializedName("message")
    public String text;

    @TableField("sender")
    @SerializedName("sender")
    public String from;

    @TableField("receivers")
    @SerializedName("receivers")
    public List<String> to;

    @TableField("timestamp")
    @SerializedName("timestamp")
    public Long timestamp;

    @TableField("sent_by_you")
    @SerializedName("sent_by_you")
    public Boolean sentByYou = false;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {

    }

    public boolean isCompleted() {
        return chat_message_id != null && conv_id != null && text != null && from != null
                && timestamp != null;

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("chat_message_id", chat_message_id)
                .add("conv_id", conv_id)
                .add("text", text)
                .add("from", from)
                .add("to", to)
                .add("timestamp", timestamp)
                .toString();
    }
}
