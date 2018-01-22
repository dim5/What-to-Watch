package com.danielmarczin.whattowatch.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.danielmarczin.whattowatch.BuildConfig;

import java.util.List;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public final class Util {
    public static final Random random = new Random();

    private Util() {
    }

    public static <T> T getRandomElement(final List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T getRandomElement(final T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static boolean checkFirstRun(Context context, final String tag) {
        final String prefsName = "FirstRunPrefs";
        final String versionCode = "versionCode_" + tag;
        final int none = -1;

        int currentVersion = BuildConfig.VERSION_CODE;
        SharedPreferences prefs = context.getSharedPreferences(prefsName, MODE_PRIVATE);
        int savedVersion = prefs.getInt(versionCode, none);

        boolean result = false;
        if (savedVersion == none) { //first run
            result = true;
        } else if (savedVersion < currentVersion) { //upgrade
            result = false;
        }

        prefs.edit().putInt(versionCode, currentVersion).apply();
        return result;
    }
}