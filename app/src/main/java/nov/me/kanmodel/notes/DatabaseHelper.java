package nov.me.kanmodel.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by kgdwhsk on 2017/11/26.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_NOTES = "create table Note(" +
            "'id' integer primary key autoincrement, " +
            "'content' text, " +
            "'time' text)";

    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(CREATE_NOTES);
        sqLiteDatabase.execSQL("create table Note(" +
                "'id' integer primary key autoincrement, " +
                "'content' text, " +
                "'title' text, " +
                "'isDeleted' int default 0, " +
                "'logtime' timestamp default CURRENT_TIMESTAMP, " +
                "'time' integer)");
        Toast.makeText(mContext, "创建数据库成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Note");
        onCreate(sqLiteDatabase);
    }
}
