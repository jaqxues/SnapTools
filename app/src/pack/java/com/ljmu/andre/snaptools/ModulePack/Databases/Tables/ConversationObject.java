package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@TableName("Conversations")
public class ConversationObject implements CBIObject, MultiItemEntity {
    public static final int TYPE = 0;

    @PrimaryKey
    @TableField("conversation_id")
    @SerializedName("conversation_id")
    public String conv_id;

    @TableField("conversation_name")
    @SerializedName("conversation_name")
    public String conversationName;

    @TableField("your_username")
    @SerializedName("your_username")
    public String yourUsername;

    @TableField("users")
    @SerializedName("users")
    public List<String> users;

    @TableField("last_message_timestamp")
    @SerializedName("last_message_timestamp")
    public Long timestamp;

    private String formattedDate;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
    }

    public void setUsers(String sender, Collection<String> receivers) {
        if (users == null)
            users = Collections.emptyList();

        Set<String> usersSet = new HashSet<>(users);
        usersSet.add(sender);

        if (receivers != null)
            usersSet.addAll(receivers);

        users = new ArrayList<>(usersSet);
    }

    public void setupDisplayView(BaseViewHolder holder) {
        TextView txtUsers = ResourceUtils.getView(holder.itemView, "txt_users");
        txtUsers.setText(getConversationName());
        TextView txtDateTime = ResourceUtils.getView(holder.itemView, "txt_datetime");
        txtDateTime.setText(getFormattedDate(holder.itemView.getContext()));
    }

    public String getConversationName() {
        if (conversationName == null) {
            List<String> usersList = new ArrayList<>(users);
            usersList.remove(yourUsername);

            conversationName = generateFormattedName(usersList);
        }

        return conversationName;
    }

    public String getFormattedDate(Context context) {
        if (formattedDate == null) {
            formattedDate = (String)
                    DateUtils.getRelativeDateTimeString(
                            context,
                            timestamp,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.WEEK_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE
                    );
        }

        return formattedDate;
    }

    @NonNull
    private String generateFormattedName(List<String> usersList) {
        if (usersList.isEmpty()) {
            return "No Name Defined";
        }

        StringBuilder builder = new StringBuilder();
        Iterator<String> userIterator = usersList.iterator();

        while (userIterator.hasNext()) {
            String username = userIterator.next();
            builder.append(username);
            if (userIterator.hasNext())
                builder.append(", ");
        }

        return builder.toString();
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
