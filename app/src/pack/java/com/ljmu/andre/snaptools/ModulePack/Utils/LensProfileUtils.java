package com.ljmu.andre.snaptools.ModulePack.Utils;

import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.ljmu.andre.CBIDatabase.CBITable;
import com.ljmu.andre.snaptools.ModulePack.Databases.LensDatabase;
import com.ljmu.andre.snaptools.ModulePack.Databases.Tables.LensObject;
import com.ljmu.andre.snaptools.Utils.MapUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.BACKUPS_PATH;
import static com.ljmu.andre.snaptools.Utils.StringUtils.yyyyMMddHHmmss;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class LensProfileUtils {
    public static String getFreshFilenameNoExt() {
        return yyyyMMddHHmmss.format(new Date(System.currentTimeMillis()));
    }

    public static boolean deleteProfile(String profileName) {
        File profileFile = getProfileFile(profileName);

        if (profileFile == null || !profileFile.exists()) {
            Timber.w("Lens Profile file doesn't exist");
            return true;
        }

        return profileFile.delete();
    }

    private static File getProfileFile(String profileName) {
        File profileDir = getCreateDir(BACKUPS_PATH);
        if (profileDir == null || !profileDir.exists()) {
            Timber.w("Lens Profile dir not created");
            return null;
        }

        return new File(profileDir, createFilename(profileName));
    }

    public static String createFilename(String profileName) {
        return "LensProfile_" + profileName + ".json";
    }

    public static boolean backupCurrentProfile(String profileName) {
        CBITable<LensObject> lensTable = LensDatabase.getTable(LensObject.class);
        Collection<LensObject> lensList = lensTable.getAll();
        List<LensProfileModel> lensProfileModelList = new ArrayList<>();

        for (LensObject lens : lensList) {
            if (lens.isActive || lens.isFavourited()) {
                lensProfileModelList.add(
                        LensProfileModel.fromLens(lens)
                );
            }
        }

        Timber.d("Found %s lenses worth backing up", lensProfileModelList.size());

        File profileFile = getProfileFile(profileName);
        Closer closer = Closer.create();

        try {
            //noinspection ResultOfMethodCallIgnored
            profileFile.createNewFile();

            FileWriter writer = closer.register(new FileWriter(profileFile));
            new Gson().toJson(lensProfileModelList, writer);
            writer.flush();

            Timber.d("Backup successful");
            return true;
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }

        Timber.d("Backup failed");
        return false;
    }

    public static Result<Boolean, String> restoreProfile(String profile) {
        File profileFile = getProfileFile(profile);

        if (profileFile == null || !profileFile.exists()) {
            Timber.w("Lens Profile file doesn't exist");
            return new Result<>(false, "Couldn't find Profile file");
        }

        Closer closer = Closer.create();

        int successfulUpdates = 0;

        try {
            FileReader reader = closer.register(new FileReader(profileFile));
            Type listType = new TypeToken<ArrayList<LensProfileModel>>() {
            }.getType();

            List<LensProfileModel> lensProfileModelList = new Gson().fromJson(reader, listType);

            if (lensProfileModelList == null)
                return new Result<>(false, "Failed to load Lens Profile");

            if (lensProfileModelList.isEmpty())
                return new Result<>(false, "No Lenses found in the profile to restore");

            Timber.d("Found %s lenses to restore", lensProfileModelList.size());

            CBITable<LensObject> lensTable = LensDatabase.getTable(LensObject.class);
            Map<String, LensObject> lensObjects = MapUtils.convertList(lensTable.getAll(), mapEntry -> mapEntry.id);

            for (LensProfileModel lensProfileModel : lensProfileModelList) {
                LensObject lensObject = lensObjects.get(lensProfileModel.getId());

                if (lensObject == null) {
                    continue;
                }

                lensProfileModel.updateLens(lensObject);

                if (lensTable.insert(lensObject))
                    successfulUpdates++;
            }

            return new Result<>(successfulUpdates > 0, "Restored [" + successfulUpdates + "/" + lensProfileModelList.size() + "] lens states");
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                closer.close();
            } catch (IOException ignored) {
            }
        }

        return new Result<>(false, "Failed to load Lens Profile");
    }

    private static class LensProfileModel {
        @SerializedName("id")
        private String id;
        @SerializedName("favourited")
        private boolean isFavourited;
        @SerializedName("active")
        private boolean isActive;

        public String getId() {
            return id;
        }

        public LensProfileModel setId(String id) {
            this.id = id;
            return this;
        }

        public void updateLens(LensObject lens) {
            if (!lens.id.equals(id))
                throw new IllegalArgumentException("Lens doesn't match profile model");

            lens.favourited = isFavourited();
            lens.isActive = isActive();
        }

        public boolean isFavourited() {
            return isFavourited;
        }

        public LensProfileModel setFavourited(boolean favourited) {
            isFavourited = favourited;
            return this;
        }

        public boolean isActive() {
            return isActive;
        }

        public LensProfileModel setActive(boolean active) {
            isActive = active;
            return this;
        }

        public static LensProfileModel fromLens(LensObject lens) {
            return new LensProfileModel()
                    .setId(lens.id)
                    .setFavourited(lens.favourited)
                    .setActive(lens.isActive);
        }
    }
}
