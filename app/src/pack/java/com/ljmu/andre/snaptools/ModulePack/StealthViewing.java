package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.StealthViewProvider;
import com.ljmu.andre.snaptools.ModulePack.Fragments.StealthViewingFragment;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.SNAP_STATUS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CHAT_V3_FRAGMENT_CREATED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CONSTRUCTOR_OPERA_PAGE_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CREATE_PROFILE_SETTINGS_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.DISPATCH_CHAT_UPDATE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_RECEIVED_SNAP_PAYLOAD;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_SNAP_ID;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_STORY_SNAP_PAYLOAD;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.MARK_CHAT_VIEWED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.MARK_STORY_VIEWED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.NETWORK_EXECUTE_SYNC;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.OPENED_SNAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.SET_SNAP_STATUS;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_DISPLAYED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_BUILDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_GET_OBJECT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.STORY_METADATA_INSERT_OBJECT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.RECEIVED_SNAP_PAYLOAD_HOLDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.RECEIVED_SNAP_PAYLOAD_MAP;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.SNAPCHAT_FRAGMENT_CONTENT_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_ADVANCER_DISPLAY_STATE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_ADVANCER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_UPDATE_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_UPDATE_METADATA_ID;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_UPDATE_METADATA_LIST;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.DEFAULT_CHAT_STEALTH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.DEFAULT_SNAP_STEALTH;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StealthViewing extends ModuleHelper {
	private static final int MAX_STEALTH_BYPASS_SIZE = 50;
	public static boolean bypassNextStealthView;
	private List<FrameLayout> activeLayouts = new ArrayList<>();
	private Set<String> stealthySnapSet = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
		protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
			return size() > 100;
		}
	});

	private Set<String> stealthBypassSet = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
		protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
			return size() > MAX_STEALTH_BYPASS_SIZE;
		}
	});
	private StealthViewProvider viewProvider = new StealthViewProvider();

	public StealthViewing(String name, boolean canBeDisabled) {
		super(name, canBeDisabled);
	}

	@Override public FragmentHelper[] getUIFragments() {
		return new FragmentHelper[]{
				new StealthViewingFragment()
		};
	}

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);

		hookConstructor(
				CONSTRUCTOR_OPERA_PAGE_VIEW,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						Timber.d("Opera view created.... Assigning active view");
						activeLayouts.add((FrameLayout) param.thisObject);
					}
				}
		);

		hookMethod(
				OPENED_SNAP,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						Timber.d("Direct snap displayed... Binding stealth to active layout");

						if (Looper.myLooper() != Looper.getMainLooper()) {
                            new Handler(Looper.getMainLooper()).post(() -> assignStealthToActiveLayout(ContextHelper.getActivity()));
						} else
                            assignStealthToActiveLayout(ContextHelper.getActivity());
					}
				}
		);

		if (!Saving.hasLoadedHooks) {
			/**
			 * ===========================================================================
			 * Force Stories to contain their matched MetaData
			 * ===========================================================================
			 */
			hookMethod(
					STORY_METADATA_BUILDER,
					new ST_MethodHook() {
						@Override protected void after(MethodHookParam param) throws Throwable {
							Object storyMetadata = param.getResult();
							callHook(STORY_METADATA_INSERT_OBJECT, storyMetadata, "STORY_REPLY_SNAP", param.args[0]);
						}
					}
			);
		}

		/**
		 * ===========================================================================
		 * Story displayed hook
		 * ===========================================================================
		 */
		hookMethod(
				STORY_DISPLAYED,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						Object displayState = getObjectField(STORY_ADVANCER_DISPLAY_STATE, param.thisObject);
						if (displayState == null || !displayState.toString().equals("FULLY_DISPLAYED"))
							return;

						Object snapMetaData = getObjectField(STORY_ADVANCER_METADATA, param.thisObject);

						Object storySnap = callHook(STORY_METADATA_GET_OBJECT, snapMetaData, "STORY_REPLY_SNAP");

						if (storySnap == null) {
							Timber.d("StorySnap null: Probably not a normal story");
							return;
						}

						Timber.d("Story snap displayed... Binding stealth to active layout");

						if (Looper.myLooper() != Looper.getMainLooper()) {
                            new Handler(Looper.getMainLooper()).post(() -> assignStealthToActiveLayout(ContextHelper.getActivity()));
						} else
                            assignStealthToActiveLayout(ContextHelper.getActivity());
					}
				}
		);

		/**
		 * ===========================================================================
		 * Snap status updater
		 * ===========================================================================
		 */
		try {
			Class statusEnumClass = HookResolver.resolveHookClass(SNAP_STATUS);
			Class storySnapClass = HookResolver.resolveHookClass(STORY_SNAP);

			hookMethod(
					SET_SNAP_STATUS,
					new ST_MethodHook() {
						@Override protected void before(MethodHookParam param) throws Throwable {
							String snapId = callHook(GET_SNAP_ID, param.thisObject);

							Enum<?> statusEnum = (Enum<?>) param.args[0];
							Timber.d("Tried to update status [%s] of [Snap: %s]", statusEnum, param.thisObject);

							if (statusEnum.name().equals("RECEIVED_AND_VIEWED") || statusEnum.name().equals("VIEWED_AND_REPLAY_AVAILABLE")) {
								Timber.d("ClassType: " + param.thisObject.getClass().getSimpleName());

								if (param.thisObject.getClass().equals(storySnapClass)) {
									return;
								}

								if (handleStealthCheck(snapId))
									param.args[0] = Enum.valueOf(statusEnumClass, "UNVIEWED_AND_LOADED");
							}
						}
					}
			);
		} catch (HookNotFoundException e) {
			Timber.e(e);
			moduleLoadState.fail();
		}

		/**
		 * ===========================================================================
		 * Story status updater
		 * ===========================================================================
		 */
		hookMethod(
				MARK_STORY_VIEWED,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						String snapId = callHook(GET_SNAP_ID, param.args[0]);

						if (handleStealthCheck(snapId))
							param.setResult(null);
					}
				}
		);

		/**
		 * ===========================================================================
		 * Received snap update payload provider
		 * ===========================================================================
		 */
		hookMethod(
				GET_RECEIVED_SNAP_PAYLOAD,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						Object snapPayloadHolder = getObjectField(RECEIVED_SNAP_PAYLOAD_HOLDER, param.thisObject);
						Map<String, Object> snapMap = getObjectField(RECEIVED_SNAP_PAYLOAD_MAP, snapPayloadHolder);

						for (String shouldntStealth : stealthySnapSet) {
							snapMap.remove(shouldntStealth);
						}

						Timber.d("EntrySize: " + snapMap.size());
					}
				}
		);

		/**
		 * ===========================================================================
		 * Story snap update payload provider
		 * ===========================================================================
		 */
		hookMethod(
				GET_STORY_SNAP_PAYLOAD,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						List<Object> storyUpdateList = getObjectField(STORY_UPDATE_METADATA_LIST, param.thisObject);

						int storyUpdateListSize = storyUpdateList.size();

						Iterator<Object> storyIterator = storyUpdateList.iterator();

						while (storyIterator.hasNext()) {
							Object storyMetaHolder = storyIterator.next();
							Object storyMetaData = getObjectField(STORY_UPDATE_METADATA, storyMetaHolder);
							String storyId = getObjectField(STORY_UPDATE_METADATA_ID, storyMetaData);

							if (stealthySnapSet.contains(storyId))
								storyIterator.remove();
						}

						stealthySnapSet.clear();

						Timber.d("Stripped %s stories from update list", (storyUpdateListSize - storyUpdateList.size()));
					}
				}
		);

		/**
		 * ===========================================================================
		 * General network manager hook
		 * ===========================================================================
		 */
		hookMethod(
				NETWORK_EXECUTE_SYNC,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						if (!(boolean) getPref(DEFAULT_CHAT_STEALTH))
							return;

						String url = (String) callMethod(param.thisObject, "getUrl");

						if (url.endsWith("chat_typing")) {
							param.setResult(null);
							return;
						}

						Timber.d("ExecAsyncUrl: " + url);
					}
				}
		);

		/**
		 * ===========================================================================
		 * Chat network update hook
		 * ===========================================================================
		 */
		hookMethod(
				DISPATCH_CHAT_UPDATE,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						if (getPref(DEFAULT_CHAT_STEALTH)) {
							Timber.d("Bypassed chat connection");
							Timber.d("Param1: " + param.args[0]);
							Timber.d("Param2: " + param.args[1]);

							param.setResult(null);
						}
					}
				}
		);

		/**
		 * ===========================================================================
		 * Chat Message hasBeenRead hook
		 * ===========================================================================
		 */
		hookMethod(
				MARK_CHAT_VIEWED,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						if (getPref(DEFAULT_CHAT_STEALTH))
							param.setResult(null);
					}
				}
		);

		/**
		 * ===========================================================================
		 * Chat Header Button hook
		 * ===========================================================================
		 */
		hookMethod(
				CHAT_V3_FRAGMENT_CREATED,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						View contentView = getObjectField(SNAPCHAT_FRAGMENT_CONTENT_VIEW, param.thisObject);
                        RelativeLayout headerTitle = getView(contentView, getId(ContextHelper.getActivity(), "chat_header_title"));
                        headerTitle.addView(viewProvider.getStealthChatButton(ContextHelper.getActivity(), getModuleContext(ContextHelper.getActivity())));
					}
				});

		/**
		 * ===========================================================================
		 * Profile Navigation Buttons hook
		 * ===========================================================================
		 */
		hookMethod(
				CREATE_PROFILE_SETTINGS_VIEW,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						ViewGroup contentView = (ViewGroup) param.getResult();
                        LinearLayout buttonsLayout = getView(contentView, getId(ContextHelper.getActivity(), "profile_navigation_buttons"));
                        buttonsLayout.addView(viewProvider.getProfileContainer(ContextHelper.getActivity(), getModuleContext(ContextHelper.getActivity())));
					}
				});
	}

	private void assignStealthToActiveLayout(Activity snapActivity) {
		Iterator<FrameLayout> activeLayoutIterator = activeLayouts.iterator();

		while (activeLayoutIterator.hasNext()) {
			FrameLayout activeLayout = activeLayoutIterator.next();

			Timber.d("Testing layout: " + activeLayout);
			Timber.d("WindowVis: " + (int) activeLayout.getWindowVisibility());

			if (activeLayout.getWindowVisibility() == View.GONE) {
				Timber.d("Active layout is dead... removing from list");
				activeLayoutIterator.remove();
			} else {
				if (activeLayout.findViewById(getIdFromString("stealth_layout")) != null) {
					Timber.i("Active layout already has stealth layout... skipping");
					continue;
				}

				activeLayout.addView(viewProvider.getStealthSnapLayout(getModuleContext(snapActivity)));
			}
		}
	}

	private boolean handleStealthCheck(String snapId) {
		Timber.d("BypassActive: " + bypassNextStealthView);

		if (bypassNextStealthView) {
			Timber.d("Not using stealth for snap");
			bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);
			stealthBypassSet.add(snapId);
			stealthySnapSet.remove(snapId);
			return false;
		}

		if (stealthBypassSet.contains(snapId)) {
			Timber.d("Snap has already been marked as NON STEALTH");
			return false;
		}

		stealthySnapSet.add(snapId);
		bypassNextStealthView = !(boolean) getPref(DEFAULT_SNAP_STEALTH);
		return true;
	}
}
