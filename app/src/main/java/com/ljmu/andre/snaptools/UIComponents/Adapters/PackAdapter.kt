package com.ljmu.andre.snaptools.UIComponents.Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.Utils.displayName
import com.ljmu.andre.snaptools.Utils.layoutInflater
import com.ljmu.andre.snaptools.data.PackMetadata
import kotlinx.android.synthetic.main.item_listable_msg.view.*


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 02.06.20 - Time 19:28.
 */

class TextViewType<T: Any>: ViewType<T, TextViewHolder> {
    override fun createViewType(parent: ViewGroup, expandable: ExpandableListener<T, TextViewHolder>) =
            TextViewHolder(parent.context.layoutInflater.inflate(R.layout.item_listable_msg, parent, false))
}

class TextViewHolder(mView: View): RecyclerView.ViewHolder(mView) {
    val textView: TextView = mView.txt_listable
}

class PackAdapter(list: List<PackMetadata>): ExpandableAdapter<PackMetadata>(list) {
    val textViewType = TextViewType<PackMetadata>()

    init {
        registerViewType(textViewType)
    }

    fun expand(idx: Int) {
        expandItem(dataset[idx], listOf(
                textViewType.generateViewItem { holder, _, _ -> holder.textView.text = "Pack Type: Premium" },
                textViewType.generateViewItem { holder, obj, _ -> holder.textView.text = "Snapchat Version: ${obj.scVersion}" },
                textViewType.generateViewItem { holder, obj, _ -> holder.textView.text = "Pack Version: ${obj.packVersion}" },
                textViewType.generateViewItem { holder, obj, _ -> holder.textView.text = "Flavour: ${obj.flavour}" }
        ))
    }

    override val parentViewItemType = object: ParentViewItemType<PackMetadata, TextViewHolder> {
        override fun createViewType(parent: ViewGroup, expandable: ExpandableListener<PackMetadata, TextViewHolder>) =
                TextViewHolder(parent.context.layoutInflater.inflate(R.layout.item_listable_head_stateful, parent, false))

        override fun bindViewType(holder: TextViewHolder, obj: PackMetadata, expandable: ExpandableListener<PackMetadata, TextViewHolder>) {
            holder.textView.text = obj.displayName
        }
    }
}
