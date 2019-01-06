package com.ljmu.andre.snaptools.ModulePack;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.StoryBlockingSettingsFragment;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.PreferenceHelpers;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodReplacement;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_FRIEND_RECENT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_FRIEND_VIEWED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SPONSORED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FRIEND_PROFILE_POPUP_CREATED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FRIEND_STORY_TILE_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_STORIES;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_STORY_SNAP_ADVERT;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCKED_STORIES;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_ADVERTS_BLOCKED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_DISCOVER_BLOCKED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.dp;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;
import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StoryBlocker extends ModuleHelper {
	public StoryBlocker(String name, boolean canBeDisabled) {
		super(name, canBeDisabled);
	}

	// ===========================================================================

	@Override public FragmentHelper[] getUIFragments() {
		return new FragmentHelper[]{new StoryBlockingSettingsFragment()};
	}

	// ===========================================================================

	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		if (getPref(STORY_BLOCKER_ADVERTS_BLOCKED)) {
			hookMethod(
					LOAD_STORY_SNAP_ADVERT,
					XC_MethodReplacement.DO_NOTHING
			);
		}

		hookMethod(
				FRIEND_PROFILE_POPUP_CREATED,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						Bundle arguments = (Bundle) callMethod(param.thisObject, /*getArguments*/ decryptMsg(new byte[]{-31, 92, 100, -24, -82, -102, 81, 66, 47, 37, -69, 58, -121, -9, 74, 69}));
						String username = arguments.getString(/*FRIEND_MINI_PROFILE_USERNAME*/ decryptMsg(new byte[]{-118, -114, -111, 122, -96, 122, 104, 20, -37, -7, -35, -14, -68, 78, -18, 37, 100, 94, -37, -43, 73, 101, -123, 127, -40, 7, -123, -87, 121, -92, -101, 81}));

						if (username == null) {
							Timber.e(new Throwable(/*Empty StoryTile Username*/ decryptMsg(new byte[]{-43, 46, -27, -67, -69, -81, -51, 100, 77, 42, -24, -13, 39, -39, -1, 13, -59, -126, 71, 3, 24, -33, -128, -96, 90, -77, -32, -80, -24, -126, -112, 1})));
							return;
						}

						username = username.toLowerCase();

						boolean isUserBlocked = collectionContains(BLOCKED_STORIES, username);

						RelativeLayout relativeView = getView((View) param.args[0], /*mini_profile_view*/ decryptMsg(new byte[]{125, 29, 88, -75, -43, 105, -44, 84, 68, 80, -113, -47, -65, -26, -44, -31, -12, 33, -64, 126, 27, 24, 73, 88, -27, 44, 75, 23, -48, 46, -115, 103}));
						Context modContext = getModuleContext(snapContext);

						int horizontalPadding = dp(20, snapContext);
						int verticalPadding = dp(7, snapContext);

						Button blockerButton = new Button(modContext);
						blockerButton.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

						RelativeLayout.LayoutParams buttonparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						buttonparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
						buttonparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
						buttonparams.bottomMargin = dp(20, snapContext);
						blockerButton.setLayoutParams(buttonparams);
						relativeView.addView(blockerButton);

						updateBlockerButtonState(modContext, blockerButton, isUserBlocked);

						String finalUsername = username;
						blockerButton.setOnClickListener(v -> {
							boolean isUserBlocked1 = collectionContains(BLOCKED_STORIES, finalUsername);

							if (isUserBlocked1) {
								PreferenceHelpers.removeFromCollection(BLOCKED_STORIES, finalUsername);
							} else {
								PreferenceHelpers.addToCollection(BLOCKED_STORIES, finalUsername);
							}

							updateBlockerButtonState(modContext, blockerButton, !isUserBlocked1);

							SafeToastAdapter.showDefaultToast(
									ContextHelper.getActivity(),
									/*Refresh Stories for changes to take effect*/ decryptMsg(new byte[]{-90, -20, -81, -104, 111, -94, 125, 127, -109, -32, 85, 83, -78, -59, 60, 105, -102, -14, 115, 44, 9, 44, -44, 122, -115, 3, -110, -122, 98, 39, 40, 56, -79, -113, -50, -123, 62, 122, -62, -89, -21, -64, 68, -68, 85, -2, -120, 34})
							);
						});

						AnimationUtils.expand(blockerButton, 4);
					}
				}
		);

		try {
			Class<?> sponsoredStoryClass = HookResolver.resolveHookClass(STORY_SPONSORED);
			Class<?> recentStoryClass = HookResolver.resolveHookClass(STORY_FRIEND_RECENT);
			Class<?> friendStoryClass = HookResolver.resolveHookClass(STORY_FRIEND_VIEWED);
			boolean blockDiscovery = getPref(STORY_BLOCKER_DISCOVER_BLOCKED);

			hookMethod(
					LOAD_STORIES,
					new ST_MethodHook() {
						@Override protected void before(MethodHookParam param) throws Throwable {
							try {
								List<?> originalList = (List<?>) param.args[0];
								List<Object> iterativeList = new ArrayList<>(originalList);

								for (Object storyItemObject : iterativeList) {
									Class<?> storyItemClass = storyItemObject.getClass();

									if (blockDiscovery && storyItemClass.equals(sponsoredStoryClass)) {
										originalList.remove(storyItemObject);
									} else if (storyItemClass.equals(friendStoryClass) || storyItemClass.equals(recentStoryClass)) {
										try {
											String username = callHook(FRIEND_STORY_TILE_USERNAME, storyItemObject);
											if (username != null && !username.isEmpty() && collectionContains(BLOCKED_STORIES, username.toLowerCase()))
												originalList.remove(storyItemObject);
										} catch (Throwable t) {
											Timber.e(t);
										}
									}
								}
							} catch (UnsupportedOperationException ignored) {

							}
						}
					}
			);
		} catch (HookNotFoundException e) {
			Timber.e(e, /*Failed loading story blockers*/ decryptMsg(new byte[]{11, 6, 65, 21, -76, -106, 65, 33, -4, -89, -80, 103, -98, -17, -37, 53, -90, -93, 111, 45, 88, 57, -62, -72, -34, 32, 3, -57, 105, 100, -35, 37}));
			moduleLoadState.fail();
		}
	}

	private void updateBlockerButtonState(Context modContext, Button button, boolean isUserBlocked) {
		if (isUserBlocked) {
			button.setBackgroundResource(getDrawable(modContext, /*neutral_button*/ decryptMsg(new byte[]{24, 84, -4, 80, 25, -75, 48, 57, 31, 74, 44, -99, -69, 88, -100, 48})));
			button.setTextColor(ContextCompat.getColor(modContext, getColor(modContext, /*primaryLight*/ decryptMsg(new byte[]{40, 119, -28, 37, -45, 55, -47, -6, -22, 53, -74, -109, -103, -61, -36, 24}))));
			button.setText(/*Unblock Stories*/ decryptMsg(new byte[]{-81, 125, 66, 44, 113, -91, 65, 21, 49, -110, 57, 23, -105, -123, 87, -28}));
		} else {
			button.setBackgroundResource(getDrawable(modContext, /*error_button*/ decryptMsg(new byte[]{49, -61, -114, -56, 56, 71, 89, -6, 83, -77, -65, -107, -117, -78, 48, 97})));
			button.setTextColor(ContextCompat.getColor(modContext, getColor(modContext, /*errorLight*/ decryptMsg(new byte[]{-94, -96, 80, 124, 105, 88, 97, 77, 44, 121, 77, 22, -35, -19, 27, 82}))));
			button.setText(/*Block Stories*/ decryptMsg(new byte[]{-41, -24, 81, -100, 62, -116, 106, -25, 70, -124, -50, 89, 65, -87, 72, 2}));
		}

		AnimationUtils.scaleUp(button);
	}
}
