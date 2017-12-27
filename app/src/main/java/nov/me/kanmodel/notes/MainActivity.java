package nov.me.kanmodel.notes;

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
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.ui.IntroActivity;
import nov.me.kanmodel.notes.utils.Aid;
import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.utils.RecyclerViewClickListener;
import nov.me.kanmodel.notes.utils.WrapContentLinearLayoutManager;
import nov.me.kanmodel.notes.utils.PreferenceManager;

/**
 * 主要Activity
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static SwipeMenuRecyclerView recyclerView;

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = 400;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
//                    .setBackgroundDrawable(R.drawable.selector_green)// 点击的背景。
                    .setImage(R.drawable.ic_launcher_foreground) // 图标。
                    .setWidth(width) // 宽度。
                    .setHeight(height); // 高度。
            swipeLeftMenu.addMenuItem(addItem); // 添加一个按钮到左侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                    .setText("删除") // 文字。
                    .setBackgroundColor(Color.RED)
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。.

            // 上面的菜单哪边不要菜单就不要添加。
        }
    };

    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            return false;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int adapterPosition = srcHolder.getAdapterPosition();
            // Item被侧滑删除时，删除数据，并更新adapter。
            long time = NoteAdapter.getNotes().get(adapterPosition).getTime();
            Aid.deleteSQLNote(time);
            noteAdapter.removeData(adapterPosition);
            checkEmpty();
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
    private PreferenceManager preferences;
    private static LinearLayout emptyView;
    private static TextView emptyTV;

    static boolean isDebug = false;
    int REQUEST_CODE_INTRO = 1001;

    public android.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();

        initRecyclerView();
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
                long lastChangedTime = cursor.getLong(cursor.getColumnIndex("lastChangedTime"));
                if (isDebug) {
                    Log.d(TAG, "onOptionsItemSelected: id:" + id + "\ntitle:" + title + "\ncontent:"
                            + content + "\nlogtime:" + logtime + "\ntime:" + time + "\nisDeleted:" + isDeleted);
                }
                if (isDeleted == 0) {
                    noteList.add(0, new Note(title, content, logtime, time, lastChangedTime));//数据库按ID顺序倒序排列
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void initComponent() {
        /*组件初始化*/
        actionBar = getActionBar();
        preferences = new PreferenceManager(this.getApplicationContext());
        emptyView = findViewById(R.id.empty_view);
        emptyTV = findViewById(R.id.empty_view_text);
        emptyTV.setTypeface(Aid.getFontAwesome(getApplicationContext()));

        /*判断是否是debug模式*/
        isDebug = preferences.getDebug();
        Log.d(TAG, "onCreate: isDebug " + isDebug);
        if (isDebug) {
            Toast.makeText(this, "isDebug:" + isDebug + "\n当前版本名称:" + Aid.getVersionName(this) +
                    "\n当前版本号" + Aid.getVersionCode(this), Toast.LENGTH_SHORT).show();
            setTitle(getResources().getString(R.string.app_name) + "[Debug模式]");
            Log.d(TAG, "onCreate: DatabaseDir: " + getDatabasePath("Note.db").getAbsolutePath());
        }
