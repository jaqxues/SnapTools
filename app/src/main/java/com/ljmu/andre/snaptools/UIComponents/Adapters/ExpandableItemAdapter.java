package com.ljmu.andre.snaptools.UIComponents.Adapters;


import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.Utils.RequiresFramework;

import java.util.List;

import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ExpandableItemAdapter<T extends ExpandableItemEntity> extends BaseMultiItemQuickAdapter<T, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ExpandableItemAdapter(List<T> data) {
        super(data);
        Timber.d("Init adapter");
    }

    public int indexOf(T item) {
        int index = -1;

        for (T searchItem : getData()) {
            index++;

            if (searchItem.equals(item))
                return index;
        }

        return -1;
    }

    public int indexOf(int id) {
        int index = -1;

        for (T searchItem : getData()) {
            if (searchItem.getItemId() == id)
                return index;
        }

        return -1;
    }

    @Override
    protected void convert(BaseViewHolder holder, T item) {
        int itemId = item.getItemId();

        if (itemId != -1)
            holder.itemView.setId(itemId);

        item.convert(holder, this);
    }

    public void addType(int type, @LayoutRes int layoutResId) {
        Timber.d("Adding type: " + type + " res: " + layoutResId);
        addItemType(type, layoutResId);
    }

    public boolean toggleItem(BaseViewHolder holder, ExpandableItemEntity item) {
        int pos = holder.getAdapterPosition();

        if (item.isExpanded())
            this.collapse(pos);
        else
            this.expand(pos);

        boolean expanded = item.isExpanded();
        Timber.d("Toggled: " + expanded);
        return expanded;
    }

    public abstract static class ExpandableItemEntity<E> extends AbstractExpandableItem<E> implements MultiItemEntity {
        public abstract void convert(BaseViewHolder holder, ExpandableItemAdapter adapter);

        public int getItemId() {
            return -1;
        }

        public void defaultClickOperation(BaseViewHolder holder, ExpandableItemAdapter adapter, View v) {
            adapter.toggleItem(holder, this);

            if (adapter.getOnItemChildClickListener() != null)
                adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, holder.getLayoutPosition());
        }
    }

    public static class ErrorTextItemEntity extends TextItemEntity {
        public ErrorTextItemEntity(String text) {
            super(text);
        }

        public ErrorTextItemEntity(String text, int level) {
            super(text, level);
        }

        @Override
        protected int getTextColorRes() {
            return R.color.error;
        }
    }

    public static class HtmlTextItemEntity extends TextItemEntity {
        @RequiresFramework(73)
        public HtmlTextItemEntity(String html) {
            super(html);
        }

        @RequiresFramework(73)
        public HtmlTextItemEntity(String html, int level) {
            super(html, level);
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            String html = getText();

            Timber.d("Display: " + html);

            LayoutParams param = holder.itemView.getLayoutParams();

            if (html != null) {
                param.height = LayoutParams.WRAP_CONTENT;
                param.width = LayoutParams.MATCH_PARENT;
                holder.itemView.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
                return;
            }

            TextView label = (TextView) holder.itemView.findViewById(R.id.txt_listable);
            label.setText(getSpannedHtml(html));
            label.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), getTextColorRes()));

            if (html.startsWith("<center>"))
                label.setGravity(Gravity.CENTER_HORIZONTAL);
            else
                label.setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    public static class TextItemEntity extends ExpandableItemEntity {
        public static final int type = 1;
        public static final int layoutRes = R.layout.item_listable_msg;
        public String text;
        public int level;
        @ColorRes
        private int colourRes = R.color.textPrimary;

        public TextItemEntity(String text) {
            this(text, 1);
        }

        public TextItemEntity(String text, int level) {
            this.text = text;
            this.level = level;
        }

        @Override
        public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
            String displayText = getText();

            Timber.d("Display: " + displayText);

            LayoutParams param = holder.itemView.getLayoutParams();

            if (displayText != null) {
                param.height = LayoutParams.WRAP_CONTENT;
                param.width = LayoutParams.MATCH_PARENT;
                holder.itemView.setVisibility(View.VISIBLE);
            } else {
                holder.itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
                return;
            }

            TextView label = (TextView) holder.itemView.findViewById(R.id.txt_listable);
            label.setText(displayText);
            label.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), getTextColorRes()));
            label.setGravity(Gravity.CENTER_VERTICAL);
        }

        public String getText() {
            return text;
        }

        public TextItemEntity setText(String text) {
            this.text = text;
            return this;
        }

        protected @ColorRes
        int getTextColorRes() {
            return colourRes;
        }

        @RequiresFramework(73)
        public TextItemEntity setColourRes(@ColorRes int colourRes) {
            this.colourRes = colourRes;
            return this;
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
