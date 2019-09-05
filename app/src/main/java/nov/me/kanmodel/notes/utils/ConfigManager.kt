package nov.me.kanmodel.notes.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import nov.me.kanmodel.notes.R

/**
 * Preferences管理类
 * Created by KanModel on 2017/12/27.
 */
class ConfigManager(private val context: Context) {
    private val appSharedPrefs: SharedPreferences

    var isFirstLaunch: Boolean
        get() = appSharedPrefs.getBoolean(FIRST_LAUNCH, true)
        set(firstLaunch) {
            val prefsEditor = appSharedPrefs.edit()
            prefsEditor.putBoolean(FIRST_LAUNCH, firstLaunch)
            prefsEditor.apply()
        }

    val debug: Boolean
        get() = appSharedPrefs.getBoolean("switch_preference_is_debug", false)
    val fontContextSize: Int
        get() = Integer.parseInt(appSharedPrefs.getString("font_content_size", context.resources.getInteger(R.integer.font_content_size_default).toString())!!)
    val fontTimeSize: Int
        get() = Integer.parseInt(appSharedPrefs.getString("font_time_size", context.resources.getInteger(R.integer.font_time_size_default).toString())!!)
    val fontTitleSize: Int
        get() = Integer.parseInt(appSharedPrefs.getString("font_title_size", context.resources.getInteger(R.integer.font_title_size_default).toString())!!)

    init {
        appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
    }

    companion object {

        // Path:/data/data/nov.me.kanmodel.notes/shared_prefs/nov.me.kanmodel.notes_preferences.xml
        private const val APP_SHARED_PREFS = "nov.me.kanmodel.notes_preferences"
        private const val FIRST_LAUNCH = "first_launch"
    }
}
