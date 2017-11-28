package nov.me.kanmodel.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    public EditText titleET;
    public TextView timeTV;
    public EditText contentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.d(TAG, "onCreate: start");
        titleET = findViewById(R.id.editor_title);
        timeTV = findViewById(R.id.editor_time);
        contentET = findViewById(R.id.editor_content);
        Intent intent = getIntent();
        titleET.setText(intent.getStringExtra("title"));
        timeTV.setText(intent.getStringExtra("time"));
        contentET.setText(intent.getStringExtra("content"));
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: start");
        Intent intent = new Intent();
        intent.putExtra("data_return", contentET.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
