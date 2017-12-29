package nov.me.kanmodel.notes.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;

/**
 *
 * Created by KanModel on 2017/12/29.
 */

public class Utils {
    private static final String TAG = "Utils";

    private static Typeface fontAwesome;

    /**
     * @param context Context
     * @return fontawesome字体
     */
    public static Typeface getFontAwesome(Context context) {
        if (fontAwesome == null) {
            Log.d(TAG, "getFontAwesome: 不存在并添加");
            fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        }
        Log.d(TAG, "getFontAwesome: 存在");
        return fontAwesome;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param context
     * @return 返回版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
