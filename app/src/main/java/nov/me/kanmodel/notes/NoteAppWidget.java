package nov.me.kanmodel.notes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class NoteAppWidget extends AppWidgetProvider {
    private static final String TAG = "NoteAppWidget";
    private static DatabaseHelper dbHelper;
    private List<WidgetInfo> widgetInfoList= new ArrayList<>();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_app_widget);
//        CharSequence widgetTitle = context.getString(R.string.appwidget_text);
        if (NoteAdapter.getNotes() != null) {
            Note note = NoteAdapter.getNotes().get(Aid.pos);
            String widgetTitle = note.getTitle();
            String time = note.getLogTime();
            String content = note.getContent();
            views.setTextViewText(R.id.widget_title, widgetTitle);
            views.setTextViewText(R.id.widget_time, time);
            views.setTextViewText(R.id.widget_content, content);
        } else {
            if (MainActivity.getIsDebug()) {
                Toast.makeText(context, "开机Debug", Toast.LENGTH_SHORT).show();
            }
            List<Note> noteList = Aid.initNotes(dbHelper);
            Note note = noteList.get(0);
            if (note != null) {
                String widgetTitle = note.getTitle();
                String time = note.getLogTime();
                String content = note.getContent();
                views.setTextViewText(R.id.widget_title, widgetTitle);
                views.setTextViewText(R.id.widget_time, time);
                views.setTextViewText(R.id.widget_content, content);
            }
        }
        Intent intent1 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent1);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: Start");

        dbHelper = new DatabaseHelper(context, "Note.db", null, 11);//版本需要一致
        boolean isDebug = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_preference_is_debug", false);
        MainActivity.setIsDebug(isDebug);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("widget", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                /*获取数据库数据*/
                int widgetID = cursor.getInt(cursor.getColumnIndex("widgetID"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                if (isDeleted == 0) {
                    widgetInfoList.add(new WidgetInfo(time, widgetID));
//                    noteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
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
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted: Start");
        super.onDeleted(context, appWidgetIds);
    }

}

