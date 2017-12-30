package nov.me.kanmodel.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import nov.me.kanmodel.notes.R;

/**
 *
 * Created by KanModel on 2017/12/27.
 */

public class PreferenceManager {
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private Context context;

    private static final String APP_SHARED_PREFS="nov.me.kanmodel.notes";
    private static final String FIRST_LAUNCH = "first_launch";

    public PreferenceManager(Context context) {
        this.context = context;
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public boolean isFirstLaunch() {
        return appSharedPrefs.getBoolean(FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        prefsEditor.putBoolean(FIRST_LAUNCH, firstLaunch);
        prefsEditor.commit();
    }

    public boolean getDebug(){
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_preference_is_debug", false);
    }

    public int getFontContextSize(){
        return Integer.parseInt(android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getString("font_content_size", String.valueOf(context.getResources().getInteger(R.integer.font_content_size_default))));
    }
    public int getFontTimeSize(){
        return Integer.parseInt(android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getString("font_time_size", String.valueOf(context.getResources().getInteger(R.integer.font_time_size_default))));
    }
    public int getFontTitleSize(){
        return Integer.parseInt(android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getString("font_title_size", String.valueOf(context.getResources().getInteger(R.integer.font_title_size_default))));
    }
}
