package com.ljmu.andre.snaptools.ModulePack;

import android.app.Activity;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.QueryBuilder;
import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Databases.LensDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensObject;
import com.ljmu.andre.snaptools.ModulePack.Fragments.LensSettingsFragment;
import com.ljmu.andre.snaptools.ModulePack.Utils.FieldMapper;
import com.ljmu.andre.snaptools.Utils.MapUtils;
import com.ljmu.andre.snaptools.Utils.MapUtils.KeyBinder;
import com.ljmu.andre.snaptools.Utils.XposedUtils.ST_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import java.util.*;

import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.*;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookDef.*;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.LENS_CATEGORY_MAP;
import static com.ljmu.andre.snaptools.ModulePack.HookResolver.resolveHookClass;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.LENS_AUTO_ENABLE;
import static com.ljmu.andre.snaptools.Utils.StringEncryptor.decryptMsg;

//import com.crashlytics.android.answers.Answers;
//import com.crashlytics.android.answers.CustomEvent;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
public class LensCollector extends ModuleHelper {
    private static final Object BUILD_LOCK = new Object();

    public LensCollector(String name, boolean canBeDisabled) {
        super(name, canBeDisabled);
    }

    // ===========================================================================

    @Override
    public FragmentHelper[] getUIFragments() {
        return new FragmentHelper[]{new LensSettingsFragment()};
    }

    // ===========================================================================

