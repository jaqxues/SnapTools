package com.ljmu.andre.snaptools.ModulePack.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.ljmu.andre.snaptools.Fragments.FragmentHelper;
import com.ljmu.andre.snaptools.ModulePack.Fragments.KotlinViews.MiscChangesViewProvider;
import com.ljmu.andre.snaptools.ModulePack.MiscChanges;
import com.ljmu.andre.snaptools.ModulePack.Notifications.SafeToastAdapter;
import com.ljmu.andre.snaptools.ModulePack.Utils.Result;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.ljmu.andre.GsonPreferences.Preferences.getCreateDir;
import static com.ljmu.andre.GsonPreferences.Preferences.getPref;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.CURRENT_FONT;
import static com.ljmu.andre.snaptools.ModulePack.Utils.ModulePreferenceDef.FONTS_PATH;
import static com.ljmu.andre.snaptools.Utils.PreferenceHelpers.putAndKill;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getDSLView;
import static com.ljmu.andre.snaptools.Utils.ResourceUtils.getIdFromString;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class MiscChangesFragment extends FragmentHelper {
    private MiscChangesViewProvider viewProvider;

    // ===========================================================================
    private ViewGroup mainContainer;
    private ViewGroup generalContainer;
    private ViewGroup experimentsContainer;

    private Spinner fontSpinner;
    // ===========================================================================

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewProvider = new MiscChangesViewProvider(
                getActivity(),
                viewGroup -> generalContainer = viewGroup,
                viewGroup -> experimentsContainer = viewGroup,
                this::handleEvent
        );

        mainContainer = viewProvider.getMainContainer();
        fontSpinner = getDSLView(generalContainer, "font_selector_spinner");

        return mainContainer;
    }

    @Override
    public void onResume() {
        super.onResume();
        initFontSpinner();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initFontSpinner() {
        List<String> fontList = viewProvider.getFontList();
        fontList.clear();
        fontList.addAll(getInstalledFonts());

        viewProvider.refreshFontAdapter();

        String currentFont = getPref(CURRENT_FONT);
        fontSpinner.setSelection(fontList.indexOf(currentFont), false);
    }

    public static List<String> getInstalledFonts() {
        List<String> fontList = new ArrayList<>();
        fontList.clear();
        fontList.add("Default");

        File fontFolder = getCreateDir(FONTS_PATH);

        if (fontFolder != null) {
            Timber.d("Searching for fonts in \"" + fontFolder.getPath() + "\"");

            //noinspection ResultOfMethodCallIgnored
            fontFolder.list((dir, name) -> {
                if (name.endsWith(".ttf") && isFileTTF(new File(dir, name)))
                    fontList.add(name);

                return false;
            });
        }

        return fontList;
    }

    private static boolean isFileTTF(File file) {
        //noinspection OctalInteger
        byte[] compare = {00, 01, 00, 00, 00};
        byte[] check = new byte[5];
        try {
            InputStream is = new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            is.read(check);
            is.close();
        } catch (IOException e) {
            Timber.e(e);
        }

        return Arrays.equals(compare, check);
    }

    private void handleEvent(@NotNull Result<MiscChangesEvent, Object> eventResult) {
        switch (eventResult.getKey()) {
            case FONT_SELECTED:
                String selectedFont = (String) eventResult.getValue();
                Timber.d("Selected Font: " + selectedFont);

                if (selectedFont.equalsIgnoreCase("Select font")) {
                    break;
                }

                if (selectedFont.equalsIgnoreCase("No fonts found")) {
                    SafeToastAdapter.showErrorToast(getActivity(), "No valid .ttf files found");
                    break;
                }

                putAndKill(CURRENT_FONT, selectedFont, getActivity());
                break;
            default:
                throw new IllegalStateException("Unknown MiscChanges Event: " + eventResult.toString());
        }
    }

    public static Typeface getTypefaceSafe(String filename) {
        if (!filename.equalsIgnoreCase("Default")) {
            File fontsDir = getCreateDir(FONTS_PATH);
            File fontFile = new File(fontsDir, filename);

            if (fontFile.exists()) {
                return MiscChanges.createTypefaceSafe(fontFile);
            } else
                Timber.d("Font file doesn't exist: " + fontFile);
        }

        return Typeface.DEFAULT;
    }

    public enum MiscChangesEvent {
        FONT_SELECTED
    }

    @Override
    public String getName() {
        return "Misc Changes";
    }

    @Override
    public Integer getMenuId() {
        return getIdFromString(getName());
    }


}
