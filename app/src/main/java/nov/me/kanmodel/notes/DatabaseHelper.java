package nov.me.kanmodel.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * 用于数据库创建
 * Created by kgdwhsk on 2017/11/26.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    /**
     * 构造方法
     * @param context Context类
     * @param name 数据库文件名字
     * @param factory 不知道
     * @param version 数据库，增加自动调用onUpgrade更新数据库
     */
    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * 创建表调用该方法
     * @param sqLiteDatabase sql类
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Note(" +
                "'id' integer primary key autoincrement, " +
                "'content' text, " +
                "'title' text, " +
                "'isDeleted' int default 0, " +
                "'logtime' timestamp default CURRENT_TIMESTAMP, " +
                "'time' integer)");
        Toast.makeText(mContext, "创建数据库成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新数据库用方法
     * @param sqLiteDatabase sql类
     * @param i 未知
     * @param i1 未知
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Note");
        onCreate(sqLiteDatabase);
    }
}
