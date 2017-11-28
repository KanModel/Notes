package nov.me.kanmodel.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KanModel on 2017/11/26.
 */

public class Aid {

    private static final String TAG = "AidClass";

    static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    static String stampToDate(long time) {
        return stampToDate(String.valueOf(time));
    }

    public static Note addSQLNote(DatabaseHelper dbHelper) {
        return addSQLNote(dbHelper, "content", "title");
    }

    public static void addSQLNote(DatabaseHelper dbHelper, Note note) {
        addSQLNote(dbHelper, note.getContent(), note.getTitle());
    }

    static Note addSQLNote(DatabaseHelper dbHelper, String content, String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Note note;
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("title", title);
        long timeStamp = new Date().getTime();
        Log.d(TAG, "onOptionsItemSelected: " + timeStamp);
        values.put("time", timeStamp);
        db.insert("Note", null, values);
        //获取数据库最后一条信息
        Cursor cursor1 = db.rawQuery("select * from Note", null);
        if (cursor1.moveToLast()) {
            int id = cursor1.getInt(cursor1.getColumnIndex("id"));
            int isDeleted = cursor1.getInt(cursor1.getColumnIndex("isDeleted"));
            String logtime = cursor1.getString(cursor1.getColumnIndex("logtime"));
            long time = cursor1.getLong(cursor1.getColumnIndex("time"));
//                    noteList.add(new Note(title, content, logtime, time));
            note = new Note(title, content, logtime, time);
        } else {
            note = null;
        }
        return note;
    }

    /**
     * @param title 标题
     * @param content 内容
     * @param time 时间戳
     * @param pos 在RecyclerView中的位置
     */
    static void noteSQLUpdate(String title, String content, Long time, int pos) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        NoteAdapter.getNotes().get(pos).setTitle(title);
        NoteAdapter.getNotes().get(pos).setContent(content);
        MainActivity.getNoteAdapter().refreshData(pos);
    }

    static void noteSQLDelete(long time) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
    }

    static void noteSQLDeleteForced() {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        db.delete("Note", "time > ?", new String[]{"0"});
    }
}
