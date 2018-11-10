package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;

import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.ChatDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ChatObject;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.ConversationObject;
import com.ljmu.andre.snaptools.ModulePack.Fragments.ChatManagerFragment;
import com.ljmu.andre.snaptools.ModulePack.Utils.FieldMapper;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_BODY_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_HEADER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_ISSAVED_INAPP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_MESSAGE_VIEW_MEASURE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_READ;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_READ_SECOND;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_WRITE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_WRITE_SECOND;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_NOTIFICATION;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_SAVE_INAPP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.CHAT_SAVING_LINKER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.CHAT_SAVING_LINKER_CHAT_REF;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.NOTIFICATION_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCK_TYPING_NOTIFICATIONS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_CHAT_IN_SC;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORE_CHAT_MESSAGES;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatSaving extends ModuleHelper {
    private static final int MAX_CHAT_MESSAGE_ENTRIES = 500;
    private CBITable<ChatObject> chatTable;
    private CBITable<ConversationObject> conversationTable;
    private String yourUsername;

    public ChatSaving(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    @Override
    public FragmentHelper[] getUIFragments() {
        return new ChatManagerFragment[]{new ChatManagerFragment()};
    }

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		/*findAndHookMethod(
				"ify", snapClassLoader,
				"a", findClass("com.snapchat.android.core.structure.fragment.SnapchatFragment", snapClassLoader),
				new ST_MethodHook() {
					@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						Object snapchatFragment = param.args[0];
						Timber.d("SnapchatFragment: " + snapchatFragment.getClass() + " | " + snapchatFragment);
					}
				}
		);

		findAndHookMethod(
				"com.snapchat.android.app.feature.messaging.chat.fragment.ChatV3Fragment", snapClassLoader,
				"onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						View buttonContainer = (View) param.getResult();


						TableLayout menuTableLayout = getView(buttonContainer, "chat_menu_table");
					}
				}
		);*/

        if (getPref(BLOCK_TYPING_NOTIFICATIONS)) {
            hookMethod(
                    CHAT_NOTIFICATION,
                    new ST_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Enum<?> notificationType = getObjectField(NOTIFICATION_TYPE, param.args[0]);
                            String name = notificationType.name();

                            Timber.d("Notification inbound: " + notificationType);

                            if (name.contains("TYPING"))
                                param.setResult(null);
                        }
                    }
            );
        }

        if (getPref(STORE_CHAT_MESSAGES)) {
            try {
                ChatDatabase.init(snapContext);

                chatTable = ChatDatabase.getTable(ChatObject.class);
                conversationTable = ChatDatabase.getTable(ConversationObject.class);

                // ===========================================================================

                Class chatClass = HookResolver.resolveHookClass(CHAT_METADATA);
                Class chatSuperClass = chatClass.getSuperclass();
                Class headerClass = HookResolver.resolveHookClass(CHAT_HEADER_METADATA);
                Class bodyClass = HookResolver.resolveHookClass(CHAT_BODY_METADATA);

                // ===========================================================================

                FieldMapper.initMapper("Chat",
                        chatClass);
                FieldMapper.initMapper("ChatSuper",
                        chatSuperClass);
                FieldMapper.initMapper("Header",
                        headerClass);
                FieldMapper.initMapper("Body",
                        bodyClass);

                // ===========================================================================

                //hookAllMethods("oet", snapClassLoader, false, false);
                hookMethod(
                        CHAT_METADATA_READ,
                        new ST_MethodHook() {
                            @Override
                            protected void after(MethodHookParam param) throws Throwable {
                                try {
                                    Object chat = param.getResult();
                                    handleChatLogging(chat);
                                } catch (Throwable t) {
                                    Timber.e(t);
                                }
                            }
                        }
                );

                hookMethod(
                        CHAT_METADATA_WRITE,
                        new ST_MethodHook() {
                            @Override
                            protected void before(MethodHookParam param) throws Throwable {
                                try {
                                    Object chat = param.args[1];
                                    handleChatLogging(chat);
                                } catch (Throwable t) {
                                    Timber.e(t);
                                }
                            }
                        });

                hookMethod(
                        CHAT_METADATA_READ_SECOND,
                        new ST_MethodHook() {
                            @Override
                            protected void after(MethodHookParam param) throws Throwable {
                                try {
                                    Object chat = param.getResult();
                                    handleChatLogging(chat);
                                } catch (Throwable t) {
                                    Timber.e(t);
                                }
                            }
                        }
                );

                hookMethod(
                        CHAT_METADATA_WRITE_SECOND,
                        new ST_MethodHook() {
                            @Override
                            protected void before(MethodHookParam param) throws Throwable {
                                try {
                                    Object chat = param.args[1];
                                    handleChatLogging(chat);
                                } catch (Throwable t) {
                                    Timber.e(t);
                                }
                            }
                        });
            } catch (HookNotFoundException e) {
                Timber.e(e);
                moduleLoadState.fail();
            } catch (Throwable t) {
                Timber.e(t, "Unknown error");
            }
        }

        Set<Integer> intArray = Collections.newSetFromMap(new LinkedHashMap<Integer, Boolean>() {
            protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> eldest) {
                return size() > MAX_CHAT_MESSAGE_ENTRIES;
            }
        });

        if (getPref(SAVE_CHAT_IN_SC)) {
            hookMethod(
                    CHAT_MESSAGE_VIEW_MEASURE,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            ContextHelper.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Object chatLinker = getObjectField(CHAT_SAVING_LINKER, param.thisObject);

                                        if (chatLinker == null) {
                                            Timber.w("Null Chat Linker");
                                            return;
                                        }

                                        Object chat = getObjectField(CHAT_SAVING_LINKER_CHAT_REF, chatLinker);

                                        if (chat == null) {
                                            Timber.w("Null Chat Object");
                                            return;
                                        }

                                        Boolean isSaved = callHook(CHAT_ISSAVED_INAPP, chat);
                                        int hashCode = chat.hashCode();

                                        if (!isSaved) {
                                            synchronized (intArray) {
                                                if (intArray.contains(hashCode)) {
                                                    return;
                                                }
                                            }

                                            callHook(CHAT_SAVE_INAPP, param.thisObject);
                                        } else {
                                            synchronized (intArray) {
                                                intArray.add(hashCode);
                                            }
                                        }

                                    } catch (Throwable t) {
                                        Timber.w(t);
                                    }
                                }
                            });
                        }
                    });
        }
    }

    private void handleChatLogging(Object chat) {
        FieldMapper chatMapper = FieldMapper.getMapper("Chat");
        FieldMapper chatSuperMapper = FieldMapper.getMapper("ChatSuper");
        FieldMapper headerMapper = FieldMapper.getMapper("Header");
        FieldMapper bodyMapper = FieldMapper.getMapper("Body");

        String messageId = chatMapper.getFieldVal(chat, "chat_message_id");

        if (chatTable.contains(messageId))
            return;

        ChatObject newChatObject = new ChatObject();

        // Set Body Text =============================================================
        Object body = chatMapper.getFieldVal(chat, "body");
        newChatObject.text = bodyMapper.getFieldVal(body, "text");

        String type = bodyMapper.getFieldVal(body, "type");

        if (!type.equals("text")) {
            if (newChatObject.text == null)
                newChatObject.text = "<" + type + ">";
            else
                newChatObject.text = "<" + type + ">\n" + newChatObject.text;
        }

        if (newChatObject.text == null) {
            Timber.w("Null text for chat... Probably not a message");
            return;
        }

        // Set Message ID ============================================================
        newChatObject.chat_message_id = messageId;
        // Set Timestamp ===========================================
        newChatObject.timestamp = chatSuperMapper.getFieldVal(chat, "timestamp");
        // Set Sender/Receiver =======================================================
        Object header = chatSuperMapper.getFieldVal(chat, "header");
        newChatObject.from = headerMapper.getFieldVal(header, "from");
        newChatObject.to = headerMapper.getFieldVal(header, "to");
        newChatObject.conv_id = headerMapper.getFieldVal(header, "conv_id");
        newChatObject.sentByYou = yourUsername.equals(newChatObject.from);
        // ===========================================================================

        if (!newChatObject.isCompleted()) {
            Timber.w("Chat Object not completed!");
            return;
        }

        if (chatTable.insert(newChatObject)) {
            ConversationObject conversation = conversationTable.getFirst("conversation_id", newChatObject.conv_id);

            if (conversation == null)
                conversation = new ConversationObject();

            conversation.setUsers(newChatObject.from, newChatObject.to);
            conversation.timestamp = newChatObject.timestamp;
            conversation.conv_id = newChatObject.conv_id;
            conversation.yourUsername = yourUsername;
            conversationTable.insert(conversation);
        } else
            Timber.w("Chat object not inserted");

        Timber.d("Created new chat object: " + newChatObject.toString());
    }

    @Override
    public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {
        // Giving time Snapchat time to initialize
        try {
            yourUsername = callStaticHook(GET_USERNAME);
        } catch (Exception e) {
            Timber.e(e, "Unable to get UserName");
            moduleLoadState.fail();
        }
    }
}
