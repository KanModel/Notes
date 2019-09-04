package nov.me.kanmodel.notes.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import java.util.Locale

import nov.me.kanmodel.notes.R
import nov.me.kanmodel.notes.receiver.AlarmReceiver
import nov.me.kanmodel.notes.activity.ui.TimeAndDatePickerDialog
import nov.me.kanmodel.notes.utils.DBAid
import nov.me.kanmodel.notes.utils.TimeAid
import nov.me.kanmodel.notes.utils.Utils
import nov.me.kanmodel.notes.widget.NoteAppWidget

/**
 * 编辑便签的Activity
 */

class EditActivity : AppCompatActivity(), TimeAndDatePickerDialog.TimePickerDialogInterface {

    private var dialog: TimeAndDatePickerDialog? = null

    private var titleET: EditText? = null
    private var timeTV: TextView? = null
    private var contentET: EditText? = null
    private var time: Long = 0
    private var lastChangedTime: Long = 0
    internal var isNew: Boolean = false
    private var parentIntent: Intent? = null
    private var title: String? = null
    private var content: String? = null
    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        Log.d(TAG, "onCreate: start " + Utils.getVersionName(this))
        titleET = findViewById(R.id.editor_title)
        timeTV = findViewById(R.id.editor_time)
        contentET = findViewById(R.id.editor_content)
        parentIntent = intent
        title = parentIntent!!.getStringExtra("title")
        content = parentIntent!!.getStringExtra("content")
        pos = parentIntent!!.getIntExtra("pos", 0)
        titleET!!.setText(title)
        contentET!!.setText(content)

        time = parentIntent!!.getLongExtra("timeLong", 0)
        lastChangedTime = parentIntent!!.getLongExtra("lastChangedTime", 0)
        if (time == lastChangedTime) {
            timeTV!!.text = parentIntent!!.getStringExtra("time")
        } else {
            timeTV!!.text = TimeAid.stampToDate(time) + " - 修改于" + TimeAid.stampToDate(lastChangedTime)
        }

