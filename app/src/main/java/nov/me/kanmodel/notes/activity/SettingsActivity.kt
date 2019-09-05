package nov.me.kanmodel.notes.activity

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import nov.me.kanmodel.notes.R
import nov.me.kanmodel.notes.activity.adapter.NoteAdapter
import nov.me.kanmodel.notes.activity.ui.AppCompatPreferenceActivity
import nov.me.kanmodel.notes.utils.FileUtils
import java.io.IOException

/**
 * 应用设置Activity
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onHeaderClick(header: Header, position: Int) {
        Log.d(TAG, "onHeaderClick id: " + header.id)
        if (header.id == R.id.header_backup.toLong()) {
            Log.d(TAG, "onHeaderClick id headerBackup: " + header.id)
            try {
                val dbCopy = FileUtils.saveDatabaseCopy(applicationContext, filesDir)//获取备份数据库文件路径
                FileUtils.showSendFileScreen(dbCopy, this)
                Log.d(TAG, "onHeaderClick: file_path :$dbCopy")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        super.onHeaderClick(header, position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * 初始化ActionBar
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return (PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
                || FontFragment::class.java.name == fragmentName)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 总体设置 二级设置菜单
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)
            val isDebugSwitch = findPreference("switch_preference_is_debug") as SwitchPreference
            isDebugSwitch.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                if (MainActivity.getIsDebug()) {
                    Toast.makeText(activity, "关闭开发者模式", Toast.LENGTH_SHORT).show()
                    MainActivity.setIsDebug(false)
                } else {
                    Toast.makeText(activity, "开启开发者Debug模式", Toast.LENGTH_SHORT).show()
                    MainActivity.setIsDebug(true)
                }
                true
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * 字体设置 二级设置菜单
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class FontFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_font)
            setHasOptionsMenu(true)
            val fontTitleSize = findPreference("font_title_size") as EditTextPreference
            val fontTimeSize = findPreference("font_time_size") as EditTextPreference
            val fontContentSize = findPreference("font_content_size") as EditTextPreference
            fontTitleSize.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                NoteAdapter.setTitleFontSize(Integer.parseInt(fontTitleSize.text))
                Log.d(TAG, "onPreferenceChange: 修改标题字体大小为 " + NoteAdapter.getTitleFontSize())
                true
            }
            fontTimeSize.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                NoteAdapter.setTimeFontSize(Integer.parseInt(fontTimeSize.text))
                Log.d(TAG, "onPreferenceChange: 修改时间字体大小为 " + NoteAdapter.getTimeFontSize())
                true
            }
            fontContentSize.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                NoteAdapter.setContentFontSize(Integer.parseInt(fontContentSize.text))
                Log.d(TAG, "onPreferenceChange: 修改内容字体大小为 " + NoteAdapter.getContentFontSize())
                true
            }
            //todo 更改字体设置改变相应字体，提示，设置字体安全范围
            bindPreferenceSummaryToValue(findPreference("font_title_size"))
            bindPreferenceSummaryToValue(findPreference("font_time_size"))
            bindPreferenceSummaryToValue(findPreference("font_content_size"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private const val TAG = "SettingsActivity"

        /**
         * 偏好变更监听
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val index = preference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent)

                } else {
                    val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue))

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null)
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * 绑定偏好摘要
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
