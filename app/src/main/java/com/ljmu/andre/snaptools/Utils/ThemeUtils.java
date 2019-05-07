package com.ljmu.andre.snaptools.Utils;


import android.app.Activity;
import android.content.Intent;

import com.ljmu.andre.snaptools.R;

/**
 * ===========================================================================
 * Source Code/Implementation created by Ethan (ElectronicWizard)
 *
 * @see <a href="https://github.com/ElectronicWizard">Ethan's Github</a>
 * ===========================================================================
 */
public class ThemeUtils {

    public final static int DEFAULT = 0;
    public final static int AMOLED_BLACK = 1;
    public static int cTheme;

    public static void changeToTheme(Activity activity, int theme) {
        cTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (cTheme) {
            default:
            case DEFAULT:
                activity.setTheme(R.style.DefaultTheme);
                break;
            case AMOLED_BLACK:
                activity.setTheme(R.style.AMOLEDBlack);
                break;

        }

    }

}