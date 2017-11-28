package nov.me.kanmodel.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    public EditText titleET;
    public TextView timeTV;
    public EditText contentET;
    public long time;
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
    }

    @Override
    public void onBackPressed() {
        String title = titleET.getText().toString();
//        Long time = Long.parseLong(timeTV.getText().toString());
        String content = contentET.getText().toString();
        Log.d(TAG, "onBackPressed: [title: " + title + " | content: " + content + "]");
        Intent intent = new Intent();
        int pos = parentIntent.getIntExtra("pos", 0);
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        intent.putExtra("data_return", contentET.getText().toString());
        NoteAdapter.getNotes().get(pos).setTitle(title);
        NoteAdapter.getNotes().get(pos).setContent(content);
        MainActivity.getNoteAdapter().refreshData(pos);
        setResult(RESULT_OK, intent);
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
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home");
                int pos = parentIntent.getIntExtra("pos", 0);
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();
                SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);
                db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
                NoteAdapter.getNotes().get(pos).setTitle(title);
                NoteAdapter.getNotes().get(pos).setContent(content);
                MainActivity.getNoteAdapter().refreshData(pos);
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
