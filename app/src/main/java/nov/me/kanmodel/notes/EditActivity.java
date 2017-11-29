package nov.me.kanmodel.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 编辑便签的Activity
 */

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    public EditText titleET;
    public TextView timeTV;
    public EditText contentET;
    public long time;
    boolean isNew;
    private Intent parentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.d(TAG, "onCreate: start");
        titleET = findViewById(R.id.editor_title);
        timeTV = findViewById(R.id.editor_time);
        contentET = findViewById(R.id.editor_content);
        parentIntent = getIntent();
        titleET.setText(parentIntent.getStringExtra("title"));
        timeTV.setText(parentIntent.getStringExtra("time"));
        contentET.setText(parentIntent.getStringExtra("content"));
        time = parentIntent.getLongExtra("timeLong", 0);
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
                MainActivity.getNoteAdapter().addData(Aid.addSQLNote(MainActivity.getDbHelper(), content, title, time));
                MainActivity.getRecyclerView().scrollToPosition(0);
                ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);//todo
                progressDialog.setTitle("保存您的更改");
                progressDialog.setMessage("正在保存...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } else {
            Log.d(TAG, "onBackPressed: [title: " + title + " | content: " + content + "]");
            int pos = parentIntent.getIntExtra("pos", 0);
            Aid.updateSQLNote(title, content, time, pos);
            ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);//todo
            progressDialog.setTitle("保存您的更改");
            progressDialog.setMessage("正在保存...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                        MainActivity.getNoteAdapter().addData(Aid.addSQLNote(MainActivity.getDbHelper(), content, title, time));
                        MainActivity.getRecyclerView().scrollToPosition(0);
                        ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);//todo
                        progressDialog.setTitle("保存您的更改");
                        progressDialog.setMessage("正在保存...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                } else {
                    int pos = parentIntent.getIntExtra("pos", 0);
                    Aid.updateSQLNote(title, content, time, pos);
                    ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);//todo
                    progressDialog.setTitle("保存您的更改");
                    progressDialog.setMessage("正在保存...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
