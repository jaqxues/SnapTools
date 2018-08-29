package com.ljmu.andre.snaptools.UIComponents;

import com.ljmu.andre.snaptools.R;
import timber.log.Timber;

/**
 * ===========================================================================
 * Source Code/Implementation created by Ethan (ElectronicWizard)
 *
 * @see <a href="https://github.com/ElectronicWizard">Ethan's Github</a>
 * ===========================================================================
 */
public enum UITheme {

    DEFAULT("Default", 0, R.color.backgroundPrimary, R.style.DefaultTheme), AMOLED_BLACK("AMOLED Black", 1, R.color.backgroundPrimaryAMOLED, R.style.AMOLEDBlack);

    private String name;
    private int id;
    private int colorBG;
    private int theme;

    UITheme(String name, int id, int colorBG, int theme) {
        this.name = name;
        this.id = id;
        this.colorBG = colorBG;
        this.theme = theme;
    }

    public int getColorBackgroundID() {
        return colorBG;
    }

    public int getTheme() {
        return theme;
    }

    public static UITheme getMatchingThemeFromName(String name) {
        for (UITheme u : values()) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u;
            }
        }
        Timber.e("Failed to load correct Theme for app, using default.");
        return UITheme.DEFAULT;
    }

    public String getName() {
        return name;
    }

    public static UITheme getMatchingThemeByID(int id) {
        for (UITheme u : values()) {
            if (u.getID() == id) {
                return u;
            }
        }
        Timber.e("Failed to load correct Theme for app, using default.");
        return UITheme.DEFAULT;
    }

    public int getID() {
        return id;
    }

}

