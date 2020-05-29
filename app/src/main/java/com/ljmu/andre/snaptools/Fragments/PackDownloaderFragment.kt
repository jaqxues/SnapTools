package com.ljmu.andre.snaptools.Fragments

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jaqxues.akrolyb.prefs.getPref
import com.ljmu.andre.snaptools.Dialogs.Content.PackHistory
import com.ljmu.andre.snaptools.Dialogs.DialogFactory
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest
import com.ljmu.andre.snaptools.Exceptions.PacketResultException
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData
import com.ljmu.andre.snaptools.Networking.Helpers.DownloadModulePack
import com.ljmu.andre.snaptools.Networking.Helpers.GetPackChangelog
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket
import com.ljmu.andre.snaptools.R
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter
import com.ljmu.andre.snaptools.Utils.*
import com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_PACKS
import com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml
import com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight
import com.ljmu.andre.snaptools.viewmodel.PackViewModel
import kotlinx.android.synthetic.main.frag_pack_downloader.*
import timber.log.Timber


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 25.05.20 - Time 19:18.
 */
class PackDownloaderFragment : FragmentHelper(), BaseQuickAdapter.OnItemChildClickListener {
    @Suppress("UNCHECKED_CAST")
    val adapter: ExpandableItemAdapter<ExpandableItemAdapter.ExpandableItemEntity<Any>>
        get() {
            return recyclerView.adapter as? ExpandableItemAdapter<ExpandableItemAdapter.ExpandableItemEntity<Any>>
                    ?: throw IllegalStateException("Could not be cast to expected type")
        }
    val recyclerView: RecyclerView
        get() {
            check(runningTutorial) { "Only allowing getting PackView for Tutorials" }
            return recycler_pack_downloader
        }
    private lateinit var viewModel: PackViewModel
    override fun getMenuId() = null

