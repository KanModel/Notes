package nov.me.kanmodel.notes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.utils.PreferenceManager;
import nov.me.kanmodel.notes.utils.WrapContentLinearLayoutManager;
import nov.me.kanmodel.notes.utils.dbAid;

/**
 * 便签回收站
 * Created by KanModel on 2017/12/30.
 */

public class RecycleBinActivity extends AppCompatActivity {
    public android.app.ActionBar actionBar;
    private List<Note> binNoteList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private PreferenceManager preferences;
    private static BinNoteAdapter binNoteAdapter;
    public static SwipeMenuRecyclerView recyclerView;
    private static final String TAG = "RecycleBinActivity";

    private boolean isDebug = MainActivity.getIsDebug();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();

        initBinRecyclerView();
    }

    private void initComponent() {
        /*组件初始化*/
        actionBar = getActionBar();
        dbHelper = dbAid.getDbHelper(this);
        preferences = new PreferenceManager(this.getApplicationContext());
    }

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
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDeleted == 1) {
                    binNoteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void initBinRecyclerView() {
        /*设置RecyclerView内容字体大小*/
        BinNoteAdapter.setTitleFontSize(preferences.getFontTitleSize());
        BinNoteAdapter.setTimeFontSize(preferences.getFontTimeSize());
        BinNoteAdapter.setContentFontSize(preferences.getFontContextSize());

        /*sql数据库初始化*/
        dbHelper = dbAid.getDbHelper(this);
        initNodes();

        /*RecyclerView初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));//瀑布流
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*使用菜单*/
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        /*使用侧滑删除*/
//        recyclerView.setOnItemMoveListener(mItemMoveListener);
//        recyclerView.setItemViewSwipeEnabled(true);
        binNoteAdapter = new BinNoteAdapter(binNoteList);
        recyclerView.setAdapter(binNoteAdapter);//设置Note集合
        Log.d(TAG, "initRecyclerView: length : " + binNoteAdapter.getItemCount());

    }

    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            return false;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int adapterPosition = srcHolder.getAdapterPosition();
            // Item被侧滑删除时，删除数据，并更新adapter。
            long time = BinNoteAdapter.getNotes().get(adapterPosition).getTime();
            dbAid.setSQLNote(time, 0);
            binNoteAdapter.removeData(adapterPosition);
        }
    };

    /**
     * 菜单创建器。左滑菜单依次为恢复、删除。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = 400;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(RecycleBinActivity.this)
                    .setText("恢复") // 文字。
                    .setBackgroundColor(Color.GREEN)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            SwipeMenuItem deleteItem = new SwipeMenuItem(RecycleBinActivity.this)
                    .setText("删除") // 文字。
                    .setBackgroundColor(Color.RED)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.

            // 上面的菜单哪边不要菜单就不要添加。
        }
    };

    /**
     * 菜单监听器
     */
    SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
//            Toast.makeText(RecycleBinActivity.this, "删除POS" + adapterPosition, Toast.LENGTH_SHORT).show();
            long time = binNoteList.get(adapterPosition).getTime();
            Log.d(TAG, "onItemClick: menuPosition :" + menuPosition);
            switch (menuPosition) {
                case 0:
                    if (isDebug) Toast.makeText(RecycleBinActivity.this, "恢复 Pos" + adapterPosition, Toast.LENGTH_SHORT).show();
                    dbAid.setSQLNote(time, 0);
                    break;
                case 1:
                    if (isDebug) Toast.makeText(RecycleBinActivity.this, "从数据库上删除 Pos" + adapterPosition, Toast.LENGTH_SHORT).show();
                    dbAid.deleteSQLNoteForced(time);
                    dbAid.setSQLNoticeDone(getApplicationContext(), time, 1);
                    break;
            }
//            dbAid.deleteSQLNote(time);
//            binNoteAdapter.removeData(adapterPosition);
            binNoteAdapter.removeData(adapterPosition);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
