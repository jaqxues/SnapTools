package com.ljmu.andre.snaptools.UIComponents.Adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 31.05.20 - Time 13:17.
 */
abstract class ExpandableAdapter<T: Any>(dataset: List<T>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), ExpandableListener<T> {
    var dataset: List<T> = dataset
        set(value) {
            field = value
            expandedItems.filter { (obj, _) ->
                obj in value
            }
            notifyDataSetChanged()
        }
    private val expandedItems = mutableMapOf<T, IntArray>()
    private val viewTypes = Vector<ViewType<T>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewTypes[viewType].createViewType(parent, this)
    }

    // Parent and Child Items
    override fun getItemCount() = dataset.size + expandedItems.values.sumBy { it.size }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = viewTypes[holder.itemViewType] ?: throw IllegalArgumentException("Unknown ViewType")
        type.bindViewType(holder, getItemAtPosition(position))
    }

    fun registerViewType(type: ViewType<T>) = viewTypes.add(type)

    override fun expandItem(obj: T, items: List<ViewType<T>>) {
        if (obj in expandedItems)
            throw IllegalArgumentException("Item already expanded")
        expandedItems[obj] = IntArray(items.size) { viewTypes.indexOf(items[it]) }
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
                    if (++idx == position) return subViewType
                }
            }
        }
        throw IllegalArgumentException("Data integrity not preserved: did not find item $position in Recycler")
    }

    private fun getItemAtPosition(position: Int): T {
        var idx = -1
        for (value in dataset) {
            if (++idx == position) return value
            if (value in expandedItems) {
                idx += expandedItems[value]!!.size
                if (idx >= position) return value
            }
        }
        throw IllegalStateException("Could not find appropriate item at position $position")
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

interface ExpandableListener<T: Any> {
    fun expandItem(obj: T, items: List<ViewType<T>>)
    fun shrinkItem(obj: T)
}

interface ViewType<T: Any> {
    fun createViewType(parent: ViewGroup, expandable: ExpandableListener<T>): RecyclerView.ViewHolder
    fun bindViewType(holder: RecyclerView.ViewHolder, obj: T)
}
