package nov.me.kanmodel.notes.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.util.Log

/**
 *
 * Created by KanModel on 2017/12/29.
 */

object Utils {
    private const val TAG = "Utils"

    private var fontAwesome: Typeface? = null

    /**
     * @param context Context
     * @return fontawesome字体
     */
    fun getFontAwesome(context: Context): Typeface? {
        if (fontAwesome == null) {
            Log.d(TAG, "getFontAwesome: 不存在并添加")
            fontAwesome = Typeface.createFromAsset(context.assets, "fontawesome-webfont.ttf")
        }
        Log.d(TAG, "getFontAwesome: 存在")
        return fontAwesome
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param context
     * @return 返回版本号
     */
    fun getVersionCode(context: Context): Int {
        var versionCode = 0
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionCode
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    fun getVersionName(context: Context): String {
        var verName = ""
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return verName
    }
}
