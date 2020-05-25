package com.ljmu.andre.snaptools.ModulePack.Databases.Tables;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.ljmu.andre.CBIDatabase.Annotations.PrimaryKey;
import com.ljmu.andre.CBIDatabase.Annotations.TableField;
import com.ljmu.andre.CBIDatabase.Annotations.TableName;
import com.ljmu.andre.CBIDatabase.CBIDatabaseCore;
import com.ljmu.andre.CBIDatabase.CBIObject;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.CBIDatabase.Utils.SQLCommand;
import com.ljmu.andre.snaptools.EventBus.EventBus;
import com.ljmu.andre.snaptools.Exceptions.HookNotFoundException;
import com.ljmu.andre.snaptools.ModulePack.Databases.LensDatabase;
import com.ljmu.andre.snaptools.ModulePack.Events.LensEventRequest;
import com.ljmu.andre.snaptools.ModulePack.Events.LensEventRequest.LensEvent;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef;
import com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef;
import com.ljmu.andre.snaptools.ModulePack.HookResolver;
import com.ljmu.andre.snaptools.ModulePack.Utils.FieldMapper;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter;
import com.ljmu.andre.snaptools.UIComponents.Adapters.ExpandableItemAdapter.ExpandableItemEntity;
import com.ljmu.andre.snaptools.Utils.GlideApp;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;
import timber.log.Timber;

import static com.jaqxues.akrolyb.prefs.PrefManagerKt.getPref;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENUM_LENS_ACTIVATOR_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.ENUM_LENS_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_APPLICATION_CONTEXT_ENUM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_ASSET_BUILT;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_ASSET_LOAD_MODE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_ASSET_TYPE;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_CAMERA_CONTEXT_ENUM;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookClassDef.LENS_CONTEXT_HOLDER;
import static com.ljmu.andre.snaptools.ModulePack.HookDefinitions.HookVariableDef.LENS_ACTIVATOR;
import static com.ljmu.andre.snaptools.ModulePack.HookResolver.resolveHookClass;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.SHOW_LENS_NAMES;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDrawable;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getView;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

@SuppressWarnings("WeakerAccess")
@TableName(value = "Lenses", VERSION = 8)
public class LensObject extends ExpandableItemEntity implements CBIObject {
    public static final int type = 1;
    private static final Gson gson = new Gson();
    private static final Map<String, Object> dataCache = new HashMap<>();

    private static final Map<String, ValueAdapter> adapterMap = new ImmutableMap.Builder<String, ValueAdapter>()
            .put("mType", new LensTypeAdapter())
            .put("categories", new CategoryAdapter())
            .put("mAssetsManifestList", new AssetAdapter())
            .put("lensContext", new LensContextAdapter())
            .build();


    @PrimaryKey
    @TableField(value = "id")
    public String id;
    @TableField(value = "isActive")
    public boolean isActive;
    @TableField(value = "code")
    public String code;
    @TableField(value = "mActivationCamera")
    public String mActivationCamera;
    @TableField(value = "mIsFeatured")
    public boolean mIsFeatured;
    @TableField(value = "mType")
    public String mType;
    @TableField(value = "icon_link", NOT_NULL = true)
    public String icon_link;
    @TableField(value = "hint_id")
    public String hint_id;
    @TableField(value = "categories")
    public String categories;
    @TableField(value = "mLensLink", NOT_NULL = true)
    public String mLensLink;
    @TableField(value = "mAbsoluteCarouselPosition")
    public int mAbsoluteCarouselPosition;
    @TableField(value = "mIsSponsored")
    public boolean mIsSponsored;
    @TableField(value = "mIsThirdParty")
    public boolean mIsThirdParty;
    @TableField(value = "mSignature")
    public String mSignature;

    /*@TableField(value = "mSponsoredSlugPosAndText")
    public pik mSponsoredSlugPosAndText;*/
    @TableField(value = "mReleaseDate")
    public String mReleaseDate;
    @TableField(value = "mIndexInDataSource")
    public int mIndexInDataSource;
    @TableField(value = "mBitmojiComicId")
    public String mBitmojiComicId;
    @TableField(value = "mIsStudioPreview")
    public boolean mIsStudioPreview;

