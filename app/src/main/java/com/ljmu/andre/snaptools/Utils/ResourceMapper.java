package com.ljmu.andre.snaptools.Utils;

import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.util.SparseIntArray;

import com.ljmu.andre.snaptools.BuildConfig;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class ResourceMapper {
    private static SparseIntArray resourceMap = new SparseIntArray();

    public static SparseIntArray getMap() {
        return resourceMap;
    }

    public static Integer getFakeRes(Integer res) {
        Integer fakeId = resourceMap.get(res);

        if (fakeId == 0) {
            Timber.d("Fake Resource not found [Id: %s]", res);
            return res;
        }

        return fakeId;
    }

    public static void mapAllColors(XModuleResources moduleResources, Integer... ids) {
        for (Integer id : ids)
            mapColor(moduleResources, id);
    }

    public static void mapColor(XModuleResources moduleResources, Integer res) {
        if (resourceMap.get(res) != 0)
            resourceMap.put(res, moduleResources.getColor(res, null));
    }

    public static void mapAll(XResources xResources, XModuleResources target, Integer... ids) {
        for (Integer id : ids)
            mapResource(xResources, target, id);
    }

    public static void mapResource(XResources xResources, XModuleResources target, Integer res) {
        addFakeRes(res, xResources.addResource(target, res));
    }

    public static void addFakeRes(Integer originalRes, Integer fakeRes) {
        Timber.d("Putting Res [Original: %s][Fake: %s]", originalRes, fakeRes);

        resourceMap.put(originalRes, fakeRes);
    }

    public static int getResId(Context context, String tag, String type) {
        return context.getResources().getIdentifier(tag, type,
                context.getPackageName().equals("com.snapchat.android") ?
                        "com.snapchat.android" : // Needed for Resource Things like Stealth in Snapchat
                        BuildConfig.APPLICATION_ID); // Needed for App Repackaging
    }
}
