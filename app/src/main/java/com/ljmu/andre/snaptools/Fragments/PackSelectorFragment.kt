package com.ljmu.andre.snaptools.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest
import com.ljmu.andre.snaptools.EventBus.Events.PackLoadEvent
import com.ljmu.andre.snaptools.EventBus.Events.PackUnloadEvent
import com.ljmu.andre.snaptools.Framework.FrameworkManager
import com.ljmu.andre.snaptools.Framework.MetaData.FailedPackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.LocalPackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter
import com.ljmu.andre.snaptools.Utils.*
import com.ljmu.andre.snaptools.viewmodel.PackViewModel
import kotlinx.android.synthetic.main.frag_pack_selector.*
import timber.log.Timber


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 13.05.20 - Time 14:23.
 */

class PackSelectorFragment : FragmentHelper() {
    @Suppress("UNCHECKED_CAST")
    val adapter: ExpandableItemAdapter<ExpandableItemAdapter.ExpandableItemEntity<Any>>
        get() {
            return recyclerView.adapter as? ExpandableItemAdapter<ExpandableItemAdapter.ExpandableItemEntity<Any>>
                    ?: throw IllegalStateException("Could not be cast to expected type")
        }
    val recyclerView: RecyclerView
        get() {
            check(runningTutorial) { "Only allowing getting PackView for Tutorials" }
            return recycler_pack_selector
        }
    private val evtHandler = PackEventRequest.EventHandler { handlePackActions(it) }
    private lateinit var viewModel: PackViewModel

    override fun getMenuId() = null

    override fun getName() = TAG

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutContainer = inflater.inflate(R.layout.frag_pack_selector, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PackViewModel::class.java)

        return layoutContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_pack_selector.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ExpandableItemAdapter(emptyList()).apply {
            bindToRecyclerView(recycler_pack_selector)
            addType(PackMetaData.type, PackMetaData.layoutRes)
            addType(ExpandableItemAdapter.TextItemEntity.type, ExpandableItemAdapter.TextItemEntity.layoutRes)
            addType(LocalPackMetaData.LocalPackToolbarItem.type, LocalPackMetaData.LocalPackToolbarItem.layoutRes)
            addType(FailedPackMetaData.FailedPackToolbar.type, FailedPackMetaData.layoutRes)
            setEmptyView(R.layout.layout_empty_packs)
        }

        swipe_layout.setOnRefreshListener {
            if (runningTutorial) {
                swipe_layout.isRefreshing = false
                return@setOnRefreshListener
            }
            viewModel.refreshLocalPacks(evtHandler)
        }

        // Listening to Packs in ViewModel
        viewModel.localMetadata.observe(viewLifecycleOwner, Observer { packs ->

            // Animating
            recycler_pack_selector?.animateSequentGroup()
            adapter.setNewData(packs.toMutableList() as List<ExpandableItemAdapter.ExpandableItemEntity<Any>>?)
            swipe_layout.isRefreshing = false
        })

        viewModel.eventDispatcher.addEventObserver(this) {
            handlePackStateEvents(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearLocalPacks()
    }

    override fun onResume() {
        super.onResume()

        if (runningTutorial) viewModel.setTutorialPacks()
        else viewModel.refreshLocalPacks(evtHandler)
    }

    private fun handlePackStateEvents(packEvents: Any) {
        when(packEvents) {
            is PackLoadEvent -> updatePackState(packEvents.packName)

            is PackUnloadEvent -> updatePackState(packEvents.packMetaData.name)

            is PackDeleteEvent -> {
                val idx = viewModel.getInfoFromName(packEvents.packName, viewModel.localMetadata.value)!!.second
                val adapter = recycler_pack_selector.adapter!! as ExpandableItemAdapter<*>
                adapter.collapse(idx)
                adapter.remove(idx)
            }

            is PackEventRequest -> handlePackActions(packEvents)
        }
    }

    private fun handlePackActions(packRequest: PackEventRequest) {
        when(packRequest.request) {
            PackEventRequest.EventRequest.UNLOAD -> {
                viewModel.unloadPack(packRequest.packName, requireActivity())
            }
            PackEventRequest.EventRequest.LOAD -> {
                viewModel.enablePack(packRequest.packName, requireActivity()).consumeResult {
                    if (it is Result.Error) {
                        FrameworkManager.addFailReason(packRequest.packName, it.exception.message)
                        updatePackState(packRequest.packName)
                    }
                }
            }
            PackEventRequest.EventRequest.DELETE -> {
                fun deletePack() {
                    if (viewModel.deletePack(packRequest.packName, requireActivity(), evtHandler) is Result.Error) {
                        SafeToast.show(activity, "Failed to disable Pack: ${packRequest.packName}", Toast.LENGTH_LONG)
                    }
                }
                val pack = viewModel.localMetadata.value?.find { packRequest.packName == it.name }
                        ?: throw IllegalStateException("Pack to delete was not found")

                // If the Pack has failed to load, delete without confirmation
                if (pack is FailedPackMetaData) {
                    deletePack()
                    return
                }
                DialogFactory.createConfirmation(
                        requireActivity(),
                        "Confirm Action",
                        "Are you sure you wish to delete this pack?",
                        object: ThemedDialog.ThemedClickListener() {
                            override fun clicked(themedDialog: ThemedDialog) {
                                deletePack()
                                themedDialog.dismiss()
                            }
                        }
                )
            }
            else -> Timber.d("Ignoring unhandled request")
        }
    }

    private fun updatePackState(packName: String) {
        var (_, idx) = viewModel.getInfoFromName(packName, viewModel.localMetadata.value) ?: return
        val adapter = recycler_pack_selector.adapter!! as ExpandableItemAdapter<*>
        adapter.notifyItemChanged(idx)
        var el = adapter.getItem(idx)

        if (el == null || !el.isExpanded) return

        while (++idx < adapter.itemCount) {
            el = adapter.getItem(idx)

            if (el !is PackMetaData)
                adapter.notifyItemChanged(idx)
            else break
        }
    }

    override fun progressTutorial() {
    }

    fun generateTutorialData() {
//        viewModel.setTutorialPacks()
    }

    fun generateMetaData() {
//        viewModel.refreshLocalPacks(evtHandler)
    }

    companion object {
        const val TAG = "Pack Selector"
    }
}