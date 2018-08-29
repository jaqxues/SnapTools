package com.ljmu.andre.snaptools.ModulePack.Utils;

import timber.log.Timber;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FONTS_PATH;

/**
 * Created by ethan on 1/11/2018.
 */

public class FontUtils {

    public static List<String> getInstalledFonts() {
        List<String> items = new ArrayList<>();
        File fontFolder = getCreateDir(FONTS_PATH);

        if (fontFolder != null) {
            Timber.d("Searching for fonts in \"" + fontFolder.getPath() + "\"");

            File[] files = fontFolder.listFiles((file, s) -> s.endsWith(".ttf"));
            items.add("Default");
            if (files != null) {
                for (File f : files) {
                    //noinspection OctalInteger
                    byte[] compare = {00, 01, 00, 00, 00};
                    byte[] check = new byte[5];
                    try {
                        InputStream is = new FileInputStream(f);
                        is.read(check);
                        is.close();
                    } catch (IOException e) {
                        Timber.e(e);
                    }

                    Timber.d("Template: \"" + Arrays.toString(compare) + "\"|" + "Input: \"" + Arrays.toString(check) + "\"");
                    if (Arrays.equals(compare, check)) {
                        items.add(f.getName());
                        Timber.d("Font is valid. Adding to list.");
                    }
                }
            }
        }

        return items;
    }

}
