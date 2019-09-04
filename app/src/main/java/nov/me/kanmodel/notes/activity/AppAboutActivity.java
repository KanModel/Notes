package nov.me.kanmodel.notes.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.utils.Utils;
import nov.me.kanmodel.notes.utils.dbAid;


public class AppAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appabout);
        String versionName = Utils.getVersionName(this);
        TextView textView = findViewById(R.id.about_tv);
        textView.setText(String.format("%s\n版本 :%s", getResources().getString(R.string.app_name)
                , versionName));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
}


