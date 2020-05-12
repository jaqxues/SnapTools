package com.ljmu.andre.snaptools.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.snaptools.Dialogs.Content.PackHistory;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ItemExpandedEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDeleteEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent;
import com.ljmu.andre.snaptools.EventBus.Events.PackDownloadEvent.DownloadState;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.PackEventRequest.EventRequest;
import com.ljmu.andre.snaptools.EventBus.Events.ReqLoadFragmentEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ReqPackDownloaderRefreshEvent;
import com.ljmu.andre.snaptools.Framework.MetaData.PackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData;
import com.ljmu.andre.snaptools.Framework.MetaData.ServerPackMetaData.ServerPackToolbarItem;
import com.ljmu.andre.snaptools.Networking.Helpers.DownloadModulePack;
import com.ljmu.andre.snaptools.Networking.Helpers.GetPackChangelog;
import com.ljmu.andre.snaptools.Networking.Helpers.GetServerPacks;
import com.ljmu.andre.snaptools.Networking.Packets.PackDataPacket;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.PacketResultListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PackUtils;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.LAST_CHECK_PACKS;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.SELECTED_PACKS;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSpannedHtml;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.StringUtils.htmlHighlight;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class PackDownloaderFragment
        extends FragmentHelper implements BaseQuickAdapter.OnItemChildClickListener {

    // ===========================================================================

    private static final String TAG = "Pack Downloader";
    private final List<ServerPackMetaData> packs = new ArrayList<>();


    @Nullable
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_pack_downloader)
    RecyclerView list_packs;
    @BindView(R.id.txt_last_checked)
    TextView txtLastChecked;
    ExpandableItemAdapter<ExpandableItemEntity> adapter;
    private Unbinder unbinder;

    // ===========================================================================

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View layoutContainer = inflater.inflate(R.layout.frag_pack_downloader, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);

        list_packs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpandableItemAdapter(packs);
        adapter.bindToRecyclerView(list_packs);
        adapter.addType(PackMetaData.type, PackMetaData.layoutRes);
        adapter.addType(TextItemEntity.type, TextItemEntity.layoutRes);
        adapter.addType(ServerPackToolbarItem.type, ServerPackToolbarItem.layoutRes);
        adapter.setEmptyView(R.layout.layout_empty_packs);

        adapter.setOnItemChildClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    if (runningTutorial) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }

                    generateMetaData(true);
                }
        );

        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (runningTutorial)
            generateTutorialData();
        else
            generateMetaData(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        packs.clear();
    }

    public void generateMetaData(boolean invalidateCache) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);

		GetServerPacks.getServerPacks(
				getActivity(),
				invalidateCache,
                new WebResponse.ServerListResultListener<ServerPackMetaData>() {
					@Override public void success(List<ServerPackMetaData> metaDataList) {
						if (swipeRefreshLayout != null)
							swipeRefreshLayout.setRefreshing(false);

						if (runningTutorial)
							return;

						if (metaDataList == null) {
							error("There was an unhandled error during ModulePack fetching", null, -1);
							return;
						}

                        Iterator<ServerPackMetaData> iterator = metaDataList.iterator();
                        while (iterator.hasNext()) {
                            ServerPackMetaData metaData = iterator.next();
                            if (!metaData.getFlavour().equals(Constants.getApkFlavor()))
                                iterator.remove();
                        }
						setPacks(metaDataList);
					}

					@Override public void error(String message, Throwable t, int errorCode) {
						Timber.e(t, message);
						if (swipeRefreshLayout != null)
							swipeRefreshLayout.setRefreshing(false);

						if (runningTutorial)
							return;

						if (getActivity() == null)
							return;

						DialogFactory.createErrorDialog(
								getActivity(),
								"Error Fetching Server Packs",
								message,
								errorCode
						).show();

						if (packs.isEmpty())
							adapter.setEmptyView(R.layout.layout_empty_packs);
					}
				}
        );
    }

    public void setPacks(Collection<ServerPackMetaData> newPacks) {
        if (list_packs != null && list_packs.getViewTreeObserver() != null) {
            list_packs.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //At this point the layout is complete and the
                    //dimensions of recyclerView and any child views are known.

                    AnimationUtils.sequentGroup(list_packs);

                    list_packs.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        packs.clear();
        packs.addAll(newPacks);
        Collections.sort(packs);

        if (adapter != null) {
            if (packs.isEmpty())
                adapter.setEmptyView(R.layout.layout_empty_packs);
            else
                adapter.notifyDataSetChanged();
        }

        if (getActivity() != null && !getActivity().isFinishing())
            updateLastChecked();
    }

    private void updateLastChecked() {
        Long lastCheckedTimestamp = getPref(LAST_CHECK_PACKS);

        if (lastCheckedTimestamp == 0L) {
            txtLastChecked.setVisibility(View.GONE);
            return;
        }

        String formattedTime = (String) DateUtils.getRelativeDateTimeString(
                getActivity(),
                lastCheckedTimestamp,
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
        );

        txtLastChecked.setText(getSpannedHtml(
                "Last Checked: " + htmlHighlight(formattedTime)
        ));
        txtLastChecked.setVisibility(View.VISIBLE);
    }

    public void generateTutorialData() {
        List<String> tutorialVersions = new ImmutableList.Builder<String>()
                .add("10.0.0.0")
                .add("10.1.0.1")
                .add("10.12.1.0")
                .add("10.16.0.0")
                .build();

        List<ServerPackMetaData> tutorialDataList = new ArrayList<>(tutorialVersions.size());

        for (String tutorialVersion : tutorialVersions)
            tutorialDataList.add(ServerPackMetaData.getTutorialPack(tutorialVersion));

        setPacks(tutorialDataList);
    }

    public RecyclerView getRecyclerView() {
        return list_packs;
    }

    public ExpandableItemAdapter<ExpandableItemEntity> getAdapter() {
        return adapter;
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public Integer getMenuId() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.soleUnregister(this);
    }

    @Override
    public void progressTutorial() {
    }

    public void updatePackState(String packName) {
        updatePackState(getIndexFromName(packName));
    }

    public void updatePackState(int packIndex) {
        if (packIndex != -1) {
            adapter.notifyItemChanged(packIndex);
            ExpandableItemEntity entity = adapter.getItem(packIndex);

            if (entity == null || !entity.isExpanded())
                return;

            while (++packIndex < adapter.getItemCount()) {
                entity = adapter.getItem(packIndex);

                Timber.d("Entity: " + entity);
                if (!(entity instanceof PackMetaData)) {
                    adapter.notifyItemChanged(packIndex);
                } else
                    break;
            }

            for (int i = 0; i < adapter.getItemCount(); i++)
                Timber.d("Adapter item: " + adapter.getItem(i));
        }
    }

    public int getIndexFromName(String packName) {
        int index = -1;

        int id = getIdFromString(packName);
        View view = list_packs.findViewById(id);
        Timber.d("Searching for: " + id + " : " + Integer.toHexString(id) + " : " + packName);

        for (int i = 0; i < list_packs.getChildCount(); i++) {
            Timber.d("View: " + list_packs.getChildAt(i).toString());
        }

        return list_packs.indexOfChild(view);
		/*
		Timber.d("PackView: " + view);

		Timber.d("Adapter Count: " + adapter.getItemCount());

		for (Object packObj : packs) {
			index++;

			if (!(packObj instanceof ServerPackMetaData))
				continue;

			ServerPackMetaData metaData = (ServerPackMetaData) packObj;
			Timber.d("PACK" + index + ":" + metaData.getName());
			if (metaData.getName().equals(packName))
				return index;
		}

		return -1;*/
    }

    @Subscribe
    public void handleReqPackDownloaderRefreshEvent(ReqPackDownloaderRefreshEvent refreshEvent) {
        if (!runningTutorial)
            generateMetaData(true);
    }

    @Subscribe
    public void handlePackDownloadEvent(PackDownloadEvent downloadEvent) {
        Timber.d("Pack Download Event: " + downloadEvent.toString());

        if (downloadEvent.getState() == DownloadState.SUCCESS) {
            DialogFactory.createBasicMessage(
                    getActivity(),
                    "Module Pack Downloaded",
                    "Module pack successfully downloaded"
            ).show();

            PackUtils.killSCService(getActivity());

            PackMetaData metaData = downloadEvent.getMetaData();

            if (metaData == null)
                adapter.notifyDataSetChanged();
            else {
                if (collectionContains(SELECTED_PACKS, metaData.getName()))
                    EventBus.getInstance().post(new PackEventRequest(EventRequest.LOAD, metaData.getName()));

                Pair<ServerPackMetaData, Integer> metaIndexPair =
                        getMetaDataAndIndexFromName(metaData.getName());

                if (metaIndexPair == null)
                    adapter.notifyDataSetChanged();
                else {
                    ServerPackMetaData serverMetaData = metaIndexPair.first;
                    int index = metaIndexPair.second;

                    serverMetaData.setInstalled(true);
                    serverMetaData.setHasUpdate(false);
                    updatePackState(index);
                }
            }
        } else if (downloadEvent.getState() == DownloadState.FAIL) {
            if (downloadEvent.getResponseCode() == 102) {
                DialogFactory.createConfirmation(
                        getActivity(),
                        "Module Pack Download Failed",
                        "This is a premium pack which has not yet been purchased.\n\nGo to the shop?",
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                EventBus.getInstance().post(new ReqLoadFragmentEvent(
                                        R.id.nav_shop,
                                        "Shop"
                                ));

                                themedDialog.dismiss();
                            }
                        }
                ).setHeaderDrawable(R.drawable.error_header).show();
            } else {
                DialogFactory.createErrorDialog(
                        getActivity(),
                        "Module Pack Download Failed",
                        "Error Downloading File\n\n" + downloadEvent.getMessage(),
                        downloadEvent.getResponseCode()
                ).show();
            }
        }
    }

    @Nullable
    public Pair<ServerPackMetaData, Integer> getMetaDataAndIndexFromName(String packName) {
        int index = -1;
        for (Object packObj : packs) {
            index++;
            if (!(packObj instanceof ServerPackMetaData))
                continue;

            ServerPackMetaData metaData = (ServerPackMetaData) packObj;

            if (metaData.getName().equals(packName))
                return new Pair<>(metaData, index);
        }

        return null;
    }

    @Subscribe
    public void handlePackDeleteEvent(PackDeleteEvent deleteEvent) {
        Pair<ServerPackMetaData, Integer> metaIndexPair =
                getMetaDataAndIndexFromName(deleteEvent.getPackName());

        if (metaIndexPair == null)
            adapter.notifyDataSetChanged();
        else {
            ServerPackMetaData metaData = metaIndexPair.first;
            int index = metaIndexPair.second;

            metaData.setInstalled(false);
            metaData.setHasUpdate(false);
            updatePackState(index);
        }
    }

    @Subscribe
    public void handlePackEventRequest(PackEventRequest eventRequest) {
        EventRequest request = eventRequest.getRequest();
        Timber.d("New Event Request: " + request.toString());
        ServerPackMetaData metaData;

        switch (request) {
            case DOWNLOAD:
                DialogFactory.createConfirmation(
                        getActivity(),
                        "Download Pack",
                        "Are you sure you wish to download pack: " + eventRequest.getPackName(),
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                ServerPackMetaData metaData = getMetaDataFromName(eventRequest.getPackName());

                                Assert.notNull("Null metadata for: " + eventRequest.getPackName(), metaData);

                                new DownloadModulePack(
                                        ContextHelper.getActivity(),
                                        metaData.getName(),
                                        metaData.getScVersion(),
                                        metaData.getType(),
                                        metaData.isDeveloper(),
                                        metaData.getPackVersion(),
                                        metaData.getFlavour()
                                ).download();

                                themedDialog.dismiss();
                            }
                        },
                        new ThemedClickListener() {
                            @Override
                            public void clicked(ThemedDialog themedDialog) {
                                EventBus.getInstance().post(
                                        new PackDownloadEvent()
                                                .setState(DownloadState.SKIP)
                                );

                                themedDialog.dismiss();
                            }
                        }
                ).show();

                break;
            case SHOW_ROLLBACK:
                metaData = getMetaDataFromName(eventRequest.getPackName());

                if (metaData == null) {
                    Timber.e("Pack not found: " + eventRequest.getPackName());
                    SafeToast.show(
                            getActivity(),
                            "Failed to retrieve Rollback Data",
                            Toast.LENGTH_LONG,
                            true
                    );
                    return;
                }

                ThemedDialog historyDialog = new ThemedDialog(getActivity())
                        .setTitle("Pack History");
                historyDialog.setExtension(
                        new PackHistory()
                                .setActivity(getActivity())
                                .setPackType(metaData.getType())
                                .setScVersion(metaData.getScVersion())
                                .setFlavour(metaData.getFlavour())
                                .setSelectedPackCallable(
                                        // Pack selected to download =================================================
                                        historyObject -> {
                                            Timber.d("Selected: " + historyObject);

                                            DialogFactory.createConfirmation(
                                                    getActivity(),
                                                    "Download old pack?",
                                                    "Are you sure you would like to download the old pack version: " + historyObject.packVersion,
                                                    new ThemedClickListener() {
                                                        @Override
                                                        public void clicked(ThemedDialog themedDialog) {
                                                            new DownloadModulePack(
                                                                    ContextHelper.getActivity(),
                                                                    historyObject.getName(),
                                                                    historyObject.scVersion,
                                                                    historyObject.packType,
                                                                    historyObject.development,
                                                                    historyObject.packVersion,
                                                                    historyObject.flavour)
                                                                    .download();

                                                            themedDialog.dismiss();
                                                            historyDialog.dismiss();
                                                        }
                                                    }
                                            ).show();
                                        }
                                )
                );

                historyDialog.show();
                break;
            case SHOW_CHANGELOG:
                metaData = getMetaDataFromName(eventRequest.getPackName());

                if (metaData == null) {
                    Timber.e("Pack not found: " + eventRequest.getPackName());
                    SafeToast.show(
                            getActivity(),
                            "Failed to retrieve Changelog Data",
                            Toast.LENGTH_LONG,
                            true
                    );
                    return;
                }

                ThemedDialog progressDialog = DialogFactory.createProgressDialog(
                        getActivity(),
                        "Loading Changelogs",
                        "Retrieving Changelogs... This may take up to 30 seconds",
                        GetPackChangelog.TAG,
                        true
                );

                progressDialog.show();

                GetPackChangelog.performCheck(
                        getActivity(),
                        metaData.getType(),
                        metaData.getScVersion(),
                        metaData.getFlavour(),
                        new PacketResultListener<PackDataPacket>() {
                            @Override
                            public void success(String message, PackDataPacket packet) {
                                progressDialog.dismiss();

                                DialogFactory.createBasicMessage(
                                        getActivity(),
                                        "Pack Changelog",
                                        "Snapchat Version: " + packet.getSCVersion()
                                                + "\nPack Type: " + packet.getPackType()
                                                + "\n\nRelease Notes:\n" + packet.getChangelog()
                                ).show();
                            }

                            @Override
                            public void error(String message, Throwable t, int errorCode) {
                                progressDialog.dismiss();

                                DialogFactory.createErrorDialog(
                                        getActivity(),
                                        "Issue Getting Changelog",
                                        message,
                                        errorCode
                                ).show();
                            }
                        }
                );
                break;
            default:
                Timber.d("Ignoring Unhandled Request: " + request);
        }
    }

    @Nullable
    public ServerPackMetaData getMetaDataFromName(String packName) {
        Pair<ServerPackMetaData, Integer> result = getMetaDataAndIndexFromName(packName);

        return result == null ? null : result.first;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        ExpandableItemEntity item = (ExpandableItemEntity) adapter.getItem(position);

        if (item == null) {
            // TODO: Make this fit into the tutorial system
            Timber.w("Null item clicked");
            return;
        }

        if (item.isExpanded())
            EventBus.getInstance().post(new ItemExpandedEvent(item));
    }
}
