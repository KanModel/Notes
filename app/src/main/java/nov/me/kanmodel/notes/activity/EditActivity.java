package nov.me.kanmodel.notes.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import nov.me.kanmodel.notes.R;
import nov.me.kanmodel.notes.receiver.AlarmReceiver;
import nov.me.kanmodel.notes.activity.ui.TimeAndDatePickerDialog;
import nov.me.kanmodel.notes.utils.TimeAid;
import nov.me.kanmodel.notes.utils.Utils;
import nov.me.kanmodel.notes.utils.dbAid;
import nov.me.kanmodel.notes.widget.NoteAppWidget;

/**
 * 编辑便签的Activity
 */

public class EditActivity extends AppCompatActivity implements TimeAndDatePickerDialog.TimePickerDialogInterface {
    private static final String TAG = "EditActivity";

    private TimeAndDatePickerDialog dialog;

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

        Log.d(TAG, "onCreate: start " + Utils.getVersionName(this));
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
            timeTV.setText(TimeAid.stampToDate(time) + " - 最后更改于" + TimeAid.stampToDate(lastChangedTime));
        }

        isNew = parentIntent.getBooleanExtra("isNew", false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        String title = titleET.getText().toString();
        String content = contentET.getText().toString();
        if (isNew) {
            if (content.equals("") && title.equals("")) {
                Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
            } else {
                saveNewNote(title, content);
            }
        } else {
            if (this.content.equals(content) && this.title.equals(title)) {
                if (MainActivity.getIsDebug()) {
                    Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                }
            } else {
                saveOriginalNote(title, content);
            }
            NoteAppWidget.updateWidget(this, time, title, content);
            MainActivity.getNoteAdapter().refreshAllDataForce();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isNew) {
            menu.setGroupVisible(R.id.edit_new_group, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*返回按钮*/
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home");
                String title = titleET.getText().toString();
                String content = contentET.getText().toString();
                if (isNew) {
                    if (content.equals("") && title.equals("")) {
                        Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show();
                    } else {
                        saveNewNote(title, content);
                    }
                } else {
                    if (this.content.equals(content) && this.title.equals(title)) {
                        if (MainActivity.getIsDebug()) {
                            Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show();
                        }
//                        MainActivity.getNoteAdapter().refreshData(pos);
                    } else {
                        saveOriginalNote(title, content);
                    }
//                    AppWidgetManager.getInstance(this).updateAppWidget(dbAid.querySQLWidget(this, time).getAppWidgetID()
//                            , NoteAppWidget.getRemoteView(this, time, title, content));
                    MainActivity.getNoteAdapter().refreshAllDataForce();
                    NoteAppWidget.updateWidget(this, time, title, content);
                }
                finish();
                return true;
            case R.id.add_to_desktop:
                dbAid.pos = pos;
                Toast.makeText(this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            case R.id.add_time:
                dialog = new TimeAndDatePickerDialog(this);
                dialog.showDateAndTimePickerDialog();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewNote(String title, String content) {
        lastChangedTime = TimeAid.getNowTime();
        MainActivity.getNoteAdapter().addData(dbAid.addSQLNote(MainActivity.getDbHelper(), content, title, lastChangedTime, lastChangedTime));
        MainActivity.getRecyclerView().scrollToPosition(0);
    }

    private void saveOriginalNote(String title, String content) {
        lastChangedTime = TimeAid.getNowTime();
        int pos = parentIntent.getIntExtra("pos", 0);
        dbAid.updateSQLNote(title, content, time, pos, lastChangedTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void positiveListener() {
//        mTv_getOffWork.setText(hour+":"+minute);
        Log.d(TAG, "positiveListener: year  :" + dialog.getYear());
        Log.d(TAG, "positiveListener: month :" + dialog.getMonth());
        Log.d(TAG, "positiveListener: day   :" + dialog.getDay());
        Log.d(TAG, "positiveListener: hour  :" + dialog.getHour());
        Log.d(TAG, "positiveListener: minute:" + dialog.getMinute());
        String dstStr = String.format(Locale.CHINA, "%d-%d-%d %d:%d:00", dialog.getYear(), dialog.getMonth(), dialog.getDay(), dialog.getHour(), dialog.getMinute());
        long dstTime = TimeAid.dateToStamp(dstStr);
        long nowTime = TimeAid.getNowTime();
        Log.d(TAG, "positiveListener: dstTime:" + dstTime);
        Log.d(TAG, "positiveListener: nowTime:" + nowTime);
        Log.d(TAG, "positiveListener: time   :" + time);
        Log.d(TAG, "positiveListener: STAMP :" + dstTime);
        Log.d(TAG, "positiveListener: diff  :" + TimeAid.getDiff(dstTime, nowTime));
        long dDay = TimeAid.getDiffDay(dstTime, nowTime);
        long dHour = TimeAid.getDiffHour(dstTime, nowTime);
        long dMinute = TimeAid.getDiffMinutes(dstTime, nowTime);
        Log.d(TAG, "positiveListener: diff DAY    : " + dDay);
        Log.d(TAG, "positiveListener: diff Hour   : " + dHour);
        Log.d(TAG, "positiveListener: diff Minutes: " + dMinute);
//        dbAid.addSQLNotice(this, time, dstTime);
//        dbAid.updateSQLNotice(this, time, dstTime);
        title = titleET.getText().toString();
        if (dDay > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dDay + "天后提醒你", Toast.LENGTH_SHORT).show();
        } else if (dHour > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dHour + "小时后提醒你", Toast.LENGTH_SHORT).show();
        } else if (dMinute > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dMinute + "分钟后提醒你", Toast.LENGTH_SHORT).show();
        }
//        AlarmReceiver.setAlarm(this, dDay * 1000 * 60 * 60 * 24 + dHour * 1000 * 60 * 60 + dMinute * 1000 * 60, title);
        AlarmReceiver.setAlarm(this, dDay * 60 * 24 + dHour * 60 + dMinute, title);
        Log.d(TAG, "positiveListener: title" + title);
        dbAid.newSQLNotice(this, time, dstTime);
    }

    @Override
    public void negativeListener() {

    }
}
