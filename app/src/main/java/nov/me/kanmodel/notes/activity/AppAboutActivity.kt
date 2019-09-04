package nov.me.kanmodel.notes.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView

import nov.me.kanmodel.notes.R
import nov.me.kanmodel.notes.utils.Utils

/**
 * 关于Activity
 */
class AppAboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appabout)
        val textView = findViewById<TextView>(R.id.about_tv)
        textView.text = String.format("%s\n版本 :%s", resources.getString(R.string.app_name), Utils.getVersionName(this))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}


