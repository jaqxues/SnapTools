package com.ljmu.andre.snaptools.UIComponents.Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.Utils.layoutInflater
import com.ljmu.andre.snaptools.data.PackMetadata
import kotlinx.android.synthetic.main.item_listable_msg.view.*
import java.util.*


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 31.05.20 - Time 13:17.
 */
abstract class ExpandableAdapter<T: Any>(dataset: List<T>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), ExpandableListener<T, RecyclerView.ViewHolder> {
    var dataset: List<T> = dataset
        set(value) {
            field = value
            expandedItems.filter { (obj, _) ->
                obj in value
            }
            notifyDataSetChanged()
        }
    protected abstract val parentViewItemType: ParentViewItemType<T, out RecyclerView.ViewHolder>
    private val expandedItems = mutableMapOf<T, List<ViewItem<T, out RecyclerView.ViewHolder>>>()
    private val viewTypes = Vector<ViewType<T, out RecyclerView.ViewHolder>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return (viewTypes[viewType] as ViewType<Any, RecyclerView.ViewHolder>)
                .createViewType(parent, this as ExpandableListener<Any, RecyclerView.ViewHolder>)
    }

    // Parent and Child Items
    override fun getItemCount() = dataset.size + expandedItems.values.sumBy { it.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var idx = -1
        for (value in dataset) {
            if (++idx == position) {
                (parentViewItemType as ViewItem<Any, RecyclerView.ViewHolder>)
                        .bindViewType(holder, value, this as ExpandableListener<Any, RecyclerView.ViewHolder>)
                return
            }
            if (value in expandedItems) {
                for (subItem in expandedItems[value]!!) {
                    if (++idx == position) {
                        (subItem as ViewItem<Any, RecyclerView.ViewHolder>)
                                .bindViewType(holder, value, this as ExpandableListener<Any, RecyclerView.ViewHolder>)
                        return
                    }
                }
            }
        }
    }

    fun <VH: RecyclerView.ViewHolder> registerViewType(type: ViewType<T, VH>) = viewTypes.add(type)

    override fun expandItem(obj: T, items: List<ViewItem<T, out RecyclerView.ViewHolder>>) {
        if (obj in expandedItems)
            throw IllegalArgumentException("Item already expanded")
        expandedItems[obj] = items
        notifyItemRangeInserted(getPositionOfItem(obj) + 1, items.size)
    }

    override fun shrinkItem(obj: T) {
        val items = expandedItems[obj] ?: throw IllegalArgumentException("Item not expanded")
        expandedItems.remove(obj)
        notifyItemRangeRemoved(getPositionOfItem(obj) + 1, items.size)
    }

    override fun getItemViewType(position: Int): Int {
        var idx = -1
        for (value in dataset) {
            if (++idx == position) return 0
            if (value in expandedItems) {
                for (subViewType in expandedItems[value]!!) {
                    if (++idx == position) return viewTypes.indexOf(subViewType.viewType)
                }
            }
        }
        throw IllegalArgumentException("Data integrity not preserved: did not find item $position in Recycler")
    }

    private fun getPositionOfItem(obj: T): Int {
        var idx = -1
        for (value in dataset) {
            ++idx
            if (value == obj) return idx
            if (value in expandedItems) {
                idx += expandedItems[value]!!.size
            }
        }
        throw IllegalStateException("Item not in list")
    }
}

interface ExpandableListener<T: Any, VH: RecyclerView.ViewHolder> {
    fun expandItem(obj: T, items: List<ViewItem<T, out VH>>)
    fun shrinkItem(obj: T)
}

interface ViewType<T: Any, VH: RecyclerView.ViewHolder> {
    fun createViewType(parent: ViewGroup, expandable: ExpandableListener<T, VH>): VH
}

interface ViewItem<T: Any, VH: RecyclerView.ViewHolder> {
    val viewType: ViewType<T, VH>
    fun bindViewType(holder: VH, obj: T, expandable: ExpandableListener<T, VH>)
}

interface ParentViewItemType<T: Any, VH: RecyclerView.ViewHolder> : ViewType<T, VH>, ViewItem<T, VH> {
    override val viewType get() = this
}

inline fun <T: Any, VH: RecyclerView.ViewHolder> ViewType<T, VH>.generateViewItem(crossinline bindView: (holder: VH, obj: T, expandable: ExpandableListener<T, VH>) -> Unit) =
        object: ViewItem<T, VH> {
            override val viewType get() = this@generateViewItem

            override fun bindViewType(holder: VH, obj: T, expandable: ExpandableListener<T, VH>) {
                bindView(holder, obj, expandable)
            }
        }