    @Override
    public void loadHooks(ClassLoader snapClassLoader, Activity snapActivity) {
        LensDatabase.init(snapActivity);

        /**
         * ===========================================================================
         * Bypass signature checks on the lenses
         * ===========================================================================
         */
        hookMethod(
                CHECK_LENS_AUTH,
                XC_MethodReplacement.returnConstant(true)
        );

        hookMethod(
                CHECK_LENS_CATEGORY_AUTH,
                XC_MethodReplacement.returnConstant(true)
        );

        hookMethod(
                CHECK_LENS_ASSET_AUTH,
                XC_MethodReplacement.returnConstant(true)
        );

        hookMethod(
                RESOLVE_LENS_CATEGORY,
                new ST_MethodHook() {
                    @Override
                    protected void after(MethodHookParam param) throws Throwable {
                        if (param.getResult() == null || param.getThrowable() != null) {
                            try {
                                Map<String, Object> categorymap = getObjectField(LENS_CATEGORY_MAP, param.thisObject);

                                Object category = categorymap.get("LENS_CATEGORY_GROUND");

                                if (category == null)
                                    category = categorymap.get("LENS_CATEGORY_SKY");

                                if (category == null)
                                    category = categorymap.get("LENS_CATEGORY_SELFIE");

                                param.setResult(category);

                            } catch (Throwable e) {
                                Timber.e(e);
                            }
                        }
                    }
                }
        );

		/*try {
			Class<?> assetClass = resolveHookClass(LENS_ASSET);
			FieldMapper.initMapper("Asset", assetClass);
		} catch (HookNotFoundException e) {
			Timber.e(e);
			moduleLoadState.fail();
		}*/


        /**Image Quality Improvements*/
		/*XposedHelpers.findAndHookMethod(
				"com.snapchat.android.app.shared.util.SnapMediaUtils", snapClassLoader,
				"a", Bitmap.class, int.class, new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						Timber.d("Default Compression: " + param.args[1]);
						Bitmap bitmap = (Bitmap) param.args[0];
						param.args[1] = 100;

						Timber.d("Bitmap: [Bytes: %s][Config: %s][Density: %s][Height: %s][Width: %s]", bitmap.getByteCount(), bitmap.getConfig(), bitmap.getDensity(),
								bitmap.getHeight(), bitmap.getWidth());

						logStackTrace();
					}
				});

		XposedHelpers.findAndHookMethod(
				"mlc", snapClassLoader,
				"a", Bitmap.class,
				new ST_MethodHook() {
					@Override protected void before(MethodHookParam param) throws Throwable {
						Timber.i("SNap Image Set");
						Bitmap bitmap = (Bitmap) param.args[0];
						if (bitmap != null) {
							Timber.d("Bitmap: [Bytes: %s][Config: %s][Density: %s][Height: %s][Width: %s]", bitmap.getByteCount(), bitmap.getConfig(), bitmap.getDensity(),
									bitmap.getHeight(), bitmap.getWidth());

							param.args[0] = getResizedBitmap(bitmap, 1960, 4032);
						}

						logStackTrace();
					}
				});*/

        try {
            Class<?> lensClass = resolveHookClass(LENS);
            Class<?> slugClass = resolveHookClass(LENS_SLUG);
            Class<?> trackClass = resolveHookClass(LENS_TRACK);
            Class<?> contextClass = resolveHookClass(LENS_CONTEXT_HOLDER);
            CBITable<LensObject> lensTable = LensDatabase.getTable(LensObject.class);

            hookMethod(
                    LENS_LOADING,
                    new ST_MethodHook() {
                        @Override
                        protected void before(MethodHookParam param) throws Throwable {
                            synchronized (BUILD_LOCK) {
                                try {
                                    @SuppressWarnings("unchecked")
                                    List<Object> lensList = (List<Object>) param.args[0];

                                    if (lensList == null || lensList.isEmpty())
                                        return;

                                    Collection<LensObject> storedLensList = lensTable.getAll(
                                            new QueryBuilder()
                                                    .addSelection("isActive", "1")
                                    );

                                    @SuppressWarnings("Convert2Lambda")
                                    Map<String, LensObject> lensDbMap = MapUtils.convertList(
                                            storedLensList,
                                            new KeyBinder<String, LensObject>() {
                                                @Override
                                                public String getKey(LensObject mapEntry) {
                                                    return mapEntry.id;
                                                }
                                            }
                                    );

                                    FieldMapper lensMapper = FieldMapper.initMapper("Lens", lensClass);

                                    /**
                                     * ===========================================================================
                                     * Build field maps for static classes with unchanged content
                                     * ===========================================================================
                                     */
                                    /** ========================================================================== **/
                                    FieldMapper.initMapper("Slug", slugClass);
                                    // ===========================================================================
                                    FieldMapper.initMapper("Track", trackClass);
                                    // ===========================================================================
                                    FieldMapper.initMapper("Context", contextClass);
                                    /** ========================================================================== **/

                                    boolean enableNewLenses = getPref(LENS_AUTO_ENABLE);

                                    for (Object lens : lensList) {
                                        try {
                                            Timber.d("Working on lens: " + lens);

                                            String idFieldName = lensMapper.get("id");
                                            String lensId = (String) XposedHelpers.getObjectField(lens, idFieldName);

											/*if (lensTable.contains(lensId)) {
												Timber.i("Lens %s already exists", lensId);
												lensDbMap.remove(lensId);
												continue;
											}*/
                                            lensDbMap.remove(lensId);

                                            LensObject newDbLens = new LensObject();
                                            newDbLens.buildFromFieldMap(lensMapper, lens);
                                            newDbLens.isActive = enableNewLenses;

                                            if (!newDbLens.isReady()) {
                                                Timber.w("Lens not ready to save: " + newDbLens);
                                                lensDbMap.remove(lensId);
                                                continue;
                                            }

                                            if (!lensTable.insert(newDbLens))
                                                Timber.w("Failed to insert lens into database: " + newDbLens);
                                        } catch (Throwable t) {
                                            Timber.w(t, "Failed to build lens: " + lens);
                                        }
                                    }

                                    Timber.d("Inserting table into list");

                                    List<Object> convertedLenses = convertLensObjects(
                                            snapClassLoader,
                                            lensMapper,
                                            lensDbMap.values()
                                    );

                                    if (convertedLenses != null && convertedLenses.size() > 0) {
                                        lensList.addAll(convertedLenses);
                                        Timber.d(/*Inserted %s lenses*/ decryptMsg(new byte[]{61, -26, -106, 29, -103, 95, 84, 117, 2, -57, -67, -41, 90, 116, -128, 120, 120, -104, 37, 120, 89, -13, -57, 108, -56, 81, 29, 110, -97, -94, -45, 86}), convertedLenses.size());
                                    }

                                    cleanEmptyLenses(lensMapper, lensList);

                                    FieldMapper.removeMapper("Lens");
                                    FieldMapper.removeMapper("Slug");
                                    FieldMapper.removeMapper("Track");
                                    Timber.d("Cleared %s cached objects", LensObject.destroyDataCache());
                                } catch (Throwable t) {
                                    Timber.e(t, "Unknown error handling lens system");
                                }
                            }
                        }
                    }
            );
        } catch (Throwable e) {
            Timber.e(e);
            moduleLoadState.fail();
        }
    }