//        if (preferences.isFirstLaunch()) {
        if (isDebug || preferences.isFirstLaunch()) {
            startActivityForResult(new Intent(this, IntroActivity.class), REQUEST_CODE_INTRO);
            Log.d(TAG, "initComponent: IntroActivity");
//            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        }
    }

    /**
     * 初始画RecyclerView
     */
    private void initRecyclerView() {
        /*设置RecyclerView内容字体大小*/
        NoteAdapter.setTitleFontSize(preferences.getFontTitleSize());
        NoteAdapter.setTimeFontSize(preferences.getFontTimeSize());
        NoteAdapter.setContentFontSize(preferences.getFontContextSize());

        /*sql数据库*/
        dbHelper = new DatabaseHelper(this, "Note.db", null, 11);
        initNodes();

        /*RecyclerView初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));//瀑布流
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
//        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        recyclerView.setOnItemMoveListener(mItemMoveListener);
        recyclerView.setItemViewSwipeEnabled(true);
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);//设置Note集合
        Log.d(TAG, "initRecyclerView: length : " + noteAdapter.getItemCount());
//        recyclerView.addItemDecoration(new NoteDecoration(this, NoteDecoration.VERTICAL_LIST));//todo 分割线与动画联动不美观
//        recyclerView.addItemDecoration(new DefaultItemDecoration(Color.BLUE, 5, 5));
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(this, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Note note = noteList.get(position);
                Log.d(TAG, "onClick: Content:" + note.getContent() + "\nTitle:" +
                        note.getTitle() + "\nTime:" + note.getLogTime() + "\nPos:" + position);
                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
                intent.putExtra("pos", position);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("content", note.getContent());
                intent.putExtra("time", Aid.stampToDate(note.getTime()));
                intent.putExtra("timeLong", note.getTime());
                intent.putExtra("lastChangedTime", note.getLastChangedTime());
                view.getContext().startActivity(intent);
                if (isDebug) {
                    Toast.makeText(MainActivity.this, "Click " + noteList.get(position).getContent(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Long Click " + noteList.get(position), Toast.LENGTH_SHORT).show();
            }
        }));
        checkEmpty();
    }

    public static void checkEmpty(){
        if (noteAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INTRO) {
            preferences.setFirstLaunch(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        if (isDebug) {
            setTitle(getResources().getString(R.string.app_name) + "[Debug模式]");
        } else {
            setTitle(getResources().getString(R.string.app_name));
        }
//        noteAdapter.notifyDataSetChanged();
        checkEmpty();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDebug) {
            menu.setGroupVisible(R.id.main_menu_debug, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 通过反射使图标与文字同时显示
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
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
                long timeStamp = Aid.getNowTime();
                intent.putExtra("time", Aid.stampToDate(timeStamp));
                intent.putExtra("timeLong", timeStamp);
                intent.putExtra("isNew", true);
                intent.putExtra("lastChangedTime", timeStamp);
                startActivity(intent);
//                noteAdapter.addData(Aid.addSQLNote(dbHelper, "", "新建便签"));
//                recyclerView.scrollToPosition(0);//移动到顶端
                return true;
            case R.id.main_menu_add:
//                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//                progressDialog.setTitle("保存您的更改");
//                progressDialog.setMessage("正在保存...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(1000);//让他显示10秒后，取消ProgressDialog
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        progressDialog.dismiss();
//                    }
//                });
//                t.start();
                /*测试用添加三个便签数据*/
                noteAdapter.addData(Aid.addSQLNote(dbHelper, "近日，日本汽车巨头尼桑（国内称为日产）宣布将在北美地区的展厅演示增强现实（AR）体验，旨在向客户介绍旗下汽车的安全性以及驾驶辅助系统的相关技术。\n" +
                        "这个AR体验被命名为“见所未见（See the Unseen）”，将在即将上映的电影《星球大战8：最后的绝地武士》之前，也就是12月初发布。该体验将包含来自《星球大战》宇宙中的多个角色，比如风暴突击队和克隆人军团等。", "汽车巨头尼桑推出全新《星球大战》AR体验"));
                noteAdapter.addData(Aid.addSQLNote(dbHelper, "做父母的，辛辛苦苦一辈子，最大的心愿是子女幸福健康有出息。所以，只要是给子女未来铺路的事，尤其是教育方面的投入，绝大多数父母都愿意砸钱。", "\"女儿\"要到哈佛大学当交换生 父亲看完短信汇23万"));
                noteAdapter.addData(Aid.addSQLNote(dbHelper, "这个网络流行的meme也有了成真的一天。据福克斯新闻报道，美国俄亥俄州辛辛那提，一名13岁的熊孩子看到家里有只虫子，于是掏出打火机想要放火灭虫。不想不慎将整栋公寓都引燃，造成8人流离失所，造成至少30万美元的经济损失。\n" +
                        "周二晚上11点，辛辛那提市一公寓突然发生火花爆炸，在消防员赶到之前，火焰已经蔓延了6间屋子。\n" +
                        "消防员紧急撤离了楼内住户，并开始灭火。火势很快扑灭，万幸没有人员伤亡，但房屋已被烧毁，三名成年人和五名儿童流离失所。", "13岁熊孩子为了灭虫把整栋楼都烧了 网友：值了"));
                recyclerView.scrollToPosition(0);//移动到顶端
                checkEmpty();
                return true;
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
                        checkEmpty();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
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
                return true;
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
                checkEmpty();
                return true;
            case R.id.main_menu_setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.main_menu_about:
                /*启动关于应用*/
                return true;
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

    public static boolean getIsDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean isDebug) {
        MainActivity.isDebug = isDebug;
    }
}
