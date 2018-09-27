package com.ljmu.andre.snaptools.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.ljmu.andre.snaptools.Databases.Tables.ShopItem;
import com.ljmu.andre.snaptools.Databases.Tables.ShopItem.ShopItemToolbar;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.EventBus.Events.ReqItemPurchaseEvent;
import com.ljmu.andre.snaptools.EventBus.Events.ReqItemPurchaseEvent.PaymentType;
import com.ljmu.andre.snaptools.EventBus.Events.ShopPurchaseEvent;
import com.ljmu.andre.snaptools.Fragments.Tutorials.ShopTutorial;
import com.ljmu.andre.snaptools.Networking.Helpers.GetShopItems;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.RedactedClasses.Answers;
import com.ljmu.andre.snaptools.RedactedClasses.CustomEvent;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.TextItemEntity;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.CustomObservers.ErrorObserver;
import com.ljmu.andre.snaptools.Utils.SafeToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.Utils.FrameworkPreferencesDef.HAS_SHOWN_PAY_MODEL_REASONING;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("unused")
public class ShopFragment extends FragmentHelper {

    Unbinder unbinder;
    @BindView(R.id.recycler_packs)
    RecyclerView packRecycler;
    @BindView(R.id.recycler_donations)
    RecyclerView donationRecycler;
    @BindView(R.id.swipe_layout)
    @Nullable
    SwipeRefreshLayout swipeRefreshLayout;

    private ExpandableItemAdapter<ExpandableItemEntity> packAdapter;
    private ExpandableItemAdapter<ExpandableItemEntity> donationAdapter;

    private List<ShopItem> packItems = new ArrayList<>();
    private List<ShopItem> donationItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutContainer = inflater.inflate(R.layout.frag_shop, container, false);
        unbinder = ButterKnife.bind(this, layoutContainer);
        EventBus.soleRegister(this);

        getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        packRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        donationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Statement causes issues due to class casting ==============================
        packAdapter = new ExpandableItemAdapter(packItems);
        packAdapter.bindToRecyclerView(packRecycler);
        packAdapter.addType(ShopItem.TYPE, ShopItem.LAYOUT_RES);
        packAdapter.addType(ShopItemToolbar.TYPE, ShopItemToolbar.LAYOUT_RES);
        packAdapter.addType(TextItemEntity.type, TextItemEntity.layoutRes);
        packAdapter.setEmptyView(R.layout.layout_empty_shop_items);
        packAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (runningTutorial) {
                progressTutorial();
            }
        });

        // Statement causes issues due to class casting ==============================
        //noinspection unchecked
        donationAdapter = new ExpandableItemAdapter(donationItems);
        donationAdapter.bindToRecyclerView(donationRecycler);
        donationAdapter.addType(ShopItem.TYPE, ShopItem.LAYOUT_RES);
        donationAdapter.addType(ShopItemToolbar.TYPE, ShopItemToolbar.LAYOUT_RES);
        donationAdapter.addType(TextItemEntity.type, TextItemEntity.layoutRes);
        donationAdapter.setEmptyView(R.layout.layout_empty_shop_items);
        donationAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (runningTutorial) {
                progressTutorial();
            }
        });

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(
                    () -> {
                        if (runningTutorial)
                            generateTutorialItems();
                        else
                            generateShopItems(true);
                    }
            );
        }

        setTutorialDetails(ShopTutorial.getTutorials(packRecycler, donationRecycler));

        showPaymentModelReasoning();
        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (runningTutorial)
            generateTutorialItems();
        else
            generateShopItems(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        packItems.clear();
        donationItems.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.soleUnregister(this);
        getActivity().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // TODO: Create Tutorial Items
    private void generateTutorialItems() {
//		this.packItems.clear();
//		this.donationItems.clear();
//
//		String[] tutorialItems = new String[]{
//				"Item 1", "Item 2",
//				"Item 3", "Item 4",
//				"Item 5", "Item 6"
//		};
//
//		int index = -1;
//		for (String tutorialItem : tutorialItems) {
//			ShopItem tutorialShopItem = ShopItem.generateTutorialItem(tutorialItem);
//			shopItems.add(tutorialShopItem);
//
//			if (includePending && ++index % 2 == 0) {
//				tutorialShopItem.isPending = true;
//				pendingOrders.add(PurchaseTable.generateTutorialItem(tutorialItem));
//			}
//		}
//
//		shopAdapter.notifyDataSetChanged();
//
//		if (pendingOrders.isEmpty() && pendingOrderContainer.getVisibility() == View.VISIBLE)
//			AnimationUtils.collapse(pendingOrderContainer);
//		else if (!pendingOrders.isEmpty() && pendingOrderContainer.getVisibility() != View.VISIBLE)
//			AnimationUtils.expand(pendingOrderContainer);
//
//		if (pendingOrderAdapter != null)
//			pendingOrderAdapter.notifyDataSetChanged();
    }

    private void generateShopItems(boolean invalidateCache) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
        WebResponse.ServerListResultListener<ShopItem> resultListener =
                new WebResponse.ServerListResultListener<ShopItem>() {
                    @Override
                    public void success(List<ShopItem> list) {
                        if (swipeRefreshLayout != null)
                            swipeRefreshLayout.setRefreshing(false);

                        if (runningTutorial)
                            return;

                        if (list == null)
                            error("There was an unhandled error during Shop Item fetching", null, -1);

                        setShopItems(list);
                    }

                    @Override
                    public void error(String message, Throwable t, int responseCode) {
                        if (swipeRefreshLayout != null)
                            swipeRefreshLayout.setRefreshing(false);

                        if (runningTutorial)
                            return;

                        if (t != null)
                            Timber.e(t, message);

                        if (getActivity() == null)
                            return;

                        DialogFactory.createErrorDialog(
                                getActivity(),
                                "Error Fetching Shop Items",
                                message,
                                responseCode
                        ).show();
                    }
                };

        if (GetShopItems.shouldUseCache() && !invalidateCache) {
            GetShopItems.getCacheObservable()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DefaultObserver<List<ShopItem>>() {
                        @Override
                        public void onNext(@NonNull List<ShopItem> shopItems) {
                            if (shopItems.isEmpty()) {
                                GetShopItems.getFromServer(
                                        getActivity(),
                                        resultListener
                                );

                                return;
                            }

                            if (swipeRefreshLayout != null)
                                swipeRefreshLayout.setRefreshing(false);
                            resultListener.success(shopItems);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            resultListener.error(e.getMessage(), e, -1);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            GetShopItems.getFromServer(
                    getActivity(),
                    resultListener
            );
        }
    }

    private void showPaymentModelReasoning() {
        String message = "SnapTools uses a <b><font color='#EFDE86'>Pay Per Version</font></b> model instead of a <font color='#EF8686'>monthly subscription</font> based system so that you only pay when you want to use a newer Snapchat version.\n" +
                "\n" +
                "This aims to provide a cheaper method for our users, but still make it worth our time for maintaining the software.\n" +
                "\n" +
                "It should be stressed that you are <b><font color='#EFDE86'>NEVER</font></b> forced to update to any version. You are free to use any premium packs you purchased for as long as you are able to use them.\n" +
                "\n" +
                "SnapTools also offers a <b><font color='#EFDE86'>7 Day Guarantee</font></b> so that if a pack for a newer Snapchat is released within 7 days of you purchasing a pack, you will automatically be given access to the newer pack as well.";

        if ((boolean) getPref(HAS_SHOWN_PAY_MODEL_REASONING) || message.isEmpty())
            return;

        DialogFactory.createBasicMessage(
                getActivity(),
                "Payment Model",
                message,
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        themedDialog.dismiss();
                        putPref(HAS_SHOWN_PAY_MODEL_REASONING, true);
                    }
                }
        ).setDismissable(false).show();
    }

    public void setShopItems(Collection<ShopItem> shopItems) {
        addGlobalListener(packRecycler);
        addGlobalListener(donationRecycler);

        Timber.d("Setting shop items");

        packItems.clear();
        donationItems.clear();

        for (ShopItem shopItem : shopItems) {
            if (shopItem.type.equals("pack"))
                packItems.add(shopItem);
            else
                donationItems.add(shopItem);

            Timber.d("ShopItem: " + shopItem.toString());
        }

        Collections.sort(packItems);
        Collections.sort(donationItems);

        if (packAdapter != null) {
            packAdapter.notifyDataSetChanged();
        }

        if (donationAdapter != null) {
            donationAdapter.notifyDataSetChanged();
        }
    }

    private void addGlobalListener(RecyclerView recyclerView) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean hasAnimated = false;

            @Override
            public void onGlobalLayout() {
                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.

                if (!hasAnimated) {
                    hasAnimated = true;
                    AnimationUtils.sequentGroup(recyclerView);
                }

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public String getName() {
        return "Shop/Donations";
    }

    @Override
    public Integer getMenuId() {
        return R.id.nav_shop;
    }

	/*@Override public boolean hasTutorial() {
		return true;
	}

	@Override public void triggerTutorial(boolean isFullTutorial) {
		super.triggerTutorial(isFullTutorial);
		generateTutorialItems(false);
	}*/


    @Subscribe
    public void handleShopPurchaseEvent(ShopPurchaseEvent shopPurchaseEvent) {
        Observable.create(
                e -> {
                    if (runningTutorial)
                        generateTutorialItems();
                    else
                        generateShopItems(true);

                    e.onComplete();
                }
        ).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new ErrorObserver<>());
    }

    @Subscribe
    public void handleReqItemPurchaseEvent(ReqItemPurchaseEvent purchaseEvent) {
        ShopItem item = purchaseEvent.getShopItem();
        PaymentType paymentType = purchaseEvent.getPaymentType();

        switch (paymentType) {
            case ROCKETR:
                if (item.rocketrLink == null || item.rocketrLink.isEmpty()) {
                    SafeToast.show(getActivity(), "Unknown RocketR link", Toast.LENGTH_LONG, true);
                    return;
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.rocketrLink));
                startActivity(browserIntent);
                break;
            default:
                throw new IllegalArgumentException("Couldn't find requested payment type: " + paymentType);
        }

        Answers.safeLogEvent(
                new CustomEvent("Shop Item Viewed")
                        .putCustomAttribute("Payment Type", paymentType.name())
                        .putCustomAttribute("Item Type", item.type)
                        .putCustomAttribute("Item Identifier", item.identifier)
                        .putCustomAttribute("Price", item.price)
        );
    }
}
