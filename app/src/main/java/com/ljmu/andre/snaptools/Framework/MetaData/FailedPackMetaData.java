package com.ljmu.andre.snaptools.Framework.MetaData;

import android.widget.ImageButton;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class FailedPackMetaData extends LocalPackMetaData {
    private String reason;

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public FailedPackMetaData completedBinding() {
        addSubItem(new FailedPackToolbar(
                this,
                getReason()
        ));

        return this;
    }

    public String getReason() {
        return reason;
    }

    public FailedPackMetaData setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public static class FailedPackToolbar extends ExpandableItemEntity {
        public static final int layoutRes = R.layout.failed_pack_toolbar;
        public static final int type = 3;
        private FailedPackMetaData linkedMeta;
        private int level;
        private String text;

        public FailedPackToolbar(FailedPackMetaData linkedMeta, String text) {
            this(linkedMeta, text, 1);
        }

        public FailedPackToolbar(FailedPackMetaData linkedMeta, String text, int level) {
            this.linkedMeta = linkedMeta;
            this.text = text;
            this.level = level;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            TextView label = (TextView) holder.itemView.findViewById(R.id.lbl_reason);
            label.setText(text);

            ImageButton disableBtn = (ImageButton) holder.itemView.findViewById(R.id.btn_delete);
            disableBtn.setOnClickListener(
                    v -> {
                        EventBus.getInstance().post(
                                new PackEventRequest(
                                        EventRequest.UNLOAD,
                                        linkedMeta.getName()
                                ));

                        EventBus.getInstance().post(
                                new PackDeleteEvent(
                                        linkedMeta.getName()
                                ));
                    }
            );
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public int getItemType() {
            return type;
        }
    }
}
