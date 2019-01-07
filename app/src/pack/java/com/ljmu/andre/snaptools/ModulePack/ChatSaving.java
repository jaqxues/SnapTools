package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_BODY_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_HEADER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.CHAT_METADATA_JSON_PARSER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_ISSAVED_INAPP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_MESSAGE_VIEW_MEASURE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_READ;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_METADATA_WRITE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_NOTIFICATION;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_SAVE_INAPP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.CHAT_SAVING_LINKER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.CHAT_SAVING_LINKER_CHAT_REF;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.NOTIFICATION_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCK_TYPING_NOTIFICATIONS;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SAVE_CHAT_IN_SC;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORE_CHAT_MESSAGES;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ChatSaving extends ModuleHelper {
	private static final int MAX_CHAT_MESSAGE_ENTRIES = 500;
	public static boolean bypassStealth;
	private CBITable<ChatObject> chatTable;
	private CBITable<ConversationObject> conversationTable;
	private String yourUsername;
	private HashSet<String> bypassStealthIds = new HashSet<>();

	public ChatSaving(String name, boolean canBeDisabled) {
		super(name, canBeDisabled);
	}

	@Override public FragmentHelper[] getUIFragments() {
		return new ChatManagerFragment[]{new ChatManagerFragment()};
	}

	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		findAndHookMethod(
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
		);

		if (getPref(BLOCK_TYPING_NOTIFICATIONS)) {
			hookMethod(
					CHAT_NOTIFICATION,
					new ST_MethodHook() {
						@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							Enum<?> notificationType = getObjectField(NOTIFICATION_TYPE, param.args[0]);

							Timber.d("Notification inbound: " + notificationType);

							if (notificationType.name().equals("TYPING"))
								param.setResult(null);
						}
					}
			);
		}

		/*hookAllMethods("iog$l", snapClassLoader, false, false);
		hookAllMethods("icj", snapClassLoader, false, false);
		//hookAllMethods("nst", snapClassLoader, false  , false);
		findAndHookMethod(
				"nsr", snapClassLoader,
				"logNetworkRequest", findClass("oqm", snapClassLoader), Map.class, Map.class,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {

						String url = (String) callMethod(param.thisObject, "getUrl");
						if (url.contains("update_stories_v2")) {
							Timber.d("Bypassed story update");
							param.setResult(null);
							return;
						}

						Timber.d("Url: " + url);
						Timber.d("NetworkRequest: " + param.args[0]);
						Timber.d("NetworkRequest2: " + param.args[1]);
					}
				}
		);
		findAndHookMethod(
				"nst", snapClassLoader,
				"executeSynchronously",
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						String url = (String) callMethod(param.thisObject, "getUrl");

						/*if (url.contains("update_snaps")) {
							Timber.d("Trying to update snap states: " + param.thisObject.getClass());

							logStackTrace();

							param.setThrowable(new NullPointerException("Cancelled request"));
							return;
						}*/
						/*if (url.contains("update_stories_v2") || /*url.contains("update_snaps") || url.contains("chat_typing")) {
							Timber.d("Exec story updat");
							param.setThrowable(new NullPointerException("Cancelled request"));
							return;
						}
						Timber.d("ExecAsyncUrl: " + url);
					}
				}
		);

		findAndHookMethod(
				"ctp", snapClassLoader,
				"getRequestPayload",
				new XC_MethodHook() {
					@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						Object ctn = XposedHelpers.getObjectField(param.thisObject, "b");
						Map<String, Object> snapMap = (Map<String, Object>) XposedHelpers.getObjectField(ctn, "a");

						for (Entry<String, Object> snapEntry : snapMap.entrySet()) {
							Timber.d("SnapEntry: " + snapEntry.toString());
						}

						for (String shouldntStealth : bypassStealthIds) {
							snapMap.remove(shouldntStealth);
						}

						bypassStealthIds.clear();
						Timber.d("EntrySize: " + ((Map<String, Object>) XposedHelpers.getObjectField(ctn, "a")).size());
					}
				}
		);

		try {
			Class<?> snapBaseClass = HookResolver.resolveHookClass(SNAP_BASE);
			Class statusEnumClass = findClass("jac$a", snapClassLoader);
			/*findAndHookMethod(
					"imh", snapClassLoader,
					"a", SQLiteDatabase.class, String.class, findClass("jac", snapClassLoader), findClass("jac$b", snapClassLoader),
					new XC_MethodHook() {
						@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							Object snapBase = param.args[2];

							if (!snapBase.getClass().equals(snapBaseClass)) {
								Timber.w("Database insertion not a received snap: " + snapBase);
								return;
							}

							Timber.d("Working on new received snap db entry");

							Enum<?> statusEnum = (Enum<?>) XposedHelpers.getObjectField(snapBase, "x");
							Timber.d("Received snap DB insertion: [Status: %s]", statusEnum.name());

							if(statusEnum.name().equals("RECEIVED_AND_VIEWED")) {
								XposedHelpers.setObjectField(snapBase, "x", Enum.valueOf(statusEnumClass, "UNVIEWED_AND_LOADED"));
							}
						}
					}
			);*/

			/*findAndHookMethod(
					"jac", snapClassLoader,
					"a", findClass("jac$a", snapClassLoader),
					new XC_MethodHook() {
						@Override protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							Enum<?> statusEnum = (Enum<?>) param.args[0];
							Timber.d("Tried to update status [%s] of [Snap: %s]", statusEnum, param.thisObject);

							if (!bypassStealth) {
								return;
							}

							if (statusEnum.name().equals("RECEIVED_AND_VIEWED") || statusEnum.name().equals("VIEWED_AND_REPLAY_AVAILABLE")) {
								param.args[0] = Enum.valueOf(statusEnumClass, "UNVIEWED_AND_LOADED");

								bypassStealthIds.add((String) callMethod(param.thisObject, "a"));
								bypassStealth = false;
							}
						}
					}
			);
		} catch (HookNotFoundException e) {
			Timber.e(e);
			moduleLoadState.fail();
		}

		findAndHookMethod(
				"iog$l", snapClassLoader,
				"a", findClass("mlv", snapClassLoader),
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						Timber.d("Updating snap state");
						logStackTrace();
					}
				}
		);

		findAndHookMethod(
				"icj", snapClassLoader,
				"a", findClass("iyh", snapClassLoader), findClass("raf", snapClassLoader),
				new XC_MethodReplacement() {
					@Override protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
						Timber.d("Bypassed network request");
						Timber.d("Param1: " + param.args[0]);
						Timber.d("Param2: " + param.args[1]);
						return null;
					}
				}
		);

		findAndHookMethod(
				"com.snapchat.android.app.feature.messaging.sccp.internal.main.SecureChatServiceImpl", snapClassLoader,
				"a", XposedHelpers.findClass("rmp", snapClassLoader),
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						Timber.d("ChatService: " + param.args[0]);
					}
				}
		);*/

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

				FieldMapper.initMapper(/*Chat*/ decryptMsg(new byte[]{-53, 23, 42, 20, -103, 5, -111, -84, 121, 78, 31, 29, 43, -105, 24, 72}),
						chatClass);
				FieldMapper.initMapper(/*ChatSuper*/ decryptMsg(new byte[]{40, -36, -17, -1, 69, 49, 13, -2, -12, -33, -107, 122, 97, 23, 125, -85}),
						chatSuperClass);
				FieldMapper.initMapper(/*Header*/ decryptMsg(new byte[]{36, 29, -34, 45, 50, -107, -113, 31, 71, 5, 66, 80, -111, 61, -72, -108}),
						headerClass);
				FieldMapper.initMapper(/*Body*/ decryptMsg(new byte[]{-95, -121, -59, 80, -75, 94, 106, -53, 75, -44, 66, -102, -110, -9, 123, 22}),
						bodyClass);

				// ===========================================================================

				//hookAllMethods("oet", snapClassLoader, false, false);
				hookMethod(
						CHAT_METADATA_READ,
						new ST_MethodHook() {
							@Override protected void after(MethodHookParam param) throws Throwable {
								try {
									Object chat = param.getResult();
									handleChatLogging(chat);
								} catch (Throwable t) {
									Timber.e(t);
								}
							}
						}
				);

				findAndHookMethod(
						HookResolver.resolveHookClass(CHAT_METADATA_JSON_PARSER),
						CHAT_METADATA_WRITE.getHookMethod(),
						CHAT_METADATA_WRITE.getHookParams()[0],
						chatClass.getInterfaces()[0],
						new ST_MethodHook() {
							@Override protected void before(MethodHookParam param) throws Throwable {
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
						@Override protected void after(MethodHookParam param) throws Throwable {
							ContextHelper.getActivity().runOnUiThread(new Runnable() {
								@Override public void run() {
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

	@Override
	public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {
		try {
			yourUsername = callStaticHook(GET_USERNAME);
		} catch (Throwable t) {
			Timber.e(t);
		}
	}

	private void handleChatLogging(Object chat) {
		if (yourUsername == null)
			return;
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
		}

		Timber.d("Created new chat object: " + newChatObject.toString());
	}
}