    @TableField(value = "mAttributionName")
    public String mAttributionName;
    @TableField(value = "mIsBackSection")
    public boolean mIsBackSection;

    /*@TableField(value = "mGeofence")
    public ijo mGeofence;*/
    @TableField(value = "hint_translations")
    public Map<String, String> hint_translations;

    /*@TableField(value = "unlockable_track_info")
    public ppf unlockable_track_info;*/
    @TableField(value = "mPriority")
    public int mPriority;
    @TableField(value = "mCreatorUsername")
    public String mCreatorUsername;
    public List<Object> mScheduleIntervals = Collections.emptyList();

    @TableField("mAssetsManifestList")
    public List<String> mAssetsManifestList;

    @TableField(value = "time_added", SQL_DEFAULT = "CURRENT_TIMESTAMP")
    public String timeAdded;

    @TableField(value = "favourited", SQL_DEFAULT = "0")
    public boolean favourited;

    @TableField("lensContext")
    public List<String> lensContext;

    @Override
    public void onTableUpgrade(CBIDatabaseCore linkedDBCore, CBITable table, int oldVersion, int newVersion) {
        List<SQLCommand> sqlCommands = new ArrayList<>();

        if (oldVersion < 4) {
            sqlCommands.add(
                    new SQLCommand(
                            "DELETE FROM Lenses WHERE code IS NULL"
                    )
            );
        }

        if (oldVersion < 5) {
            if (!table.columnExists("mAssetsManifestList")) {
                sqlCommands.add(
                        new SQLCommand(
                                "ALTER TABLE " + table.getTableName()
                                        + " ADD COLUMN mAssetsManifestList String DEFAULT null"
                        )
                );
            }
        }

        if (oldVersion < 6) {
            sqlCommands.add(
                    new SQLCommand(
                            "DELETE FROM Lenses WHERE code LIKE '%bitmoji%'"
                    )
            );
        }

        if (oldVersion < 7) {
            if (!table.columnExists("favourited")) {
                sqlCommands.add(
                        new SQLCommand(
                                "ALTER TABLE " + table.getTableName()
                                        + " ADD COLUMN favourited String DEFAULT 0"
                        )
                );
            }
        }

        if (oldVersion < 8) {
            if (!table.columnExists("lensContext")) {
                sqlCommands.add(
                        new SQLCommand(
                                "ALTER TABLE " + table.getTableName()
                                        + " ADD COLUMN lensContext String DEFAULT null"
                        )
                );
            }
        }

        linkedDBCore.runCommands(sqlCommands);
    }

