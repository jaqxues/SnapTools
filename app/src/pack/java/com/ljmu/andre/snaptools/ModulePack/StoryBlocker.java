package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodReplacement;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_FRIEND_RECENT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_FRIEND_VIEWED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.STORY_SPONSORED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FRIEND_PROFILE_POPUP_CREATED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FRIEND_STORY_TILE_USERNAME;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_INITIAL_STORIES;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_NEW_STORY;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_STORIES;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.LOAD_STORY_SNAP_ADVERT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.STORY_COLLECTION_MAP;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BLOCKED_STORIES;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_ADVERTS_BLOCKED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_DISCOVER_BLOCKED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.STORY_BLOCKER_SHOW_BUTTON;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.detach;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ViewFactory.dp;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.collectionContains;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getColor;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static de.robv.android.xposed.XposedHelpers.callMethod;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class StoryBlocker extends ModuleHelper {

    private Button blockerButton;

    public StoryBlocker(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{new StoryBlockingSettingsFragment()};
    }

    // ===========================================================================

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
        boolean blockDiscovery = getPref(STORY_BLOCKER_DISCOVER_BLOCKED);

        if (getPref(STORY_BLOCKER_ADVERTS_BLOCKED)) {
            hookMethod(
                    LOAD_STORY_SNAP_ADVERT,
                    XC_MethodReplacement.DO_NOTHING
            );
        }

        if (getPref(STORY_BLOCKER_SHOW_BUTTON)) {

            hookMethod(
                    FRIEND_PROFILE_POPUP_CREATED,
                    new ST_MethodHook() {
                        @Override
                        protected void after(MethodHookParam param) throws Throwable {
                            Bundle arguments = (Bundle) callMethod(param.thisObject, "getArguments");
                            String username = arguments.getString("FRIEND_MINI_PROFILE_USERNAME");

                            if (username == null) {
                                Timber.e(new Throwable("Empty StoryTile Username"));
                                return;
                            }

                            username = username.toLowerCase();

                            boolean isUserBlocked = collectionContains(BLOCKED_STORIES, username);

                            RelativeLayout relativeView = getView((View) param.args[0], "mini_profile_view");

                            relativeView.addView(detach(blockerButton));

                            updateBlockerButtonState(ContextHelper.getModuleContext(snapContext), blockerButton, isUserBlocked);

                            String finalUsername = username;
                            blockerButton.setOnClickListener(v -> {
                                boolean isUserBlocked1 = collectionContains(BLOCKED_STORIES, finalUsername);

                                if (isUserBlocked1) {
                                    PreferenceHelpers.removeFromCollection(BLOCKED_STORIES, finalUsername);
                                } else {
                                    PreferenceHelpers.addToCollection(BLOCKED_STORIES, finalUsername);
                                }

                                updateBlockerButtonState(ContextHelper.getModuleContext(snapContext), blockerButton, !isUserBlocked1);

                                SafeToastAdapter.showDefaultToast(
                                        ContextHelper.getActivity(),
                                        "Restart Snapchat for the changes to take affect"
                                );
                            });

                            AnimationUtils.expand(blockerButton, 4);
                        }
                    }
            );
        }

//		if (blockDiscovery) {
//			ST_MethodHook hooker = new ST_MethodHook() {
//				@Override protected void after(MethodHookParam param) throws Throwable {
//					if (!(boolean) XposedHelpers.callMethod(param.thisObject, STORY_DATA_IS_SUBSCRIBED.getVarName()))
//						param.setThrowable(new NullPointerException());
//				}
//			};
//
//			hookAllConstructors(
//					STORY_DATA_DISCOVER,
//					hooker
//			);
//			hookAllConstructors(
//					STORY_DATA_DYNAMIC,
//					hooker
//			);
//			hookAllConstructors(
//					STORY_DATA_MAP,
//					hooker
//			);
//			hookAllConstructors(
//					STORY_DATA_PROMOTED,
//					hooker
//			);
//			hookAllConstructors(
//					STORY_DATA_MOMENT,
//					hooker
//			);
//		}

        hookMethod(
                LOAD_INITIAL_STORIES,
                new ST_MethodHook() {
                    @Override
                    protected void before(MethodHookParam param) throws Throwable {
                        Map<Object, Object> map = (Map) param.args[3];
                        Map<Object, Object> map2 = (Map) param.args[4];

                        HashSet<String> blockedStories = getPref(BLOCKED_STORIES);

                        for (String blockedUser : blockedStories) {
                            map.remove(blockedUser);
                            map2.remove(blockedUser);
                        }
                    }
                }
        );

        hookMethod(
                LOAD_NEW_STORY,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        Map<Object, Object> map = getObjectField(STORY_COLLECTION_MAP, param.thisObject);

                        if (map.isEmpty())
                            return;

                        HashSet<String> blockedStories = getPref(BLOCKED_STORIES);

                        for (String blockedUser : blockedStories) {
                            map.remove(blockedUser);
                        }
                    }
                }
        );

        try {
            Class<?> sponsoredStoryClass = HookResolver.resolveHookClass(STORY_SPONSORED);
            Class<?> recentStoryClass = HookResolver.resolveHookClass(STORY_FRIEND_RECENT);
            Class<?> friendStoryClass = HookResolver.resolveHookClass(STORY_FRIEND_VIEWED);

            hookMethod(
                    LOAD_STORIES,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            try {
                                List<?> originalList = (List<?>) param.args[0];
                                List<Object> iterativeList = new ArrayList<>(originalList);

                                for (Object storyItemObject : iterativeList) {
                                    Class<?> storyItemClass = storyItemObject.getClass();

                                    Timber.d("StoryItem: " + storyItemClass);

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
            Timber.e(e, "Failed loading story blockers");
            moduleLoadState.fail();
        }
    }

    private void updateBlockerButtonState(Context modContext, Button button, boolean isUserBlocked) {
        if (isUserBlocked) {
            button.setBackgroundResource(getDrawable(modContext, "neutral_button"));
            button.setTextColor(ContextCompat.getColor(modContext, getColor(modContext, "primaryLight")));
            button.setText("Unblock Stories");
        } else {
            button.setBackgroundResource(getDrawable(modContext, "error_button"));
            button.setTextColor(ContextCompat.getColor(modContext, getColor(modContext, "errorLight")));
            button.setText("Block Stories");
        }

        AnimationUtils.scaleUp(button);
    }

    @Override
    public void prepareActivity(ClassLoader snapClassLoader, Activity snapActivity) {
        if (getPref(STORY_BLOCKER_SHOW_BUTTON)) {
            Context modContext = getModuleContext(snapActivity);
            int horizontalPadding = dp(20, snapActivity);
            int verticalPadding = dp(7, snapActivity);

            blockerButton = new Button(modContext);
            blockerButton.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

            RelativeLayout.LayoutParams buttonparams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            buttonparams.bottomMargin = dp(20, snapActivity);
            blockerButton.setLayoutParams(buttonparams);
        }
    }
}
