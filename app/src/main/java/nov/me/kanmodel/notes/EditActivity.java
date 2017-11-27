package nov.me.kanmodel.notes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    public EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Log.d(TAG, "onCreate: start");
        editText = findViewById(R.id.editerEditText);
        Intent intent = getIntent();
        String data = intent.getStringExtra("extra_data");
        editText.setText(data);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: start");
        Intent intent = new Intent();
        intent.putExtra("data_return", editText.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