        isNew = parentIntent!!.getBooleanExtra("isNew", false)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        val title = titleET!!.text.toString()
        val content = contentET!!.text.toString()
        if (isNew) {
            if (content == "" && title == "") {
                Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show()
            } else {
                saveNewNote(title, content)
            }
        } else {
            if (this.content == content && this.title == title) {
                if (MainActivity.getIsDebug()) {
                    Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show()
                }
            } else {
                saveOriginalNote(title, content)
            }
            NoteAppWidget.updateWidget(this, time, title, content)
            MainActivity.getNoteAdapter().refreshAllDataForce()
        }
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        /*指定菜单布局文件*/
        inflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (isNew) {
            menu.setGroupVisible(R.id.edit_new_group, false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /*返回按钮*/
            android.R.id.home -> {
                Log.d(TAG, "onOptionsItemSelected: home")
                val title = titleET!!.text.toString()
                val content = contentET!!.text.toString()
                if (isNew) {
                    if (content == "" && title == "") {
                        Toast.makeText(this, "空便签不会被保存", Toast.LENGTH_SHORT).show()
                    } else {
                        saveNewNote(title, content)
                    }
                } else {
                    if (this.content == content && this.title == title) {
                        if (MainActivity.getIsDebug()) {
                            Toast.makeText(this, "未改变便签不保存", Toast.LENGTH_SHORT).show()
                        }
                        //                        MainActivity.getNoteAdapter().refreshData(pos);
                    } else {
                        saveOriginalNote(title, content)
                    }
                    //                    AppWidgetManager.getInstance(this).updateAppWidget(DBAid.querySQLWidget(this, time).getAppWidgetID()
                    //                            , NoteAppWidget.getRemoteView(this, time, title, content));
                    MainActivity.getNoteAdapter().refreshAllDataForce()
                    NoteAppWidget.updateWidget(this, time, title, content)
                }
                finish()
                return true
            }
            R.id.add_to_desktop -> {
                DBAid.pos = pos
                Toast.makeText(this, "添加本便签到桌面\n长按桌面选择本应用挂件拖出即可", Toast.LENGTH_SHORT).show()
                val home = Intent(Intent.ACTION_MAIN)
                home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                home.addCategory(Intent.CATEGORY_HOME)
                startActivity(home)
                return true
            }
            R.id.add_time -> {
                dialog = TimeAndDatePickerDialog(this)
                dialog!!.showDateAndTimePickerDialog()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNewNote(title: String, content: String) {
        lastChangedTime = TimeAid.nowTime
        MainActivity.getNoteAdapter().addData(DBAid.addSQLNote(MainActivity.getDbHelper(), content, title, lastChangedTime, lastChangedTime))
        MainActivity.getRecyclerView().scrollToPosition(0)
    }

    private fun saveOriginalNote(title: String, content: String) {
        lastChangedTime = TimeAid.nowTime
        val pos = parentIntent!!.getIntExtra("pos", 0)
        DBAid.updateSQLNote(title, content, time, pos, lastChangedTime)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun positiveListener() {
        //        mTv_getOffWork.setText(hour+":"+minute);
        Log.d(TAG, "positiveListener: year  :" + dialog!!.year)
        Log.d(TAG, "positiveListener: month :" + dialog!!.month)
        Log.d(TAG, "positiveListener: day   :" + dialog!!.day)
        Log.d(TAG, "positiveListener: hour  :" + dialog!!.hour)
        Log.d(TAG, "positiveListener: minute:" + dialog!!.minute)
        val dstStr = String.format(Locale.CHINA, "%d-%d-%d %d:%d:00", dialog!!.year, dialog!!.month, dialog!!.day, dialog!!.hour, dialog!!.minute)
        val dstTime = TimeAid.dateToStamp(dstStr)
        val nowTime = TimeAid.nowTime
        Log.d(TAG, "positiveListener: dstTime:$dstTime")
        Log.d(TAG, "positiveListener: nowTime:$nowTime")
        Log.d(TAG, "positiveListener: time   :$time")
        Log.d(TAG, "positiveListener: STAMP :$dstTime")
        Log.d(TAG, "positiveListener: diff  :" + TimeAid.getDiff(dstTime, nowTime))
        val dDay = TimeAid.getDiffDay(dstTime, nowTime)
        val dHour = TimeAid.getDiffHour(dstTime, nowTime)
        val dMinute = TimeAid.getDiffMinutes(dstTime, nowTime)
        Log.d(TAG, "positiveListener: diff DAY    : $dDay")
        Log.d(TAG, "positiveListener: diff Hour   : $dHour")
        Log.d(TAG, "positiveListener: diff Minutes: $dMinute")
        //        DBAid.addSQLNotice(this, time, dstTime);
        //        DBAid.updateSQLNotice(this, time, dstTime);
        title = titleET!!.text.toString()
        if (dDay > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dDay + "天后提醒你", Toast.LENGTH_SHORT).show()
        } else if (dHour > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dHour + "小时后提醒你", Toast.LENGTH_SHORT).show()
        } else if (dMinute > 0) {
            Toast.makeText(this, "你设定了提醒时间 :" + dstStr
                    + "\n将于" + dMinute + "分钟后提醒你", Toast.LENGTH_SHORT).show()
        }
        //        AlarmReceiver.setAlarm(this, dDay * 1000 * 60 * 60 * 24 + dHour * 1000 * 60 * 60 + dMinute * 1000 * 60, title);
        AlarmReceiver.setAlarm(this, dDay * 60 * 24 + dHour * 60 + dMinute, title)
        Log.d(TAG, "positiveListener: title" + title!!)
        DBAid.newSQLNotice(this, time, dstTime)
    }

    override fun negativeListener() {

    }

    companion object {
        private val TAG = "EditActivity"
    }
}
