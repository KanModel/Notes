package nov.me.kanmodel.notes.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nov.me.kanmodel.notes.activity.adapter.NoteAdapter;
import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.model.Note;
import nov.me.kanmodel.notes.utils.DBAid;
import nov.me.kanmodel.notes.utils.DatabaseHelper;
import nov.me.kanmodel.notes.utils.RecyclerViewClickListener;
import nov.me.kanmodel.notes.utils.TimeAid;
import nov.me.kanmodel.notes.utils.Utils;
import nov.me.kanmodel.notes.activity.ui.WrapContentLinearLayoutManager;
import nov.me.kanmodel.notes.utils.ConfigManager;

/**
 * 启动Activity
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static SwipeMenuRecyclerView recyclerView;

    private static DatabaseHelper dbHelper;
    private List<Note> noteList = new ArrayList<>();
    private static NoteAdapter noteAdapter;
    private ConfigManager preferences;
    private LinearLayout emptyView;
    private TextView emptyTV;
    private static Context context;

    static boolean isDebug = false;
    int REQUEST_CODE_INTRO = 1001;

    public android.app.ActionBar actionBar;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initLogcat();
        initComponent();
        initRecyclerView();
    }

    /**
     * logcat记录初始化
     */
    private void initLogcat() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File appDirectory = new File(getFilesDir().getAbsolutePath());
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                boolean res = appDirectory.mkdir();
                Log.d(TAG, "initLogcat: mkdir " + appDirectory.getAbsolutePath() + " " + res);
            }

            // create log folder
            if (!logDirectory.exists()) {
                boolean res = logDirectory.mkdir();
                Log.d(TAG, "initLogcat: mkdir" + logDirectory.getAbsolutePath() + " " + res);
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Log.d(TAG, "initLogcat: 记录logcat");
                Runtime.getRuntime().exec("logcat -c");
                Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponent() {
        /*组件初始化*/
        actionBar = getActionBar();
        preferences = new ConfigManager(this.getApplicationContext());
        emptyView = findViewById(R.id.empty_view);
        emptyTV = findViewById(R.id.empty_view_text);
        emptyTV.setTypeface(Utils.INSTANCE.getFontAwesome(getApplicationContext()));

        /*判断是否是debug模式*/
        isDebug = preferences.getDebug();
        Log.d(TAG, "onCreate: isDebug " + isDebug);
        if (isDebug) {
            Toast.makeText(this, "isDebug:" + isDebug + "\n当前版本名称:" + Utils.INSTANCE.getVersionName(this) +
                    "\n当前版本号" + Utils.INSTANCE.getVersionCode(this), Toast.LENGTH_SHORT).show();
            setTitle(getResources().getString(R.string.app_name) + "[Debug模式]");
            Log.d(TAG, "onCreate: DatabaseDir: " + getDatabasePath("Note.db").getAbsolutePath());
        }
        if (isDebug || preferences.isFirstLaunch()) {
            startActivityForResult(new Intent(this, IntroActivity.class), REQUEST_CODE_INTRO);
            Log.d(TAG, "initComponent: IntroActivity");
        }
    }

    /**
     * 初始画RecyclerView
     */
    private void initRecyclerView() {

        /*sql数据库初始化*/
        dbHelper = DBAid.getDbHelper(this);
        noteList = DBAid.findAllNote(dbHelper);

        /*RecyclerView初始化*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnItemMoveListener(mItemMoveListener);
        recyclerView.setItemViewSwipeEnabled(true);
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);//设置Note集合

        /*设置RecyclerView内容字体大小*/
        NoteAdapter.setTitleFontSize(preferences.getFontTitleSize());
        NoteAdapter.setTimeFontSize(preferences.getFontTimeSize());
        NoteAdapter.setContentFontSize(preferences.getFontContextSize());

        Log.d(TAG, "initRecyclerView: length : " + noteAdapter.getItemCount());
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(this, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    Note note = NoteAdapter.getNotes().get(position);
                    Log.d(TAG, "onClick: Content:" + note.getContent() + "\nTitle:" +
                            note.getTitle() + "\nTime:" + note.getLogTime() + "\nPos:" + position);
                    Intent intent = new Intent("nov.me.kanmodel.notes.activity.EditActivity");
                    intent
                            .putExtra("pos", position)
                            .putExtra("title", note.getTitle())
                            .putExtra("content", note.getContent())
                            .putExtra("time", TimeAid.INSTANCE.stampToDate(note.getTime()))
                            .putExtra("timeLong", note.getTime())
                            .putExtra("lastChangedTime", note.getLastChangedTime());
                    view.getContext().startActivity(intent);
                    if (isDebug) {
                        Toast.makeText(MainActivity.this, "Click "
                                + NoteAdapter.getNotes().get(position).getContent(), Toast.LENGTH_SHORT).show();
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "这个便笺好像并不存在哦~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Long Click " + NoteAdapter.getNotes().get(position), Toast.LENGTH_SHORT).show();
            }
        }));
        checkEmpty();
    }

    public void checkEmpty() {
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
        checkEmpty();
        for (Note note : NoteAdapter.getNotes()) {
            Log.d(TAG, "onResume: note " + note.getTitle());
        }
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
        if (isDebug) menu.setGroupVisible(R.id.main_menu_debug, true);//debug模式显示debug菜单组
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
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.recycle_bin:
                startActivity(new Intent(MainActivity.this, RecycleBinActivity.class));
                return true;
            case R.id.add_note:
                /*添加新便签*/
                long timeStamp = TimeAid.INSTANCE.getNowTime();
                Intent intent = new Intent("nov.me.kanmodel.notes.activity.EditActivity");
                intent
                        .putExtra("title", "")
                        .putExtra("content", "")
                        .putExtra("time", TimeAid.INSTANCE.stampToDate(timeStamp))
                        .putExtra("timeLong", timeStamp)
                        .putExtra("isNew", true)
                        .putExtra("lastChangedTime", timeStamp);
                startActivity(intent);
                return true;
            case R.id.main_menu_add:
                /*测试用添加三个便签数据*/
                noteAdapter.addData(DBAid.addSQLNote(dbHelper, "近日，日本汽车巨头尼桑（国内称为日产）宣布将在北美地区的展厅演示增强现实（AR）体验，旨在向客户介绍旗下汽车的安全性以及驾驶辅助系统的相关技术。\n" +
                        "这个AR体验被命名为“见所未见（See the Unseen）”，将在即将上映的电影《星球大战8：最后的绝地武士》之前，也就是12月初发布。该体验将包含来自《星球大战》宇宙中的多个角色，比如风暴突击队和克隆人军团等。", "汽车巨头尼桑推出全新《星球大战》AR体验"));
                noteAdapter.addData(DBAid.addSQLNote(dbHelper, "做父母的，辛辛苦苦一辈子，最大的心愿是子女幸福健康有出息。所以，只要是给子女未来铺路的事，尤其是教育方面的投入，绝大多数父母都愿意砸钱。", "\"女儿\"要到哈佛大学当交换生 父亲看完短信汇23万"));
                noteAdapter.addData(DBAid.addSQLNote(dbHelper, "这个网络流行的meme也有了成真的一天。据福克斯新闻报道，美国俄亥俄州辛辛那提，一名13岁的熊孩子看到家里有只虫子，于是掏出打火机想要放火灭虫。不想不慎将整栋公寓都引燃，造成8人流离失所，造成至少30万美元的经济损失。\n" +
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
                        DBAid.deleteSQLNoteForced();
                        DBAid.findAllNote(dbHelper, NoteAdapter.getNotes());
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
                int size = NoteAdapter.getNotes().size();
                for (Note note : NoteAdapter.getNotes()) {
                    DBAid.deleteSQLNote(note.getTime());
                }
                NoteAdapter.getNotes().clear();
                noteAdapter.refreshAllData(size);
                checkEmpty();
                return true;
            case R.id.main_menu_setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.main_menu_about:
                /*启动关于应用*/
                startActivity(new Intent(MainActivity.this, AppAboutActivity.class));
                return true;
            default:
        }
        db.close();
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
            DBAid.deleteSQLNote(time);
            Toast.makeText(MainActivity.this, "你删除了一条便笺，你可以在回收站中彻底删除或恢复", Toast.LENGTH_SHORT).show();
            noteAdapter.removeData(adapterPosition);
            Log.d(TAG, "onItemDismiss: pos : " + adapterPosition);
            checkEmpty();
        }
    };
}
