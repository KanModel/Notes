package nov.me.kanmodel.notes.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.activity.adapter.BinNoteAdapter;
import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.model.Note;
import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.utils.ConfigManager;
import nov.me.kanmodel.notes.utils.Utils;
import nov.me.kanmodel.notes.activity.ui.WrapContentLinearLayoutManager;
import nov.me.kanmodel.notes.utils.DBAid;

/**
 * 便签回收站
 * Created by KanModel on 2017/12/30.
 */

public class RecycleBinActivity extends AppCompatActivity {
    public android.app.ActionBar actionBar;
    private List<Note> binNoteList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private ConfigManager preferences;
    private BinNoteAdapter binNoteAdapter;
    private LinearLayout emptyView;
    private TextView emptyTV;
    private TextView emptyDetailsTV;
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
        emptyView = findViewById(R.id.empty_view);
        emptyTV = findViewById(R.id.empty_view_text);
        emptyTV.setText(getResources().getString(R.string.fa_recycle));
        emptyTV.setTypeface(Utils.INSTANCE.getFontAwesome(getApplicationContext()));
        emptyDetailsTV = findViewById(R.id.empty_view_text_details);
        emptyDetailsTV.setText(getResources().getString(R.string.empty_bin_note_text));
        dbHelper = DBAid.getDbHelper(this);
        preferences = new ConfigManager(this.getApplicationContext());
    }

    private void initNodes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Note where isDeleted = 1 order by id asc", null);
        if (cursor.moveToFirst()) {
            do {
                /*遍历获取所有未删除Note*/
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                binNoteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
    }

    private void initBinRecyclerView() {
        /*设置RecyclerView内容字体大小*/
        BinNoteAdapter.setTitleFontSize(preferences.getFontTitleSize());
        BinNoteAdapter.setTimeFontSize(preferences.getFontTimeSize());
        BinNoteAdapter.setContentFontSize(preferences.getFontContextSize());

        /*sql数据库初始化*/
        dbHelper = DBAid.getDbHelper(this);
        initNodes();

        /*RecyclerView 布局初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*使用菜单*/
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        /*Adapter设置*/
        binNoteAdapter = new BinNoteAdapter(binNoteList);
        recyclerView.setAdapter(binNoteAdapter);//设置Note集合
        Log.d(TAG, "initRecyclerView: length : " + binNoteAdapter.getItemCount());
        checkEmpty();
    }

    /**
     * 滑动菜单创建器。左滑菜单依次为恢复、删除。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = 300;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(RecycleBinActivity.this)
                    .setText("恢复") // 文字。
                    .setBackgroundColor(Color.GRAY)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(20) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            SwipeMenuItem deleteItem = new SwipeMenuItem(RecycleBinActivity.this)
                    .setText("删除") // 文字。
                    .setBackgroundColor(Color.RED)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(20) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(addItem);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单
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
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            long time = binNoteList.get(adapterPosition).getTime();
            Log.d(TAG, "onItemClick: menuPosition :" + menuPosition);
            switch (menuPosition) {
                case 0:
                    if (isDebug)
                        Toast.makeText(RecycleBinActivity.this, "恢复 Pos" + adapterPosition, Toast.LENGTH_SHORT).show();
                    DBAid.setSQLNote(time, 0);
                    MainActivity.getNoteAdapter().refreshAllDataForce();
                    break;
                case 1:
                    if (isDebug)
                        Toast.makeText(RecycleBinActivity.this, "从数据库上删除 Pos" + adapterPosition, Toast.LENGTH_SHORT).show();
                    DBAid.deleteSQLNoteForced(time);
                    DBAid.setSQLNoticeDone(getApplicationContext(), time, 1);
                    break;
            }
            binNoteAdapter.removeData(adapterPosition);
            checkEmpty();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void checkEmpty(){
        if (binNoteAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
