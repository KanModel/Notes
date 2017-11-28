package nov.me.kanmodel.notes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by kgdwhsk on 2017/11/26.
 */

public class Aid {

    private static final String TAG = "AidClass";
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static Note addNote(DatabaseHelper dbHelper){
        return addNote(dbHelper, "content", "title");
    }

    public static void addNote(DatabaseHelper dbHelper, Note note){
        addNote(dbHelper, note.getContent(), note.getTitle());
    }

    public static Note addNote(DatabaseHelper dbHelper, String content, String title){
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
}
