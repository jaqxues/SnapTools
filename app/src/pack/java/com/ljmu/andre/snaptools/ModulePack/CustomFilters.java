package com.ljmu.andre.snaptools.ModulePack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.FiltersDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.FilterObject;
import com.ljmu.andre.snaptools.ModulePack.Fragments.FiltersManagerFragment;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.NowPlayingView;
import com.ljmu.andre.snaptools.ModulePack.Networking.Helpers.TrackAlbumArtManager;
import com.ljmu.andre.snaptools.ModulePack.Utils.TrackMetaData;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;
import com.ljmu.andre.snaptools.Utils.AnimationUtils;
import com.ljmu.andre.snaptools.Utils.Assert;
import com.ljmu.andre.snaptools.Utils.Constants;
import com.ljmu.andre.snaptools.Utils.ContextHelper;
import com.ljmu.andre.snaptools.Utils.CustomObservers.SimpleObserver;
import com.ljmu.andre.snaptools.Utils.ResourceUtils;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.GsonPreferences.Preferences.putPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.FILTER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CREATE_FILTER_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.CREATE_GEOFILTER_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.FILTER_LOAD_METADATA;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GEOFILTER_SHOULD_SUBSAMPLE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GEOFILTER_TAPPED;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.GET_GEOFILTER_CONTENT_VIEW;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.GEOFILTER_VIEW_CREATION_ARG3;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTERS_PATH;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FILTER_NOW_PLAYING_ENABLED;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.NOW_PLAYING_BOTTOM_MARGIN;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.NOW_PLAYING_IMAGE_SIZE;
import static com.ljmu.andre.snaptools.ModulePack.Utils.PackPreferenceHelpers.getFilterScaleType;
import static com.ljmu.andre.snaptools.Utils.ContextHelper.getModuleContext;
import static com.ljmu.andre.snaptools.Utils.FrameworkViewFactory.getSelectableBackgroundId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getId;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class CustomFilters extends ModuleHelper {
	private static final int BACKGROUND_FILTER_SAMPLE_SIZE = 10;
	private static final String FILTER_IMAGEVIEW_ID = "custom_filter_view";
	private static final String FILTER_FILE_ID = "custom_filter_path";
	private static final Object FILTER_LOCK = new Object();
	private String filtersPath;
	private Object duplicateInjectReference;
	private View nowPlayingView;
	private NowPlayingView playerViewProvider;
	private TrackMetaData trackMetaData;
	private ImageView nowPlayingSettingsView;
	private boolean isDraggingPlayer;

	public CustomFilters(String name, boolean canBeDisabled) {
		super(name, canBeDisabled);
	}

	@Override public FragmentHelper[] getUIFragments() {

		return new FragmentHelper[]{new FiltersManagerFragment()};
	}

	@Override
	public void loadHooks(ClassLoader snapClassLoader, Context snapContext) {
		FiltersDatabase.init(snapContext);
		filtersPath = getPref(FILTERS_PATH);

		CBITable<FilterObject> filterTable = FiltersDatabase.getTable(FilterObject.class);
		Collection<FilterObject> filterObjects = filterTable.getAll(new QueryBuilder()
				.addSelection("is_active", "1"));

		List<Object> filterMetaData = new ArrayList<>(filterObjects.size());

		try {
			Class filterMetadataClass = HookResolver.resolveHookClass(FILTER_METADATA);

			hookMethod(
					CREATE_FILTER_METADATA,
					new ST_MethodHook() {
						@Override protected void after(MethodHookParam param) throws Throwable {
							Object result = callMethod(param.getResult(), "d");
							Timber.d("MGR ITEM: " + result);

							synchronized (FILTER_LOCK) {
								if (result.getClass().equals(filterMetadataClass) && filterMetaData.isEmpty()) {
									int requiredMetaDataCount = filterObjects.size();

									if (getPref(FILTER_NOW_PLAYING_ENABLED))
										requiredMetaDataCount++;

									for (int i = 0; i < requiredMetaDataCount; i++) {
										Object firstObj = XposedHelpers.getObjectField(param.thisObject, "a");
										Object secondObj = XposedHelpers.getObjectField(firstObj, "a");

										Object mfxObj = newInstance(filterMetadataClass, secondObj);

										XposedHelpers.setObjectField(mfxObj, "a", String.valueOf(Math.random() * 1000000) + i);
										XposedHelpers.setObjectField(mfxObj, "f", true);
										filterMetaData.add(mfxObj);
									}
								}
							}
						}
					}
			);
		} catch (HookNotFoundException e) {
			Timber.e(e);
			moduleLoadState.fail();
		}

		hookMethod(
				GEOFILTER_SHOULD_SUBSAMPLE,
				new ST_MethodHook() {
					@SuppressWarnings("Guava") @Override protected void before(MethodHookParam param) throws Throwable {
						RelativeLayout geofilterLayout = (RelativeLayout) param.args[0];

						if (getAdditionalInstanceField(geofilterLayout, "is_now_playing") != null) {
							if (nowPlayingSettingsView == null)
								return;


							ContextHelper.getActivity().runOnUiThread(() -> {
								if ((boolean) param.args[1])
									AnimationUtils.collapse(nowPlayingSettingsView, 2);
								else
									AnimationUtils.expand(nowPlayingSettingsView, 2);
							});

							return;
						}

						String filterFilePath = (String) getAdditionalInstanceField(geofilterLayout, FILTER_FILE_ID);

						if (filterFilePath != null) {
							//Timber.d("Is custom filter: " + filterFilePath);
							//Timber.d("Is rendered: " + param.args[1]);

							ImageView filterImageView = getDSLView(geofilterLayout, FILTER_IMAGEVIEW_ID);
							File filterFile = new File(filterFilePath);

							if (filterImageView == null) {
								Timber.w("Couldn't find filter imageview for [Filter: %s]", filterFile.getName());
								return;
							}

							if (!filterFile.exists()) {
								Timber.w("Filter file doesn't exist? " + filterFilePath);
								return;
							}

							if ((boolean) param.args[1]) {
								Options bitmapFactoryOptions = new Options();
								bitmapFactoryOptions.inSampleSize = BACKGROUND_FILTER_SAMPLE_SIZE;
								Bitmap decodedBitmap = BitmapFactory.decodeFile(filterFilePath, bitmapFactoryOptions);

								ContextHelper.getActivity().runOnUiThread(() -> {
									Drawable viewDrawable = filterImageView.getDrawable();

									if (viewDrawable != null && viewDrawable instanceof BitmapDrawable) {
										Bitmap drawableBitmap = ((BitmapDrawable) viewDrawable).getBitmap();
										if (drawableBitmap != null)
											drawableBitmap.recycle();
									}

									filterImageView.setImageBitmap(decodedBitmap);
								});
							} else {
								Observable.fromCallable((Callable<Optional<Bitmap>>) () -> {
									try {
										return Optional.fromNullable(BitmapFactory.decodeFile(filterFilePath));
									} catch (Exception ignored) {
										Timber.w("Couldn't decode high resolution filter image: " + filterFile.getName());
									}

									return Optional.absent();
								}).subscribeOn(Schedulers.computation())
										.observeOn(AndroidSchedulers.mainThread())
										.subscribe(new SimpleObserver<Optional<Bitmap>>() {
											@Override public void onNext(Optional<Bitmap> bitmapOptional) {
												if (bitmapOptional.isPresent()) {
													Drawable viewDrawable = filterImageView.getDrawable();

													if (viewDrawable != null && viewDrawable instanceof BitmapDrawable) {
														Bitmap drawableBitmap = ((BitmapDrawable) viewDrawable).getBitmap();
														if (drawableBitmap != null)
															drawableBitmap.recycle();
													}

													filterImageView.setImageBitmap(bitmapOptional.get());
												}
											}
										});
							}
						}
					}
				}
		);

		hookMethod(
				FILTER_LOAD_METADATA,
				new ST_MethodHook() {
					@Override protected void after(MethodHookParam param) throws Throwable {
						synchronized (FILTER_LOCK) {
							Timber.d("Attempting to load filter views");
							List<Object> geoFilterList = (List<Object>) param.getResult();
							Object noIdeaWhatThisVarIs = getObjectField(GEOFILTER_VIEW_CREATION_ARG3, param.thisObject);

							if (noIdeaWhatThisVarIs == duplicateInjectReference) {
								Timber.i("Tried to re-inject filters into same list");
								return;
							}

							if (!filterObjects.isEmpty() && filterMetaData.isEmpty()) {
								Timber.w("Expected metadata for custom filters");
								return;
							}

							duplicateInjectReference = noIdeaWhatThisVarIs;

							Iterator<FilterObject> filterObjectIterator = filterObjects.iterator();
							int index = -1;
							while (filterObjectIterator.hasNext()) {
								index++;

								try {
									FilterObject filterObject = filterObjectIterator.next();
									if (filterObject == null) {
										Timber.w("Null FilterObject? The fuck's that about?");
										continue;
									}

									File imageFile = new File(filtersPath, filterObject.getFileName());

									if (!imageFile.exists()) {
										Timber.w("Filter Image doesn't exist? [Filter: %s]", filterObject.getFileName());
										filterObjectIterator.remove();
										filterTable.delete(filterObject);
										continue;
									}

									if (index >= filterMetaData.size()) {
										Timber.w(
												"Filter Index exceeded MetaData Size [Index: %s][MetaDataSize: %s][ActiveFilters: %s]",
												index, filterMetaData.size(), filterObjects.size()
										);
										continue;
									}

									//Timber.d("Index: " + index + " | " + filterMetaData.size());
									//Timber.d("Binding: " + XposedHelpers.getObjectField(filterMetaData.get(index), "a") + " TO " + filterObject.getFileName());
									Object geofilterViewHolder = callStaticHook(CREATE_GEOFILTER_VIEW, filterMetaData.get(index), param.args[1], noIdeaWhatThisVarIs);
									RelativeLayout geofilterView = callHook(GET_GEOFILTER_CONTENT_VIEW, geofilterViewHolder);
									geofilterView.removeAllViews();

									ImageView testView = new ImageView(ContextHelper.getActivity());
									testView.setId(getIdFromString(FILTER_IMAGEVIEW_ID));
									testView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
									testView.setScaleType(getFilterScaleType());
									geofilterView.addView(testView);

									setAdditionalInstanceField(geofilterView, FILTER_FILE_ID, imageFile.getAbsolutePath());
									geoFilterList.add(geofilterViewHolder);
								} catch (Throwable t) {
									Timber.e(t);
								}
							}

							if ((boolean) getPref(FILTER_NOW_PLAYING_ENABLED) && !filterMetaData.isEmpty()) {
								try {
									setupNowPlayingSettings(ContextHelper.getActivity());
								} catch (Throwable t) {
									Timber.e(t, "Couldn't create Filter Settings");
								}

								Timber.d("Adding Now PLaying Filter");

								Object geofilterViewHolder = callStaticHook(CREATE_GEOFILTER_VIEW, filterMetaData.get(filterMetaData.size() - 1), param.args[1], noIdeaWhatThisVarIs);
								RelativeLayout geofilterView = callHook(GET_GEOFILTER_CONTENT_VIEW, geofilterViewHolder);
								geofilterView.removeAllViews();
								View nowPlayingMainContainer = getPlayerViewProvider(ContextHelper.getActivity()).getCurrentPlayerView(ContextHelper.getActivity());

								nowPlayingView = getDSLView(nowPlayingMainContainer, "now_playing_container");
								updateNowPlaying(ContextHelper.getActivity());

								geofilterView.addView(nowPlayingMainContainer);
								setAdditionalInstanceField(geofilterView, "is_now_playing", true);
								geoFilterList.add(0, geofilterViewHolder);
							}
						}
					}
				});

		hookMethod(
				GEOFILTER_TAPPED,
				new XC_MethodHook() {
					@Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						if (getAdditionalInstanceField(param.thisObject, "is_now_playing") != null) {
							if (nowPlayingView == null || nowPlayingView.getVisibility() != View.VISIBLE) {
								param.setResult(false);
								return;
							}

							Rect rect = new Rect();
							if (!nowPlayingView.getGlobalVisibleRect(rect)) {
								param.setResult(false);
								return;
							}

							MotionEvent motionEvent = (MotionEvent) param.args[0];
							if (!rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
								param.setResult(false);
								return;
							}

							RelativeLayout geofilterView = (RelativeLayout) param.thisObject;
							geofilterView.removeAllViews();


							View nowPlayingMainContainer = getPlayerViewProvider(ContextHelper.getActivity()).getPlayerView(ContextHelper.getActivity(), true);

							nowPlayingView = getDSLView(nowPlayingMainContainer, "now_playing_container");
							updateNowPlaying(ContextHelper.getActivity());

							geofilterView.addView(nowPlayingMainContainer);
							Timber.d("Now playing tapped " + param.args[0]);
							param.setResult(true);
						}
					}
				}
		);
	}

	private void setupNowPlayingSettings(Activity snapActivity) {
		LinearLayout verticalButtonContainer = (LinearLayout) snapActivity.findViewById(getId(snapActivity, "vertical_button_container"));

		if (verticalButtonContainer == null) {
			Timber.w("VerticalButtonContainer not found, not assigning NowPlaying Settings button");
			return;
		}

		Context moduleContext = getModuleContext(snapActivity);
		Assert.notNull("Null Context", moduleContext);

		/**
		 * ===========================================================================
		 * Create the filter settings button
		 * ===========================================================================
		 */
		nowPlayingSettingsView = new ImageView(moduleContext);
		nowPlayingSettingsView.setBackgroundResource(getSelectableBackgroundId(snapActivity));
		nowPlayingSettingsView.setImageResource(getDrawable(moduleContext, "settings_96"));
		nowPlayingSettingsView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		nowPlayingSettingsView.setColorFilter(Color.WHITE);
		nowPlayingSettingsView.setScaleX(0.6f);
		nowPlayingSettingsView.setScaleY(0.6f);
		nowPlayingSettingsView.setVisibility(View.GONE);

		nowPlayingSettingsView.setOnClickListener((View v) -> buildPlayerSettingsView(snapActivity));
		verticalButtonContainer.addView(nowPlayingSettingsView);
	}

	@SuppressLint("ClickableViewAccessibility") private void buildPlayerSettingsView(Activity snapActivity) {
		if (nowPlayingView != null)
			nowPlayingView.setVisibility(View.GONE);

		NowPlayingView playerProvider = getPlayerViewProvider(snapActivity);
		ViewGroup playerView = getDSLView(playerProvider.getCurrentPlayerView(snapActivity), "now_playing_container");

		/**
		 * ===========================================================================
		 * Build the white overlay and add it to the preview frame
		 * ===========================================================================
		 */
		ViewGroup contentContainer = (ViewGroup) snapActivity.findViewById(getId(snapActivity, "snap_preview_frame_layout"));
		RelativeLayout overlay = playerProvider.getPlayerPositionController(snapActivity, (seekBar, progress) -> {
			View artView = getDSLView(playerView, "now_playing_art");
			ViewGroup.LayoutParams artParams = artView.getLayoutParams();
			artParams.width = progress;
			artParams.height = progress;
			artView.setLayoutParams(artParams);
		});
		overlay.setFocusableInTouchMode(true);
		overlay.requestFocus();
		contentContainer.addView(overlay);
		// ===========================================================================

		ViewGroup overlayPlayerContainer = getDSLView(overlay, "player_container");
		View bottomMarginView = getDSLView(overlay, "bottom_margin_line");

		// ===========================================================================
		playerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		((ViewGroup) playerView.getParent()).removeView(playerView);
		overlayPlayerContainer.addView(playerView);

		// ===========================================================================

		ImageButton cancelButton = getDSLView(overlay, "button_close_player_settings");
		cancelButton.setOnClickListener(v -> removeOverlayView(contentContainer, overlay));

		// ===========================================================================

		Handler touchHandler = new Handler(Looper.getMainLooper());

		/**
		 * ===========================================================================
		 * Setup our touch listener to move the player
		 * ===========================================================================
		 */
		//noinspection AndroidLintClickableViewAccessibility
		overlay.setOnTouchListener((v, event) -> {
			Timber.d("TouchEvent: " + event);

			/**
			 * ===========================================================================
			 * Check if our initial press is on the player view
			 * - Never return touch events to Snapchat
			 * ===========================================================================
			 */
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Rect rect = new Rect();
				if (!playerView.getGlobalVisibleRect(rect)) {
					return true;
				}

				if (!rect.contains((int) event.getX(), (int) event.getY())) {
					return true;
				}

				isDraggingPlayer = true;
				touchHandler.sendMessageDelayed(Message.obtain(touchHandler, 1), 250);
			}

			if (!isDraggingPlayer)
				return true;

			// ===========================================================================
			Display display = snapActivity.getWindowManager().getDefaultDisplay();
			playerView.measure(display.getWidth(), display.getHeight());
			// ===========================================================================

			int playerHeight = playerView.getMeasuredHeight(); //view height
			int parentHeight = overlay.getHeight(); //view height

			// ===========================================================================

			/**
			 * ===========================================================================
			 * Set the height of our "margin" line
			 * ===========================================================================
			 */
			int marginHeight = (int) (parentHeight - (event.getY() + (playerHeight / 2)));

			if (marginHeight > parentHeight - playerHeight)
				marginHeight = parentHeight - playerHeight;
			else if (marginHeight < 0)
				marginHeight = 0;

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomMarginView.getLayoutParams();
			layoutParams.height = marginHeight;
			bottomMarginView.setLayoutParams(layoutParams);
			// ===========================================================================

			if (event.getAction() == MotionEvent.ACTION_UP) {
				isDraggingPlayer = false;

				if (touchHandler.hasMessages(1)) {
					v.performClick();
					Timber.d("View %s got click");
				}

				putPref(NOW_PLAYING_BOTTOM_MARGIN, marginHeight);
				return true;
			}

			return true;
		});

		/**
		 * ===========================================================================
		 * Override the Back Button to close the overlay
		 * ===========================================================================
		 */
		overlay.setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (event.getAction() == MotionEvent.ACTION_UP)
					removeOverlayView(contentContainer, overlay);

				return true;
			}

			return false;
		});
	}

	private void removeOverlayView(ViewGroup contentContainer, View overlay) {
		refreshNowPlayingViewMetrics();
		contentContainer.removeView(overlay);

		if (nowPlayingView != null)
			nowPlayingView.setVisibility(View.VISIBLE);
	}

	private NowPlayingView getPlayerViewProvider(Activity activity) {
		if (playerViewProvider == null) {
			playerViewProvider = new NowPlayingView();
			initMediaReceiver(activity);
		}

		return playerViewProvider;
	}

	private void refreshNowPlayingViewMetrics() {
		if (nowPlayingView != null) {
			RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) nowPlayingView.getLayoutParams();
			playerParams.bottomMargin = getPref(NOW_PLAYING_BOTTOM_MARGIN);
			nowPlayingView.setLayoutParams(playerParams);

			int imageSize = getPref(NOW_PLAYING_IMAGE_SIZE);
			View artView = getDSLView(nowPlayingView, "now_playing_art");
			ViewGroup.LayoutParams artParams = artView.getLayoutParams();
			artParams.width = imageSize;
			artParams.height = imageSize;
			artView.setLayoutParams(artParams);
		}
	}

	private void updateNowPlaying(Context context) {
		if (nowPlayingView == null) {
			return;
		}

		String title = trackMetaData == null ? "Unknown" : trackMetaData.getTitle();
		String artist = trackMetaData == null ? "Unknown" : trackMetaData.getArtist();

		ResourceUtils.<TextView>getDSLView(nowPlayingView, "now_playing_title").setText(
				title
		);

		ResourceUtils.<TextView>getDSLView(nowPlayingView, "now_playing_artist").setText(
				artist
		);

		View artworkImageView = getDSLView(nowPlayingView, "now_playing_art");

		if (trackMetaData != null) {
			if (trackMetaData.getArtwork() != null && !trackMetaData.getArtwork().isRecycled()) {
				artworkImageView.setBackground(
						new BitmapDrawable(context.getResources(), trackMetaData.getArtwork())
				);
				return;
			}

			TrackAlbumArtManager.getAlbumArt(trackMetaData, new ObjectResultListener<Bitmap>() {
				@Override public void success(String message, Bitmap object) {
					trackMetaData.setArtwork(object);

					ResourceUtils.getDSLView(nowPlayingView, "now_playing_art").setBackground(
							new BitmapDrawable(context.getResources(), object)
					);
				}

				@Override public void error(String message, Throwable t, int errorCode) {
					if (t != null)
						Timber.e(t, message);
					else
						Timber.w(message);

					trackMetaData.setArtwork(null);
					artworkImageView.setBackgroundResource(
							Constants.getApkVersionCode() >= 65 ?
									getDrawable(getModuleContext(context), "music_record_primary_dark")
									: getDrawable(getModuleContext(context), "delete")
					);
				}
			});
		} else {
			artworkImageView.setBackgroundResource(
					Constants.getApkVersionCode() >= 65 ?
							getDrawable(getModuleContext(context), "music_record_primary_dark")
							: getDrawable(getModuleContext(context), "delete")
			);
		}
	}

	private void initMediaReceiver(Activity activity) {
		IntentFilter iF = new IntentFilter();

		iF.addAction("com.andrew.apollo.metachanged");

		iF.addAction("com.android.music.queuechanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.metachanged");
		//HTC Music
		iF.addAction("com.htc.music.playstatechanged");
		iF.addAction("com.htc.music.playbackcomplete");
		iF.addAction("com.htc.music.metachanged");
		//MIUI Player
		iF.addAction("com.miui.player.playstatechanged");
		iF.addAction("com.miui.player.playbackcomplete");
		iF.addAction("com.miui.player.metachanged");
		//Real
		iF.addAction("com.real.IMP.playstatechanged");
		iF.addAction("com.real.IMP.playbackcomplete");
		iF.addAction("com.real.IMP.metachanged");
		//SEMC Music Player
		iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
		iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
		iF.addAction("com.sonyericsson.music.TRACK_COMPLETED");
		iF.addAction("com.sonyericsson.music.metachanged");
		iF.addAction("com.sonyericsson.music.playbackcomplete");
		iF.addAction("com.sonyericsson.music.playstatechanged");
		//rdio
		iF.addAction("com.rdio.android.metachanged");
		iF.addAction("com.rdio.android.playstatechanged");
		//Samsung Music Player
		iF.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
		iF.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
		iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
		iF.addAction("com.sec.android.app.music.playstatechanged");
		iF.addAction("com.sec.android.app.music.playbackcomplete");
		iF.addAction("com.sec.android.app.music.metachanged");
		//Winamp
		iF.addAction("com.nullsoft.winamp.playstatechanged");
		iF.addAction("com.nullsoft.winamp.metachanged");
		//Amazon
		iF.addAction("com.amazon.mp3.playstatechanged");
		iF.addAction("com.amazon.mp3.metachanged");
		//Rhapsody
		iF.addAction("com.rhapsody.playstatechanged");
		//PowerAmp
		iF.addAction("com.maxmpz.audioplayer.playstatechanged");
		//Last.fm
		iF.addAction("fm.last.android.metachanged");
		iF.addAction("fm.last.android.playbackpaused");
		iF.addAction("fm.last.android.playbackcomplete");
		//A simple last.fm scrobbler
		iF.addAction("com.adam.aslfms.notify.playstatechanged");
		//Scrobble Droid
		iF.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");
		//Spotify
		iF.addAction("com.spotify.music.playbackstatechanged");
		//Poweramp
		iF.addAction("com.maxmpz.audioplayer.TRACK_CHANGED");

		Timber.d("Registering receiver");
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Timber.d("Received: " + intent.toString());
				Bundle bundle = intent.getExtras();

				if (bundle != null) {
					for (String key : bundle.keySet()) {
						Object value = bundle.get(key);

						if (value == null)
							value = "NULL";

						Timber.d("TrackEvent: " + String.format("%s %s (%s)", key,
								value.toString(), value.getClass().getName()));
					}
				}

				TrackMetaData receivedTrackMetaData;

				try {
					receivedTrackMetaData = TrackMetaData.Builder
							.fromIntent(intent)
							.build();
				} catch (NullPointerException e) {
					Timber.w("Track Broadcast not built: " + e.getMessage());
					return;
				}

				if (receivedTrackMetaData == null) {
					Timber.e("Null TrackMetaData received");
					return;
				}

				if (!receivedTrackMetaData.isPlaying()) {
					Timber.d("Track not playing... Skipping any updates");
					return;
				}

				if (trackMetaData == null || !trackMetaData.equals(receivedTrackMetaData)) {
					Timber.d("Current track differs with intent track");
					if (trackMetaData != null)
						trackMetaData.setArtwork(null);

					trackMetaData = receivedTrackMetaData;
					updateNowPlaying(activity);
				} else
					Timber.d("Intent track matches current track");
			}
		}, iF);
	}
}
