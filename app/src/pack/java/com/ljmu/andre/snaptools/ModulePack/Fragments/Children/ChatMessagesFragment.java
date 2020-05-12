package com.ljmu.andre.snaptools.ModulePack.Fragments.Children;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.Dialogs.Content.Options.OptionsButtonData;
import com.ljmu.andre.snaptools.Dialogs.DialogFactory;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog;
import com.ljmu.andre.snaptools.Dialogs.ThemedDialog.ThemedClickListener;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.ChatDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ChatObject;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ConversationObject;
import com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.EditTextListener;
import com.ljmu.andre.snaptools.R;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.ResourceMapper;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getLayout;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatMessagesFragment extends FragmentHelper {
    private final Object MESSAGE_LOCK = new Object();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ConversationObject conversationObject;
    private CBITable<ChatObject> chatTable;
    private List<MessageItem> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageQuickAdapter adapter;
    private List<Integer> colourPool = new ArrayList<>();
    private Map<String, Integer> assignedUserColourMap = new HashMap<>();
    private int assignedUserColours = 0;

    public ChatMessagesFragment() {
        assignRedirector();
    }

    private void assignRedirector() {
        if (Constants.getApkVersionCode() < 69)
            return;

        Timber.d("Assigning Redirector");

        redirector = (id, defaultVal, params) -> {
            switch (id) {
                case "back_press":
                    return onBackPressed();
                default:
                    return defaultVal;
            }
        };

        Timber.d("Redirector: " + getRedirector());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ChatDatabase.init(getActivity());
        chatTable = ChatDatabase.getTable(ChatObject.class);

        LinearLayout layoutContainer = new LinearLayout(getContext());
        layoutContainer.setOrientation(LinearLayout.VERTICAL);
        layoutContainer.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        );

        initHeader(layoutContainer, inflater);
        initMessageView(layoutContainer, inflater);

        initColourPool();

        return layoutContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        generateChatData();
    }

    @Override
    public void onPause() {
        super.onPause();
        synchronized (MESSAGE_LOCK) {
            messages.clear();
        }
    }

    private void initHeader(LinearLayout layoutContainer, LayoutInflater inflater) {
        View chatHeader = inflater.inflate(
                getLayout(getActivity(), "item_chat_header"),
                layoutContainer,
                true
        );

        View btnContainer = ResourceUtils.getView(chatHeader, "button_container");
        btnContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        TextView txtHeader = ResourceUtils.getView(chatHeader, "txt_header");
        txtHeader.setText(conversationObject.getConversationName());

        EditText txtSearch = ResourceUtils.getView(chatHeader, "txt_search");
        txtSearch.setFocusableInTouchMode(true);
        txtSearch.addTextChangedListener(new EditTextListener() {
            @Override
            protected void textChanged(@Nullable EditText source, Editable editable) {
                Observable.fromCallable(() -> {
                    String searchText = editable.toString();
                    int index = getIndexForMessage(searchText);
                    Timber.d("Index: " + index);
                    Timber.d("RecyclerViewCount: " + recyclerView.getChildCount());
                    return index;
                }).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new SimpleObserver<Integer>() {
                            @Override
                            public void onNext(Integer index) {

                                if (index > -1)
                                    recyclerView.scrollToPosition(index);

                            }
                        });
            }
        });

        ImageButton btnSearch = ResourceUtils.getView(chatHeader, "btn_search");
        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearch.getVisibility() == View.VISIBLE) {
                    AnimationUtils.collapse(txtSearch, true, 1);
                    AnimationUtils.expand(txtHeader, true, 1.75f);
                    txtSearch.clearFocus();
                    txtSearch.setSelected(false);
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(txtSearch.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                } else {
                    AnimationUtils.expand(txtSearch, true, 1.75f);
                    AnimationUtils.collapse(txtHeader, true, 1);
                    txtSearch.setSelected(true);
                    txtSearch.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }

    private void initMessageView(LinearLayout layoutContainer, LayoutInflater inflater) {
        swipeRefreshLayout = new SwipeRefreshLayout(getActivity());
        swipeRefreshLayout.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                generateChatData();
            }
        });

        inflater.inflate(getLayout(getActivity(), "recyclerview"), swipeRefreshLayout, true);

        recyclerView = ResourceUtils.getView(swipeRefreshLayout, "recyclerview");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        synchronized (MESSAGE_LOCK) {
            adapter = new MessageQuickAdapter(messages);
            adapter.bindToRecyclerView(recyclerView);
            adapter.setEmptyView(R.layout.layout_empty_chats);
        }

        layoutContainer.addView(swipeRefreshLayout);
    }

    private void initColourPool() {
        int arrayId = ResourceMapper.getResId(getActivity(), "mdcolor_300", "array");
        if (arrayId == 0) {
            Timber.e("Unknown resource mdcolor_200");
            return;
        }

        TypedArray colours = getResources().obtainTypedArray(arrayId);
        for (int i = 0; i < colours.length(); i++) {
            int colour = colours.getColor(
                    i,
                    ContextCompat.getColor(
                            getActivity(),
                            getColor(getActivity(), "primary")
                    )
            );

            Timber.d("Adding colour: " + colour);
            colourPool.add(colour);
        }
        colours.recycle();
    }

    private int getIndexForMessage(String messageRegex) {
        int index = -1;
        synchronized (MESSAGE_LOCK) {
            for (MessageItem conversationItem : messages) {
                index++;
                if (!(conversationItem instanceof ChatMessageItem))
                    continue;

                ChatMessageItem messageItem = (ChatMessageItem) conversationItem;

                if (messageItem.chatObject.text.toLowerCase().contains(messageRegex.toLowerCase()))
                    return index;
            }
        }

        return -1;
    }

    private void generateChatData() {
        swipeRefreshLayout.setRefreshing(true);

        Observable.fromCallable(new Callable<Collection<MessageItem>>() {
            private ChatObject lastChat;
            private ChatHeaderItem lastHeader;
            private String lastDate;

            @Override
            public Collection<MessageItem> call() throws Exception {
                Collection<ChatObject> chatObjects = chatTable.getAll(
                        new QueryBuilder()
                                .addSort("timestamp", "DESC")
                                .addSelection("conversation_id", conversationObject.conv_id)
                );

                if (chatObjects.isEmpty())
                    return Collections.emptyList();

                List<MessageItem> messageItems = new ArrayList<>();

                Iterator<ChatObject> chatIterator = chatObjects.iterator();

                while (chatIterator.hasNext()) {
                    ChatObject chatObject = chatIterator.next();
                    String displayDate = (String) DateUtils.getRelativeTimeSpanString(
                            chatObject.timestamp,
                            System.currentTimeMillis(),
                            DateUtils.DAY_IN_MILLIS
                    );

                    if ((lastChat != null && !lastChat.from.equals(chatObject.from)) ||
                            (lastDate != null && !lastDate.equals(displayDate)))
                        messageItems.add(generateHeaderItem(lastChat, lastDate));

                    // Assign colour and add user message ========================================
                    assignColourToUser(chatObject.from, chatObject.sentByYou);
                    messageItems.add(new ChatMessageItem(chatObject));

                    // Calculate whether to add header/username ==================================

                    if (!chatIterator.hasNext())
                        messageItems.add(generateHeaderItem(chatObject, displayDate));

                    lastChat = chatObject;
                    lastDate = displayDate;
                }

                return messageItems;
            }

            private ChatHeaderItem generateHeaderItem(ChatObject currentChat, String lastDate) {
                if (lastHeader != null && lastHeader.date != null &&
                        lastHeader.date.equals(lastDate))
                    lastHeader.date = null;

                lastHeader = new ChatHeaderItem(currentChat.from, lastDate);
                return lastHeader;
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Collection<MessageItem>>() {
                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(@NonNull Collection<MessageItem> chatObjects) {
                        setMessages(chatObjects);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void assignColourToUser(String from, boolean sentByYou) {
        if (assignedUserColourMap.get(from) == null) {
            int colour;
            if (sentByYou)
                colour = ContextCompat.getColor(getActivity(), getColor(getActivity(), "red_300"));
            else {
                int colourIndex = assignedUserColours++ % (colourPool.size() - 1);
                colour = colourPool.get(colourIndex);
            }

            Timber.d("Using colour: " + colour);
            assignedUserColourMap.put(from, colour);
        }
    }

    public void setMessages(Collection<MessageItem> chatObjects) {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.

                if (Constants.getApkVersionCode() >= 66)
                    AnimationUtils.sequentGroup(getActivity(), recyclerView, 100, 3, 0);

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        synchronized (MESSAGE_LOCK) {
            messages.clear();
            messages.addAll(chatObjects);
            //Collections.sort(chatMessages);
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public ChatMessagesFragment setConversationObject(ConversationObject conversationObject) {
        this.conversationObject = conversationObject;
        return this;
    }

    private void deleteMessage(ChatObject chatObject) {
        DialogFactory.createConfirmation(
                getActivity(),
                "Delete Message?",
                "Are you sure you want to delete this message?"
                        + "\n\n\"" + chatObject.text + "\"",
                new ThemedClickListener() {
                    @Override
                    public void clicked(ThemedDialog themedDialog) {
                        chatTable.delete(chatObject);
                        themedDialog.dismiss();
                        generateChatData();
                    }
                }
        ).show();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Integer getMenuId() {
        return null;
    }

    public boolean onBackPressed() {
        if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return true;
        }

        return false;
    }

    public class MessageQuickAdapter extends BaseMultiItemQuickAdapter<MessageItem, BaseViewHolder> {
        /**
         * Same as QuickAdapter#QuickAdapter(Context,int) but with
         * some initialization data.
         *
         * @param data A new list is created out of this one to avoid mutable list
         */
        public MessageQuickAdapter(List<MessageItem> data) {
            super(data);
            addItemType(ChatHeaderItem.TYPE, getLayout(getActivity(), "item_chat_message_header"));
            addItemType(ChatMessageItem.TYPE, getLayout(getActivity(), "item_chat_message"));
        }

        @Override
        protected void convert(BaseViewHolder helper, MessageItem item) {
            item.setupView(helper);
        }
    }

    public abstract class MessageItem implements MultiItemEntity {
        public abstract void setupView(BaseViewHolder holder);
    }

    public class ChatHeaderItem extends MessageItem {
        public static final int TYPE = 0;

        private String sender;
        @Nullable
        private String date;

        public ChatHeaderItem(String sender, @Nullable String date) {
            this.sender = sender;
            this.date = date;
        }

        @Override
        public int getItemType() {
            return TYPE;
        }

        @Override
        public void setupView(BaseViewHolder holder) {
            TextView txtUsername = ResourceUtils.getView(holder.itemView, "txt_username");
            txtUsername.setText(sender.toUpperCase());
            txtUsername.setTextColor(assignedUserColourMap.get(sender));

            TextView txtDate = ResourceUtils.getView(holder.itemView, "txt_datetime");
            if (date == null)
                txtDate.setVisibility(View.GONE);
            else {
                txtDate.setVisibility(View.VISIBLE);
                txtDate.setText(date);
            }
        }
    }

    public class ChatMessageItem extends MessageItem {
        private static final int TYPE = 1;

        private ChatObject chatObject;

        public ChatMessageItem(ChatObject chatObject) {
            this.chatObject = chatObject;
        }

        @Override
        public int getItemType() {
            return TYPE;
        }

        @Override
        public void setupView(BaseViewHolder holder) {
            TextView txtMessage = ResourceUtils.getView(holder.itemView, "txt_message");
            txtMessage.setText(chatObject.text);

            View sidebar = ResourceUtils.getView(holder.itemView, "sidebar");
            sidebar.setBackgroundColor(assignedUserColourMap.get(chatObject.from));

            TextView txtTime = ResourceUtils.getView(holder.itemView, "txt_time");
            txtTime.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtTime.getVisibility() == View.VISIBLE)
                        AnimationUtils.collapse(txtTime, 2);
                    else {
                        txtTime.setText(
                                DateUtils.getRelativeDateTimeString(
                                        getActivity(),
                                        chatObject.timestamp,
                                        DateUtils.SECOND_IN_MILLIS,
                                        DateUtils.WEEK_IN_MILLIS,
                                        DateUtils.FORMAT_ABBREV_RELATIVE
                                )
                        );
                        AnimationUtils.expand(txtTime, 2);
                    }
                }
            });


            holder.itemView.setOnLongClickListener(v -> {
                DialogFactory.createOptions(
                        getActivity(),
                        "Message",
                        new OptionsButtonData(
                                "Copy Message",
                                new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        clipboard.setPrimaryClip(ClipData.newPlainText("text label", chatObject.text));
                                        Toast.makeText(getContext(), "Copied text", Toast.LENGTH_SHORT).show();
                                        themedDialog.dismiss();
                                    }
                                }
                        ), new OptionsButtonData(
                                "Delete Message",
                                new ThemedClickListener() {
                                    @Override
                                    public void clicked(ThemedDialog themedDialog) {
                                        deleteMessage(chatObject);
                                        themedDialog.dismiss();
                                    }
                                }
                        )
                ).show();
                return false;
            });
        }
    }
}
