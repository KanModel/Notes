package nov.me.kanmodel.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import nov.me.kanmodel.notes.utils.Aid;


public class AppAboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appabout);
        String versionName = Aid.getVersionName(this);
        TextView textView = findViewById(R.id.about_tv);
        textView.setText(String.format("%s\n版本 :%s", getResources().getString(R.string.app_name)
                , versionName));
    }
}


