package nov.me.kanmodel.notes.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.model.WidgetInfo;
import nov.me.kanmodel.notes.utils.DBAid;
import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.activity.MainActivity;
import nov.me.kanmodel.notes.model.Note;
import nov.me.kanmodel.notes.activity.adapter.NoteAdapter;
import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.utils.TimeAid;

/**
 * Implementation of App Widget functionality.
 */
public class NoteAppWidget extends AppWidgetProvider {
    private static final String TAG = "NoteAppWidget";
    private static DatabaseHelper dbHelper;
    private static Context mContext;
    private static List<WidgetInfo> widgetInfoList = new ArrayList<>();
    private static List<Note> notes = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, final int appWidgetId) {
        Note note = null;
        for (WidgetInfo widgetInfo : widgetInfoList) {//遍历表寻找对应id的挂件
            if (widgetInfo.getAppWidgetID() == appWidgetId) {
                note = widgetInfo.getNote();
            }
        }
        if (note == null) {//数据库中没有相关信息进行添加
            try {
                notes = DBAid.initNotes(dbHelper);
                long time = notes.get(DBAid.pos).getTime();
                DBAid.addSQLWidget(dbHelper, time, appWidgetId);
                note = DBAid.querySQLNote(dbHelper, time);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                updateWidgetInfoList(db);//添加后刷新表
                db.close();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            if (note == null) {
                note = new Note("此便签可能以删除,请您手动删除", "", TimeAid.INSTANCE.getNowTime());
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, getRemoteView(context, note.getTime(), note.getTitle(), note.getContent()));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static RemoteViews getRemoteView(Context context, long time, String widgetTitle, String strTime, String content) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_app_widget);
        long dstTime = DBAid.querySQLNotice(DBAid.getDbHelper(context), time);
        if (dstTime > 0 && (dstTime - TimeAid.INSTANCE.getNowTime()) > 0) {
            views.setViewVisibility(R.id.widget_dis, View.VISIBLE);
            long day = TimeAid.INSTANCE.getDiffDay(dstTime);
            long hour = TimeAid.INSTANCE.getDiffHour(dstTime);
            long minute = TimeAid.INSTANCE.getDiffMinutes(dstTime);
            if (day > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + day + " 天");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                views.setTextViewText(R.id.widget_dis, spannableString);
            } else if (hour > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + hour + " 小时");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                views.setTextViewText(R.id.widget_dis, spannableString);
            } else if (minute > 0) {
                SpannableString spannableString = new SpannableString("剩余 " + minute + " 分钟");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE5ADFF"));
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.4f);
                spannableString.setSpan(sizeSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, 3, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                views.setTextViewText(R.id.widget_dis, spannableString);
            } else {
                views.setViewVisibility(R.id.widget_dis, View.GONE);
            }
        }

        views.setTextViewText(R.id.widget_title, widgetTitle);
        views.setTextViewText(R.id.widget_time, strTime);
        views.setTextViewText(R.id.widget_content, content);
        //设置挂件字体大小
        views.setTextViewTextSize(R.id.widget_title, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getTitleFontSize());
        views.setTextViewTextSize(R.id.widget_time, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getTimeFontSize());
        views.setTextViewTextSize(R.id.widget_content, TypedValue.COMPLEX_UNIT_SP, NoteAdapter.getContentFontSize());
        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_title, openAppPendingIntent);

        return views;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static RemoteViews getRemoteView(Context context, long time, String title, String content) {
        return getRemoteView(context, time, title, TimeAid.INSTANCE.stampToDate(time), content);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static RemoteViews getRemoteView(Context context, long time) {
        Note note = DBAid.querySQLNote(DBAid.getDbHelper(context), time);
        return getRemoteView(context, time, note.getTitle(), note.getContent());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void updateWidget(Context context, long time, String title, String content) {
        try {
            AppWidgetManager.getInstance(context).updateAppWidget(DBAid.querySQLWidget(context, time).getAppWidgetID()
                    , NoteAppWidget.getRemoteView(context, time, title, content));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void updateAllWidget() {
        for (WidgetInfo info : widgetInfoList) {
            Note note = DBAid.querySQLNote(DBAid.getDbHelper(mContext), info.getTime());
            updateWidget(mContext, info.getTime(), note.getTitle(), note.getContent());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: Start");
        mContext = context;
        Intent intent = new Intent(context, UpdateWidgetService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "onUpdate: startForegroundService");
            context.startForegroundService(intent);
        } else {
            Log.d(TAG, "onUpdate: startService");
            context.startService(intent);
        }
        //设置挂件字体大小
        NoteAdapter.setTitleFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_title_size", "20")));
        NoteAdapter.setTimeFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_time_size", "16")));
        NoteAdapter.setContentFontSize(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("font_content_size", "18")));
        dbHelper = DBAid.getDbHelper(context);//版本需要一致
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
        db.close();
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
                    widgetInfoList.add(new WidgetInfo(time, widgetID, DBAid.querySQLNote(dbHelper, time)));
//                    widgetInfoList.add(DBAid.querySQLWidget(dbHelper, time));
//                    noteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: Start");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled: Start");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {//这里的appWidgetIds是删除的id数组
        Log.d(TAG, "onDeleted: Start");//todo 删除
        if (MainActivity.getIsDebug()) {
            Toast.makeText(context, "删除的是ID" + appWidgetIds[0], Toast.LENGTH_SHORT).show();
        }
        dbHelper = DBAid.getDbHelper(context);//版本需要一致
        DBAid.deleteSQLWidget(dbHelper, appWidgetIds[0]);
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

    public static Context getmContext() {
        return mContext;
    }


}