    public boolean isReady() {
        return code != null && id != null && mLensLink != null && icon_link != null;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public boolean isGroundLens() {
        return categories != null && categories.contains("GROUND");
    }

    public void buildFromFieldMap(Map<String, String> fieldMap, Object lens) {
        for (Entry<String, String> lensEntry : fieldMap.entrySet()) {
            try {
                String annotName = lensEntry.getKey();
                String fieldName = lensEntry.getValue();
                Object value = XposedHelpers.getObjectField(lens, fieldName);

                this.setFieldValue(annotName, value);
            } catch (Throwable ignored) {
            }
        }
    }

    public void setFieldValue(String fieldName, Object value) {
        ValueAdapter adapter = adapterMap.get(fieldName);

        if (adapter != null) {
            Timber.d("Adapter: " + adapter);
            value = adapter.convertFromOriginal(value);
        }

        if (value == null) {
            //Timber.w("Null value for: " + fieldName);
            return;
        }

        XposedHelpers.setObjectField(this, fieldName, value);
    }

    public Object getFieldByTag(String fieldName) {
        try {
            Object convertedValue = getObjectField(this, fieldName);

            ValueAdapter adapter = adapterMap.get(fieldName);

            if (adapter != null)
                convertedValue = adapter.convertToOriginal(convertedValue);

            return convertedValue;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("code", code)
                .add("mActivationCamera", mActivationCamera)
                .add("mIsFeatured", mIsFeatured)
                .add("mType", mType)
                .add("icon_link", icon_link)
                .add("hint_id", hint_id)
                .add("categories", categories)
                .add("mLensLink", mLensLink)
                .add("mAbsoluteCarouselPosition", mAbsoluteCarouselPosition)
                .add("mIsSponsored", mIsSponsored)
                .add("mIsThirdParty", mIsThirdParty)
                .add("mSignature", mSignature)
                .add("mReleaseDate", mReleaseDate)
                .add("mIndexInDataSource", mIndexInDataSource)
                .add("mBitmojiComicId", mBitmojiComicId)
                .add("mIsStudioPreview", mIsStudioPreview)
                .add("mAttributionName", mAttributionName)
                .add("mIsBackSection", mIsBackSection)
                .add("hint_translations", hint_translations)
                .add("mPriority", mPriority)
                .add("mCreatorUsername", mCreatorUsername)
                .add("mScheduleIntervals", mScheduleIntervals)
                .toString();
    }

    @Override
    public void convert(BaseViewHolder holder, ExpandableItemAdapter adapter) {
        boolean showName = getPref(SHOW_LENS_NAMES);
        Context context = holder.itemView.getContext();

        holder.itemView.setBackgroundResource(
                showName ?
                        getDrawable(context, "border_washed")
                        : getDrawable(context, "transparent")
        );
        holder.itemView.setOnClickListener(v -> {
            isActive = !isActive;

            EventBus.getInstance().post(
                    new LensEventRequest(
                            isActive ? LensEvent.LOAD : LensEvent.UNLOAD,
                            this
                    )
            );
        });
        holder.itemView.setOnLongClickListener(
                v -> {
                    EventBus.getInstance().post(
                            new LensEventRequest(
                                    LensEvent.ACTION_MENU,
                                    this
                            )
                    );

                    return true;
                }
        );

        FrameLayout background = getView(holder.itemView, "lens_background_layout");
        background.setBackgroundResource(
                isActive ?
                        getDrawable(context, "lens_bg_selected")
                        : getDrawable(context, "lens_bg_unselected")
        );

        TextView lensLabel = getView(holder.itemView, "lensTextView");
        lensLabel.setVisibility(showName ? View.VISIBLE : View.GONE);
        lensLabel.setText(code);

        ImageView lensIcon = getView(holder.itemView, "lensIconView");

        GlideApp.with(holder.itemView.getContext())
                .load(icon_link)
                .placeholder(getDrawable(context, "spinner_fill_primary"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(getDrawable(context, "delete2_96"))
                .into(lensIcon);
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public static int destroyDataCache() {
        int currentSize = dataCache.size();
        dataCache.clear();
        return currentSize;
    }

    private interface ValueAdapter<T, T2> {
        T2 convertFromOriginal(T value);

        T convertToOriginal(T2 value);
    }

    private static class LensTypeAdapter implements ValueAdapter<Enum<?>, String> {
        @Override
        public String convertFromOriginal(Enum<?> value) {
            String enumName = value.name();
            dataCache.put(enumName, value);
            return enumName;
        }

        @Override
        public Enum<?> convertToOriginal(String value) {
            Enum<?> storedType = (Enum<?>) dataCache.get(value);

            if (storedType != null)
                return storedType;

            try {
                Class typeEnum = HookResolver.resolveHookClass(ENUM_LENS_TYPE);
                storedType = Enum.valueOf(typeEnum, value);
                dataCache.put(storedType.name(), storedType);
                return storedType;
            } catch (HookNotFoundException e) {
                Timber.e(e);
            }

            return null;
        }
    }

    private static class CategoryAdapter implements ValueAdapter<List<Object>, String> {
        private static class Category implements Serializable {
            private static final long serialVersionUID = 2178254826418705244L;
            @SerializedName("Category")
            private String category;
            @SerializedName("Activator")
            private String activatorType;

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .omitNullValues()
                        .add("category", category)
                        .add("activatorType", activatorType)
                        .toString();
            }
        }

        @Override
        public String convertFromOriginal(List<Object> value) {
            try {
                if (value == null)
                    return null;

                List<Category> categories = new ArrayList<>(value.size());

                for (Object category : value) {
                    Category converted = new Category();
                    converted.category = (String) getObjectField(category, HookVariableDef.LENS_CATEGORY.getVarName());
                    converted.activatorType = ((Enum<?>) getObjectField(category, LENS_ACTIVATOR.getVarName())).name();
                    categories.add(converted);
                }

                String cacheKey = categories.toString();
                String storedString = (String) dataCache.get(cacheKey);

                if (storedString != null)
                    return storedString;

                String jsonCategoryString = gson.toJson(categories);

                dataCache.put(cacheKey, jsonCategoryString);
                return jsonCategoryString;
            } catch (Throwable t) {
                Timber.e(t);
            }

            return null;
        }

        @Override
        public List<Object> convertToOriginal(String value) {
            try {
                List<Object> storedList = (List<Object>) dataCache.get(value);

                if (storedList != null)
                    return storedList;

                Class lensCategoryClass;
                Class lensActivatorClass;

                try {
                    lensCategoryClass = HookResolver.resolveHookClass(HookClassDef.LENS_CATEGORY);
                    lensActivatorClass = HookResolver.resolveHookClass(ENUM_LENS_ACTIVATOR_TYPE);
                } catch (HookNotFoundException e) {
                    Timber.e(e);
                    return new ArrayList<>();
                }

                Type listType = new TypeToken<ArrayList<Category>>() {
                }.getType();
                List<Category> unconvertedList = gson.fromJson(value, listType);

                if (unconvertedList == null)
                    return new ArrayList<>();

                List<Object> convertedList = new ArrayList<>(unconvertedList.size());

                for (Category category : unconvertedList) {
                    Enum<?> activatorEnum = Enum.valueOf(lensActivatorClass, category.activatorType);
                    Object lensCategory = newInstance(lensCategoryClass, category.category, activatorEnum);
                    convertedList.add(lensCategory);
                }

                dataCache.put(value, convertedList);

                return convertedList;
            } catch (Throwable t) {
                Timber.e(t);
            }

            return new ArrayList<>();
        }
    }

    private static class AssetAdapter implements ValueAdapter<List<Object>, List<String>> {
        @Override
        public List<String> convertFromOriginal(List<Object> value) {
            if (value == null)
                return null;

            try {
                CBITable<LensAssetObject> lensAssetTable = LensDatabase.getTable(LensAssetObject.class);

                Timber.d("Converting asset: " + value);

                List<String> ids = new ArrayList<>(value.size());

                for (Object asset : value) {
                    String id = (String) getObjectField(asset, "b");
                    ids.add(id);

                    if (!lensAssetTable.contains(id)) {
                        LensAssetObject lensAssetObject = new LensAssetObject();

                        Enum typeEnum = (Enum) getObjectField(asset, "a");
                        Enum loadModeEnum = (Enum) getObjectField(asset, "c");

                        lensAssetObject.id = id;
                        lensAssetObject.type = typeEnum.name();
                        lensAssetObject.loadMode = loadModeEnum.name();
                        lensAssetObject.url = (String) getObjectField(asset, "d");
                        lensAssetObject.signature = (String) getObjectField(asset, "e");
                        lensAssetObject.scale = (int) getObjectField(asset, "f");
                        lensAssetObject.preloadLimit = (int) getObjectField(asset, "g");
                        lensAssetTable.insert(lensAssetObject);
                        Timber.d("LensAssetData: " + lensAssetObject.toString());
                    }
                }

                Timber.d("Id's: " + ids);
                return ids;
            } catch (Throwable t) {
                Timber.e(t);
            }

            return Collections.emptyList();
        }

        @Override
        public List<Object> convertToOriginal(List<String> value) {
            Timber.d("Converting asset list to original: " + value);

            CBITable<LensAssetObject> lensAssetTable = LensDatabase.getTable(LensAssetObject.class);

            Class<?> assetClass;
            Class assetTypeClass;
            Class assetLoadModeClass;

            try {
                assetTypeClass = resolveHookClass(LENS_ASSET_TYPE);
                assetClass = resolveHookClass(LENS_ASSET_BUILT);
                assetLoadModeClass = resolveHookClass(LENS_ASSET_LOAD_MODE);
            } catch (HookNotFoundException e) {
                Timber.e(e);
                return null;
            }

            List<Object> convertedAssets = new ArrayList<>(value.size());

            try {
                for (String lensAssetId : value) {
                    Timber.d("Working on asset: " + lensAssetId);
                    LensAssetObject lensAssetObject = lensAssetTable.getFirst(lensAssetId);

                    Timber.d("Pulled Asset: " + lensAssetObject);

                    if (lensAssetObject == null || lensAssetObject.id == null)
                        continue;

                    try {
                        Constructor constructor = XposedHelpers.findConstructorBestMatch(assetClass, new Class[]{assetTypeClass, String.class, assetLoadModeClass, String.class, String.class, int.class, int.class});

                        Object asset = constructor.newInstance(
                                Enum.valueOf(assetTypeClass, lensAssetObject.type),
                                lensAssetObject.id,
                                Enum.valueOf(assetLoadModeClass, lensAssetObject.loadMode),
                                lensAssetObject.url,
                                lensAssetObject.signature,
                                lensAssetObject.scale,
                                lensAssetObject.preloadLimit
                        );

                        convertedAssets.add(asset);

                        Timber.d("Built asset: " + asset);
                    } catch (Throwable t) {
                        Timber.e(t, "Issue converting asset: " + lensAssetObject);
                    }
                }
            } catch (Throwable t) {
                Timber.e(t);
            }
            return convertedAssets.isEmpty() ? null : convertedAssets;
        }
    }

    private static class LensContextAdapter implements ValueAdapter<Object, List<String>> {
        @Override
        public List<String> convertFromOriginal(Object value) {
            if (value == null)
                return Collections.emptyList();

            Timber.d("Converting new lens contexts");

            List<String> contextList = new ArrayList<>();
            FieldMapper mapper = FieldMapper.getMapper("Context");
            Set<Enum> cameraSet = mapper.getFieldVal(value, "cameraContexts");
            Set<Enum> applicationSet = mapper.getFieldVal(value, "applicableContexts");

            if (cameraSet != null) {
                for (Enum cameraContext : cameraSet) {
                    contextList.add("CAM:" + cameraContext);
                    Timber.d("Added lens context: " + "CAM:" + cameraContext.name());
                }
            }

            if (applicationSet != null) {
                for (Enum applicationContext : applicationSet) {
                    contextList.add("APP:" + applicationContext.name());
                    Timber.d("Added application context: " + "APP:" + applicationContext.name());
                }
            }

            return contextList;
        }

        @Override
        public Object convertToOriginal(List<String> value) {
            Timber.d("Converting context list to original: " + value);

            Class contextHolderClass;

            try {
                contextHolderClass = HookResolver.resolveHookClass(LENS_CONTEXT_HOLDER);
            } catch (HookNotFoundException e) {
                Timber.e(e);
                return null;
            }

            try {
                Class cameraContextClass = HookResolver.resolveHookClass(LENS_CAMERA_CONTEXT_ENUM);
                Class applicationContextClass = HookResolver.resolveHookClass(LENS_APPLICATION_CONTEXT_ENUM);

                Set cameraContextSet = EnumSet.noneOf(cameraContextClass);
                Set appContextSet = EnumSet.noneOf(applicationContextClass);

                boolean hasCameraContext = false;
                boolean hasAppContext = false;

                if (value != null) {
                    for (String context : value) {
                        Timber.d("Converted lens context to original: " + context);

                        if (context.startsWith("CAM:")) {
                            hasCameraContext = true;
                            cameraContextSet.add(Enum.valueOf(
                                    cameraContextClass,
                                    context.substring(4)
                            ));

                        } else if (context.startsWith("APP:")) {
                            hasAppContext = true;
                            appContextSet.add(Enum.valueOf(
                                    applicationContextClass,
                                    context.substring(4)
                            ));
                        }
                    }
                }

                if (!hasCameraContext) {
                    cameraContextSet = (Set) getStaticObjectField(cameraContextClass, "FRONT_AND_REAR");
                    Timber.d("Lens context contains no camera context... Using default");
                }

                if (!hasAppContext) {
                    appContextSet = (Set) getStaticObjectField(applicationContextClass, "LIVE_CAMERA_AND_VIDEO_CHAT");
                    Timber.d("Lens context contains no app context... Using default");
                }

                return newInstance(contextHolderClass, cameraContextSet, appContextSet);
            } catch (HookNotFoundException e) {
                Timber.e(e);
            }

            return newInstance(contextHolderClass, Collections.emptySet(), Collections.emptySet());
        }
    }
}