    override fun getName() = TAG

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_pack_downloader, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(PackViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_pack_downloader.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ExpandableItemAdapter(emptyList()).apply {
            bindToRecyclerView(recycler_pack_downloader)
            addType(PackMetaData.type, PackMetaData.layoutRes)
            addType(ExpandableItemAdapter.TextItemEntity.type, ExpandableItemAdapter.TextItemEntity.layoutRes)
            addType(ServerPackMetaData.ServerPackToolbarItem.type, ServerPackMetaData.ServerPackToolbarItem.layoutRes)
            setEmptyView(R.layout.layout_empty_packs)
            setOnItemChildClickListener(this@PackDownloaderFragment)
        }

        swipe_layout.setOnRefreshListener {
            if (runningTutorial) {
                swipe_layout.isRefreshing = false
                return@setOnRefreshListener
            }
            viewModel.refreshRemotePacks(PackEventRequest.EventHandler.ignoreEvents, requireActivity(), true)
        }

        viewModel.remoteMetadata.observe(this.viewLifecycleOwner, Observer { request ->
            if (request is Request.Pending) {
                swipe_layout.isRefreshing = true
                return@Observer
            }
            request as Request.Loaded

            swipe_layout.isRefreshing = false
            if (runningTutorial) return@Observer

            when (val result = request.result) {
                is Result.Success -> {
                    recycler_pack_downloader?.animateSequentGroup()
                    val packs = result.data.toMutableList()
                    packs.sort()

                    adapter.setNewData(packs as List<ExpandableItemAdapter.ExpandableItemEntity<Any>>?)
                    if (activity?.isFinishing == false) updateLastChecked()
                }
                is Result.Error -> {
                    val activity = activity ?: return@Observer
                    val exception = result.exception as PacketResultException
                    DialogFactory.createErrorDialog(
                            activity,
                            exception.title,
                            exception.message,
                            exception.errorCode
                    ).show()
                }
            }
        })

        viewModel.eventDispatcher.addEventObserver(this) {
            handlePackEvent(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (runningTutorial) viewModel.setTutorialPacks()
        else viewModel.refreshRemotePacks(PackEventRequest.EventHandler.ignoreEvents, requireActivity(), false)
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearRemotePacks()
    }

    override fun progressTutorial() {}

    private fun updateLastChecked() {
        val lastChecked = LAST_CHECK_PACKS.getPref()
        if (lastChecked == 0L) {
            txt_last_checked.visibility = View.GONE
            return
        }

        val formatted = DateUtils.getRelativeDateTimeString(
                activity,
                lastChecked,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        ) as String

        txt_last_checked.text = getSpannedHtml("Last Checked: " + htmlHighlight(formatted))
        txt_last_checked.visibility = View.VISIBLE
    }

    private fun handlePackEvent(evt: Any) {
        when (evt) {
            is PackDeleteEvent -> {
            }
            is PackDownloadEvent -> {
                when (evt.state) {
                    PackDownloadEvent.DownloadState.SUCCESS -> {
                        DialogFactory.createBasicMessage(
                                requireActivity(),
                                "Success",
                                "Module Pack successfully downloaded"
                        ).show()
                        PackUtils.killSCService(activity)
                    }
                    PackDownloadEvent.DownloadState.FAIL -> {
                        DialogFactory.createErrorDialog(
                                requireActivity(),
                                "Download Failed",
                                "Failed to download Pack File\n\n${evt.message}",
                                evt.responseCode
                        ).show()
                    }
                    PackDownloadEvent.DownloadState.SKIP -> {
                        Timber.i("Ignoring skipped download state")
                    }
                    null -> {
                        throw IllegalStateException()
                    }
                }
            }
            is PackEventRequest -> handlePackEventRequest(evt)
        }
    }

    private fun handlePackEventRequest(evt: PackEventRequest) {
        when (evt.request) {
            PackEventRequest.EventRequest.DOWNLOAD -> {
                DialogFactory.createConfirmation(
                        requireActivity(),
                        "Download Pack",
                        "Are you sure you wish to download pack \"${evt.packName}\"",
                        object : ThemedDialog.ThemedClickListener() {
                            override fun clicked(themedDialog: ThemedDialog) {
                                val metadata = viewModel.getServerPack(evt.packName)
                                        ?: throw IllegalStateException("Pack not found, but packs loaded")

                                viewModel.downloadPack(requireActivity(), metadata)
                                themedDialog.dismiss()
                            }
                        },
                        object : ThemedDialog.ThemedClickListener() {
                            override fun clicked(themedDialog: ThemedDialog) {
                                themedDialog.dismiss()
                            }
                        }
                ).show()
            }
            PackEventRequest.EventRequest.SHOW_ROLLBACK -> {
                val metadata = viewModel.getServerPack(evt.packName)
                if (metadata == null) {
                    SafeToast.show(
                            requireActivity(),
                            "Failed to get PackMetadata for \"${evt.packName}\"",
                            Toast.LENGTH_LONG,
                            true
                    )
                    return
                }
                ThemedDialog(requireActivity()).apply {
                    setTitle("Pack History")
                    setExtension(PackHistory()
                            .setActivity(requireActivity())
                            .setPackType(metadata.type)
                            .setScVersion(metadata.scVersion)
                            .setFlavour(metadata.flavour)
                            .setSelectedPackCallable {
                                Timber.d("Selected: $it")

                                DialogFactory.createConfirmation(
                                        requireActivity(),
                                        "Download old pack?",
                                        "Are you sure you would like to download an old Pack Version? (${it.packVersion})",
                                        object : ThemedDialog.ThemedClickListener() {
                                            override fun clicked(themedDialog: ThemedDialog) {
                                                DownloadModulePack(
                                                        requireActivity(),
                                                        it.name, it.scVersion, it.packType, it.development, it.packVersion, it.flavour
                                                ).download()
                                                themedDialog.dismiss()
                                                dismiss()
                                            }
                                        }
                                )
                            }
                    )
                    show()
                }
            }
            PackEventRequest.EventRequest.SHOW_CHANGELOG -> {
                val metadata = viewModel.getServerPack(evt.packName)
                if (metadata == null) {
                    SafeToast.show(
                            requireActivity(),
                            "Failed to retrieve Metadata for specified pack",
                            Toast.LENGTH_LONG, true
                    )
                    return
                }
                val livedata = viewModel.requestChangeLog(requireActivity(), metadata)
                livedata.observe(viewLifecycleOwner, object: Observer<Request<PackDataPacket>> {
                    lateinit var progressDialog: ThemedDialog
                    override fun onChanged(t: Request<PackDataPacket>) {
                        if (t is Request.Pending) {
                            progressDialog = DialogFactory.createProgressDialog(
                                    requireActivity(),
                                    "Loading Changelogs",
                                    "Retrieving Changelogs...",
                                    GetPackChangelog.TAG, true
                            )
                            progressDialog.show()
                            return
                        }
                        t as Request.Loaded
                        progressDialog.dismiss()

                        when (t.result) {
                            is Result.Success -> {
                                val packet = t.result.data
                                DialogFactory.createBasicMessage(
                                        requireActivity(),
                                        "Pack Changelog",
                                        """
                                            Snapchat Version: ${packet.scVersion}
                                            Pack Type: ${packet.packType}
                                            
                                            Release Notes:
                                            ${packet.changelog}
                                            """.trimIndent()
                                ).show()
                            }
                            is Result.Error -> {
                                val ex = t.result.exception as PacketResultException
                                DialogFactory.createErrorDialog(
                                        requireActivity(),
                                        ex.title, ex.message, ex.errorCode
                                ).show()
                            }
                        }
                        livedata.removeObserver(this)
                    }
                })
            }
            else -> Timber.d("Ignoring unhandled request")
        }
    }

    fun generateTutorialData() {
//        viewModel.setTutorialPacks()
    }

    fun generateMetaData(invalidateCache: Boolean) {
//        viewModel.refreshLocalPacks(evtHandler)
    }

    companion object {
        const val TAG = "Pack Downloader"
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val item = adapter.getItem(position) as? ExpandableItemAdapter.ExpandableItemEntity<*>

        if (item == null) {
            Timber.w("Null item clicked")
            return
        }

        if (item.isExpanded) {
            val parent = parentFragment as PackManagerFragment
            parent.handleExpandedItem(adapter.getItem(position) as ExpandableItemAdapter.ExpandableItemEntity<*>)
        }
    }
}