package nov.me.kanmodel.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 编辑便签的Activity
 */

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    private EditText titleET;
    private TextView timeTV;
    private EditText contentET;
    private long time;
    private long lastChangedTime;
    boolean isNew;
    private Intent parentIntent;
    private String title;
    private String content;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.d(TAG, "onCreate: start " + Aid.getVersionName(this));
        titleET = findViewById(R.id.editor_title);
        timeTV = findViewById(R.id.editor_time);
        contentET = findViewById(R.id.editor_content);
        parentIntent = getIntent();
        title = parentIntent.getStringExtra("title");
        content = parentIntent.getStringExtra("content");
        pos = parentIntent.getIntExtra("pos", 0);
        titleET.setText(title);
        contentET.setText(content);

        time = parentIntent.getLongExtra("timeLong", 0);
        lastChangedTime = parentIntent.getLongExtra("lastChangedTime", 0);
        if (time == lastChangedTime) {
            timeTV.setText(parentIntent.getStringExtra("time"));
        } else {
            timeTV.setText(Aid.stampToDate(time) + " - 最后更改于" + Aid.stampToDate(lastChangedTime));
        }

        isNew = parentIntent.getBooleanExtra("isNew", false);
    }

    @Override
    public void onBackPressed() {
        String title = titleET.getText().toString();
        String content = contentET.getText().toString();
        if (isNew) {
            if (content.equals("")) {
                Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
            } else {
                saveNewNote(title, content);
            }
        } else {
            if (this.content.equals(content) && this.title.equals(title)) {
                if (MainActivity.getIsDebug()){
                    Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                }
            } else {
                saveOriginalNote(title, content);
            }
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*返回按钮*/
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home");
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();
                if (isNew) {
                    if (content.equals("")) {
                        Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
                    } else {
                        saveNewNote(title, content);
                    }
                } else {
                    if (this.content.equals(content) && this.title.equals(title)) {
                        if (MainActivity.getIsDebug()){
                            Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        saveOriginalNote(title, content);
                    }
                }
                finish();
                return true;
            case R.id.add_to_desktop:
                Aid.pos = pos;
                Toast.makeText(this, "添加本便签到桌面", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewNote(String title, String content) {
        lastChangedTime = Aid.getNowTime();
        MainActivity.getNoteAdapter().addData(Aid.addSQLNote(MainActivity.getDbHelper(), content, title, time, lastChangedTime));
        MainActivity.getRecyclerView().scrollToPosition(0);
        ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("保存您的更改");
        progressDialog.setMessage("正在保存...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void saveOriginalNote(String title, String content) {
        lastChangedTime = Aid.getNowTime();
        int pos = parentIntent.getIntExtra("pos", 0);
        Aid.updateSQLNote(title, content, time, pos, lastChangedTime);
        ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("保存您的更改");
        progressDialog.setMessage("正在保存...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