    private List<Object> convertLensObjects(ClassLoader snapClassLoader, FieldMapper lensMapper,
                                            Collection<LensObject> lensObjects) {
        try {
            FieldMapper slugMapper = FieldMapper.getMapper("Slug");
            Object slugPos = XposedHelpers.newInstance(slugMapper.getLinkClass());
            slugMapper.setField(slugPos, "alignment", "right");
            slugMapper.setField(slugPos, "position", "BOTTOM_RIGHT");
            slugMapper.setField(slugPos, "text", "SPONSORED");
            slugMapper.setField(slugPos, "time_before_fadeout", 3500);

            FieldMapper trackMapper = FieldMapper.getMapper("Track");
            Object trackInfo = XposedHelpers.newInstance(trackMapper.getLinkClass());
            trackMapper.setField(trackInfo, "skip_track", false);


            List<Object> convertedLenses = new ArrayList<>(lensObjects.size());
            Map<String, String> lensMapDowncast = lensMapper;

            for (LensObject lensObject : lensObjects) {
                if (!lensObject.isReady())
                    continue;

                //Timber.d("Processing: " + lensObject);
                Object convertedLens;

                try {
                    convertedLens = XposedHelpers.newInstance(lensMapper.getLinkClass());
                } catch (Throwable t) {
                    Timber.e(t);
                    continue;
                }

                for (String fieldTag : lensMapDowncast.keySet()) {
                    Object storedVal = lensObject.getFieldByTag(fieldTag);

                    if (storedVal != null)
                        lensMapper.setField(convertedLens, fieldTag, storedVal);
					/*else {
						if (fieldTag.equals("id") || fieldTag.equals("code")
								|| fieldTag.equals("icon_link") || fieldTag.equals("mLensLink")) {
							Answers.safeLogEvent(
									new CustomEvent("NullLensEntry")
											.putCustomAttribute("Field", fieldTag)
							);
						}
					}*/
                }

                lensMapper.setField(convertedLens, "mSponsoredSlugPosAndText", slugPos);
                lensMapper.setField(convertedLens, "unlockable_track_info", trackInfo);

                if (isConvertedLensCompleted(lensMapper, convertedLens))
                    convertedLenses.add(convertedLens);
				/*else {
					Answers.safeLogEvent(
							new CustomEvent("LensCrashMitigated")
					);
				}*/
            }

            Timber.d(/*Converted %s lenses*/ decryptMsg(new byte[]{33, -71, 97, 48, 39, 103, 125, -107, -75, 72, -72, 115, 70, 95, -6, -115, -110, -110, 28, -39, 118, 24, 98, 59, 59, -124, -120, 100, 112, -14, 75, -102}), convertedLenses.size());
            return convertedLenses;
        } catch (Throwable t) {
            Timber.e(t);
        }

        return Collections.emptyList();
    }

    private void cleanEmptyLenses(FieldMapper lensMapper, List<Object> convertedLensList) {
        Iterator<Object> convertedLensIterator = convertedLensList.iterator();

        int emptyLensCount = 0;

        while (convertedLensIterator.hasNext()) {
            Object convertedLens = convertedLensIterator.next();

            if (convertedLens == null || !isConvertedLensCompleted(lensMapper, convertedLens)) {
                convertedLensIterator.remove();
                emptyLensCount++;
            }
        }

//		if (emptyLensCount > 0) {
//			Answers.safeLogEvent(
//					new CustomEvent("EmptyLensReport")
//							.putCustomAttribute("Count", emptyLensCount)
//			);
//		}
    }

    public boolean isConvertedLensCompleted(FieldMapper lensMapper, Object convertedLens) {
        return lensMapper.getFieldVal(convertedLens, "id") != null
                && lensMapper.getFieldVal(convertedLens, "code") != null
                && lensMapper.getFieldVal(convertedLens, "icon_link") != null
                && lensMapper.getFieldVal(convertedLens, "mLensLink") != null;
    }
}
