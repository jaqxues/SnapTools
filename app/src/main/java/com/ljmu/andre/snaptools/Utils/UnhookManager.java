package com.ljmu.andre.snaptools.Utils;

import androidx.annotation.Nullable;

import com.ljmu.andre.snaptools.Framework.FrameworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook.Unhook;
import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class UnhookManager {
    private static final Object UNHOOK_LOCK = new Object();
    private static Map<String, List<Unhook>> unhookMap = new HashMap<>();
    private static boolean abort;

    public static void addUnhook(Unhook unhook) {
        addUnhook("Default", unhook);
    }

    public static void addUnhook(String tag, Unhook unhook) {
        synchronized (UNHOOK_LOCK) {
            if (abort) {
                unhook.unhook();
                return;
            }

            List<Unhook> unhooks = unhookMap.get(tag);

            if (unhooks == null)
                unhooks = new ArrayList<>();

            unhooks.add(unhook);
            unhookMap.put(tag, unhooks);
        }
    }

    public static void abortSystem() {
        synchronized (UNHOOK_LOCK) {
            unhookAll();
            abort = true;
            FrameworkManager.getModulePackList().clear();
        }
    }

    public static void unhookAll() {
        unhookAll(null);
    }

    @RequiresFramework(77)
    public static void unhookAll(@Nullable String key) {
        Timber.d("Performing unhook all");

        synchronized (UNHOOK_LOCK) {
            Timber.d("Entered lock");

            Iterator<Entry<String, List<Unhook>>> unhookIterator = unhookMap.entrySet().iterator();

            while (unhookIterator.hasNext()) {
                Entry<String, List<Unhook>> unhookEntry = unhookIterator.next();
                if (!unhookEntry.getKey().equals(key) && key != null)
                    continue;

                List<Unhook> unhooks = unhookEntry.getValue();
                Timber.d("Unhooking Unhook List %s (Size: %s)", key, unhooks.size());

                for (Unhook unhook : unhooks) {
                    if (unhook == null)
                        continue;

                    unhook.unhook();
                }

                unhookIterator.remove();
            }

            Timber.d("Done unhooking items");
        }

        Timber.d("Exiting unhook");
    }

    public static void unhook(String tag) {
        List<Unhook> unhooks = unhookMap.get(tag);

        if (unhooks != null) {
            Timber.d("Found %s hooks to unhook", unhooks.size());

            for (Unhook unhook : unhooks)
                unhook.unhook();

            unhookMap.remove(tag);
        }

        Timber.d("Done with unhook: " + tag);
    }

    public static void debugAllHooks() {
        Timber.d("HookMapSize: " + unhookMap.size());

        for (Entry<String, List<Unhook>> entry : unhookMap.entrySet()) {
            Timber.d("Entry [Key: %s][Size: %s]", entry.getKey(), entry.getValue().size());
        }
    }

    public static void addUnhook(Set<Unhook> unhookSet) {
        addUnhook("Default", unhookSet);
    }

    public static void addUnhook(String tag, Set<Unhook> unhookSet) {
        synchronized (UNHOOK_LOCK) {
            if (abort) {
                for (Unhook unhook : unhookSet)
                    unhook.unhook();

                return;
            }

            List<Unhook> unhooks = unhookMap.get(tag);

            if (unhooks == null)
                unhooks = new ArrayList<>();

            unhooks.addAll(unhookSet);
            unhookMap.put(tag, unhooks);
        }
    }
}
