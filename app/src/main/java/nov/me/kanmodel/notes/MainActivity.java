package nov.me.kanmodel.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public Button startEditActivityButton;
    public RecyclerView recycler;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static DatabaseHelper dbHelper;
    private List<Note> noteList = new ArrayList<>();
    private static NoteAdapter noteAdapter;

    private String text;

    public android.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*使用SharedPreference存储数据*/
//        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        pref = getSharedPreferences("data", MODE_PRIVATE);
        text = pref.getString("text", "Nothing");
        Log.d(TAG, "onCreate: " + text);

        /*sql数据库*/
        dbHelper = new DatabaseHelper(this, "Note.db", null, 9);
        initNodes();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);
//        recyclerView.addItemDecoration();
        //todo 分割线

        /*组件初始化*/
        actionBar = getActionBar();
        startEditActivityButton = findViewById(R.id.startEditbtn);
        recycler = findViewById(R.id.recycler_view);
        startEditActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = pref.getString("text", "Nothing");
                Intent intent = new Intent("nov.me.kanmodel.notes.EditActivity");
                intent.putExtra("content", data);
//                startActivity(intent);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initNodes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int isDeleted = cursor.getInt(cursor.getColumnIndex("isDeleted"));
                String logtime = cursor.getString(cursor.getColumnIndex("logtime"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                Log.d(TAG, "onOptionsItemSelected: id:" + id + "\ntitle:" + title + "\ncontent:"
                        + content + "\nlogtime:" + logtime + "\ntime:" + time + "\nisDeleted:" + isDeleted);
//                noteList.add(new Note(title, content, logtime, time));
                noteList.add(0, new Note(title, content, logtime, time));//数据库按ID顺序倒序排列
            } while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (item.getItemId()) {
            case R.id.add_note:
                noteAdapter.addData(Aid.addNote(dbHelper, "", "新建便签"), 0);//todo 添加到第一个没有动画
                Toast.makeText(this, "新建便签", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_note:
                //todo 删除功能
                Toast.makeText(this, "Remove", Toast.LENGTH_SHORT).show();
                break;
            case R.id.list_database:
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
                break;
            case R.id.update_database:
                noteAdapter.addData(Aid.addNote(dbHelper, "", "新建标签"), 0);//todo 添加到第一个没有动画
                Toast.makeText(this, "update_database", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: start");
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnedData = data.getStringExtra("data_return");
                    Log.d(TAG, "onActivityResult: " + returnedData);
                    editor = pref.edit();
                    editor.putString("text", returnedData);
                    editor.apply();
                }
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public static NoteAdapter getNoteAdapter() {
        return noteAdapter;
    }
}
