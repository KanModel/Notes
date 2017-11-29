package nov.me.kanmodel.notes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nov.me.kanmodel.notes.utils.WrapContentLinearLayoutManager;

/**
 * 主要Activity
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //    public static RecyclerView recyclerView;
    public static SwipeMenuRecyclerView recyclerView;
    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = 200;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                    .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
                    .setImage(R.drawable.ic_launcher_foreground) // 图标。
                    .setWidth(width) // 宽度。
                    .setHeight(height); // 高度。
            swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                    .setText("删除") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.

            // 上面的菜单哪边不要菜单就不要添加。
        }
    };

    SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            Toast.makeText(MainActivity.this, "删除POS" + adapterPosition, Toast.LENGTH_SHORT).show();
            long time = NoteAdapter.getNotes().get(adapterPosition).getTime();
            Aid.deleteSQLNote(time);
            noteAdapter.removeData(adapterPosition);
        }
    };

    private static DatabaseHelper dbHelper;
    private List<Note> noteList = new ArrayList<>();
    private static NoteAdapter noteAdapter;

    public android.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*sql数据库*/
        dbHelper = new DatabaseHelper(this, "Note.db", null, 9);
        initNodes();

        /*RecyclerView初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);//设置Note集合
//        recyclerView.addItemDecoration(new NoteDecoration(this, NoteDecoration.VERTICAL_LIST));//设置分割线
        recyclerView.addItemDecoration(new DefaultItemDecoration(Color.BLUE, 5, 5));

        /*组件初始化*/
        actionBar = getActionBar();
    }

    /**
     * 从数据库获取数据添加到noteList集合中
     */
    private void initNodes() {
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
                Log.d(TAG, "onOptionsItemSelected: id:" + id + "\ntitle:" + title + "\ncontent:"
                        + content + "\nlogtime:" + logtime + "\ntime:" + time + "\nisDeleted:" + isDeleted);
                if (isDeleted == 0) {
                    noteList.add(0, new Note(title, content, logtime, time));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.add_note:
                /*添加新便签*/
                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
//                intent.putExtra("pos", position);
                intent.putExtra("title", "新建便签");
                intent.putExtra("content", "");
                long timeStamp = new Date().getTime();
                intent.putExtra("time", Aid.stampToDate(timeStamp));
                intent.putExtra("timeLong", timeStamp);
                intent.putExtra("isNew", true);
                startActivity(intent);
//                noteAdapter.addData(Aid.addSQLNote(dbHelper, "", "新建便签"));
//                recyclerView.scrollToPosition(0);//移动到顶端
                break;
            case R.id.main_menu_add:
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);//todo
                progressDialog.setTitle("保存您的更改");
                progressDialog.setMessage("正在保存...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);//让他显示10秒后，取消ProgressDialog
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                });
                t.start();
                noteAdapter.addData(Aid.addSQLNote(dbHelper, "", "新建便签"));
                recyclerView.scrollToPosition(0);//移动到顶端
                break;
            case R.id.remove_note:
                /*清空数据库*/
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                /*弹窗再次确认*/
                builder.setTitle("你确定要清空数据库吗？");
                builder.setMessage("一旦确认将无法撤回（此功能仅用于开发者）");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int size = NoteAdapter.getNotes().size();
                        Aid.deleteSQLNoteForced();
                        noteList.clear();
                        initNodes();
                        noteAdapter.refreshAllData(size);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.list_database:
                /*遍历数据库输出到Logcat*/
                Cursor cursor0 = db.query("Note", null, null, null, null, null, null);
                if (cursor0.moveToFirst()) {
                    do {
                        int id = cursor0.getInt(cursor0.getColumnIndex("id"));
                        String content = cursor0.getString(cursor0.getColumnIndex("content"));
                        String title = cursor0.getString(cursor0.getColumnIndex("title"));
                        int isDeleted = cursor0.getInt(cursor0.getColumnIndex("isDeleted"));
                        String logtime = cursor0.getString(cursor0.getColumnIndex("logtime"));
                        long time = cursor0.getLong(cursor0.getColumnIndex("time"));

                        Log.d(TAG, "onOptionsItemSelected: id:" + id + "\ntitle:" + title + "\ncontent:"
                                + content + "\nlogtime:" + logtime + "\ntime:" + time + "\nisDeleted:" + isDeleted);
                    } while (cursor0.moveToNext());
                }
                cursor0.close();
                break;
            case R.id.note_clear:
                /*删除所有便签，清空列表*/
                Cursor cursor1 = db.query("Note", null, null, null, null, null, null);
                int size = NoteAdapter.getNotes().size();
                if (cursor1.moveToFirst()) {
                    do {
                        long time = cursor1.getLong(cursor1.getColumnIndex("time"));
                        Aid.deleteSQLNote(time);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                noteList.clear();
                initNodes();
                noteAdapter.refreshAllData(size);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return 数据库操作类
     */
    public static DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * @return NoteAdapter
     */
    public static NoteAdapter getNoteAdapter() {
        return noteAdapter;
    }

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
