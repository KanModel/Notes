package nov.me.kanmodel.notes.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nov.me.kanmodel.notes.MainActivity;
import nov.me.kanmodel.notes.Note;
import nov.me.kanmodel.notes.NoteAdapter;
import nov.me.kanmodel.notes.widget.WidgetInfo;

/**
 * 存放各种操作方法的助手类
 * Created by KanModel on 2017/11/26.
 */

public abstract class Aid {

    private static final String TAG = "AidClass";

    public static int pos = 0;

    public static long getNowTime() {
        return new Date().getTime();
    }

    /**
     * @param time 字符串类型的时间戳
     * @return 时间字符串
     */
    public static String stampToDate(String time) {
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.valueOf(time);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * @param time long类型的时间戳
     * @return 时间字符串
     */
    public static String stampToDate(long time) {
        return stampToDate(String.valueOf(time));
    }

    /**
     * @param dbHelper 数据库操作类
     * @return Note类
     */
    public static Note addSQLNote(DatabaseHelper dbHelper) {
        return addSQLNote(dbHelper, "content", "title");
    }

    /**
     * @param dbHelper 数据库操作类
     * @param note     Note类
     */
    public static void addSQLNote(DatabaseHelper dbHelper, Note note) {
        addSQLNote(dbHelper, note.getContent(), note.getTitle());
    }

    /**
     * @param dbHelper 数据库操作类
     * @param content  内容
     * @param title    标题
     * @return 返回新添加的Note类
     */
    public static Note addSQLNote(DatabaseHelper dbHelper, String content, String title) {
        return addSQLNote(dbHelper, content, title, Aid.getNowTime(), Aid.getNowTime());
    }

    public static Note addSQLNote(DatabaseHelper dbHelper, String content, String title, long timeStamp, long lastChangedTimeStamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Note note;
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("title", title);
        values.put("time", timeStamp);
        values.put("lastChangedTime", lastChangedTimeStamp);
        db.insert("Note", null, values);
        Log.d(TAG, "addSQLNote: timestamp:" + timeStamp);
        //获取数据库最后一条信息
        Cursor cursor1 = db.rawQuery("select * from Note", null);
        if (cursor1.moveToLast()) {
            String logtime = cursor1.getString(cursor1.getColumnIndex("logtime"));
            long time = cursor1.getLong(cursor1.getColumnIndex("time"));
            long lastChangedTime = cursor1.getLong(cursor1.getColumnIndex("lastChangedTime"));
            note = new Note(title, content, logtime, time, lastChangedTime);
        } else {
            note = null;
        }
        cursor1.close();
        return note;
    }

    /**
     * @param title           标题
     * @param content         内容
     * @param time            时间戳
     * @param pos             在RecyclerView中的位置
     * @param lastChangedTime 最后更改的时间戳
     */
    public static void updateSQLNote(String title, String content, long time, int pos, long lastChangedTime) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("lastChangedTime", lastChangedTime);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        NoteAdapter.getNotes().get(pos).setTitle(title);
        NoteAdapter.getNotes().get(pos).setContent(content);
        NoteAdapter.getNotes().get(pos).setLastChangedTime(lastChangedTime);
        MainActivity.getNoteAdapter().refreshData(pos);
    }

    /**
     * 根据时间戳搜索数据库中的内容设置isDeleted为1代表删除
     *
     * @param time 时间戳
     */
    public static void deleteSQLNote(long time) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
    }

    /**
     * 清空数据库
     */
    public static void deleteSQLNoteForced() {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        db.delete("Note", "time > ?", new String[]{"0"});
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

    public static List<Note> initNotes(DatabaseHelper dbHelper) {
        List<Note> noteList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDeleted == 0) {
                    noteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteList;
    }

    public static Note querySQLNote(DatabaseHelper dbHelper, long time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String title = "", content = "";
        int isDeleted;
        Cursor cursor = db.query("Note", null, "time like ?", new String[]{String.valueOf(time)}, null, null, null);
        if (cursor.moveToLast()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
        } else {
            isDeleted = 1;
        }
        cursor.close();
        if (isDeleted == 1) {
            return null;
        }
        return new Note(title, content, time);
    }

    public static WidgetInfo querySQLWidget(DatabaseHelper dbHelper, long time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int widgetID, isDeleted;
        Cursor cursor = db.query("widget", null, "time like ?", new String[]{String.valueOf(time)}, null, null, null);
        if (cursor.moveToLast()) {
            widgetID = cursor.getInt(cursor.getColumnIndex("widgetID"));
            isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
        } else {
            widgetID = isDeleted = 1;
        }
        cursor.close();
        if (isDeleted == 1) {
            return null;
        }
        return new WidgetInfo(time, widgetID, querySQLNote(dbHelper, time));
    }

    public static void addSQLWidget(DatabaseHelper dbHelper, long time, int appWidgetId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("widgetID", appWidgetId);
        db.insert("widget", null, values);
//        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
    }

    public static void deleteSQLWidget(DatabaseHelper dbHelper, int widgetID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("widget", values, "widgetID = ?", new String[]{String.valueOf(widgetID)});
    }

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

}
