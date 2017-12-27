package nov.me.kanmodel.notes.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.utils.Aid;
import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.MainActivity;
import nov.me.kanmodel.notes.Note;
import nov.me.kanmodel.notes.NoteAdapter;
import nov.me.kanmodel.notes.R;

/**
 * Implementation of App Widget functionality.
 */
public class NoteAppWidget extends AppWidgetProvider {
    private static final String TAG = "NoteAppWidget";
    private static DatabaseHelper dbHelper;
    private List<WidgetInfo> widgetInfoList = new ArrayList<>();
    private static List<Note> notes = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, final int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_app_widget);
        Note note = null;
        for (WidgetInfo widgetInfo : widgetInfoList) {//遍历表寻找对应id的挂件
            if (widgetInfo.getAppWidgetID() == appWidgetId) {
                note = widgetInfo.getNote();
            }
        }
        /*widgetInfoList.forEach(wi -> {
            if (wi.getAppWidgetID() == appWidgetId) {
                note = wi.getNote();
            }
        });*/
        if (note == null) {//数据库中没有相关信息进行添加
            notes = Aid.initNotes(dbHelper);
            long time = notes.get(Aid.pos).getTime();
            Aid.addSQLWidget(dbHelper, time, appWidgetId);
            note = Aid.querySQLNote(dbHelper, time);
            updateWidgetInfoList(dbHelper.getWritableDatabase());//添加后刷新表
            if (note == null) {
                note = new Note("此便签可能以删除,请您手动删除", "", Aid.getNowTime());
            }
        }
//        CharSequence widgetTitle = context.getString(R.string.appwidget_text);
        String widgetTitle = note.getTitle();
        String time = Aid.stampToDate(note.getTime());
        String content = note.getContent();
//        if (NoteAdapter.getNotes() != null) {
//            note = NoteAdapter.getNotes().get(Aid.pos);
//            widgetTitle = note.getTitle();
//            time = note.getLogTime();
//            content = note.getContent();
//        } else {
//            if (MainActivity.getIsDebug()) {
//                Toast.makeText(context, "开机Debug", Toast.LENGTH_SHORT).show();
//            }
//            List<Note> noteList = Aid.initNotes(dbHelper);
//            note = noteList.get(0);
//            if (note != null) {
//                widgetTitle = note.getTitle();
//                time = note.getLogTime();
//                content = note.getContent();
//            } else {
//                widgetTitle = "加载失败，请删除本挂件";
//                time = "";
//                content = "";
//            }
//        }
        //设置挂件内容
        views.setTextViewText(R.id.widget_title, widgetTitle);
        views.setTextViewText(R.id.widget_time, time);
        views.setTextViewText(R.id.widget_content, content);
        views.setTextViewTextSize(R.id.widget_title, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getTitleFontSize());
        views.setTextViewTextSize(R.id.widget_time, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getTimeFontSize());
        views.setTextViewTextSize(R.id.widget_content, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getContentFontSize());
        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent1);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: Start");
        //设置挂件字体大小
        NoteAdapter.setTitleFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_title_size", "30")));
        NoteAdapter.setTimeFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_time_size", "16")));
        NoteAdapter.setContentFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_content_size", "24")));
        dbHelper = new DatabaseHelper(context, "Note.db", null, 11);//版本需要一致
        boolean isDebug = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_preference_is_debug", false);
        MainActivity.setIsDebug(isDebug);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        updateWidgetInfoList(db);
        Log.d(TAG, "onUpdate: " + isDebug);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //todo 添加到widget表
            if (isDebug) {
                Toast.makeText(context, "appWidgetId:" + appWidgetId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    void updateWidgetInfoList(SQLiteDatabase db) {
        widgetInfoList.clear();//清空表
        Cursor cursor = db.query("widget", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                int widgetID = cursor.getInt(cursor.getColumnIndex("widgetID"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                if (isDeleted == 0) {
//                    widgetInfoList.add(new WidgetInfo(time, widgetID));
                    widgetInfoList.add(new WidgetInfo(time, widgetID, Aid.querySQLNote(dbHelper, time)));
//                    widgetInfoList.add(Aid.querySQLWidget(dbHelper, time));
//                    noteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: Start");
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled: Start");
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {//这里的appWidgetIds是删除的id数组
        Log.d(TAG, "onDeleted: Start");//todo 删除
//        for (int appWidgetID : appWidgetIds) {
//            Toast.makeText(context, "删除的是ID" + appWidgetID, Toast.LENGTH_SHORT).show();
//        }
        if (MainActivity.getIsDebug()) {
            Toast.makeText(context, "删除的是ID" + appWidgetIds[0], Toast.LENGTH_SHORT).show();
        }
        dbHelper = new DatabaseHelper(context, "Note.db", null, 11);//版本需要一致
        Aid.deleteSQLWidget(dbHelper, appWidgetIds[0]);
        updateWidgetInfoList(dbHelper.getWritableDatabase());
        int pos = 0;
        for (int i = 0; i < widgetInfoList.size(); i++) {
            if (widgetInfoList.get(i).getAppWidgetID() == appWidgetIds[0]) {
                pos = i;
            }
        }
        widgetInfoList.remove(pos);
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        if (MainActivity.getIsDebug()) {
            Toast.makeText(context, "改变大小id：" + appWidgetId, Toast.LENGTH_SHORT).show();
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}

