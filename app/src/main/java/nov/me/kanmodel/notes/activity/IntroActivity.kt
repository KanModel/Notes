package nov.me.kanmodel.notes.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment

import nov.me.kanmodel.notes.R
import nov.me.kanmodel.notes.activity.ui.CustomSlideBigText

/**
 * Created by KanModel on 2017/12/27.
 * 介绍页面
 */
class IntroActivity : AppIntro() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFlowAnimation()
        setBarColor(resources.getColor(R.color.colorPrimaryDark))
        setSeparatorColor(resources.getColor(R.color.colorPrimaryDark))
        Log.d(TAG, "onCreate: 开始")
        //第一页
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro1_title), getString(R.string.intro1_desc),
                R.drawable.logo_appwidget_preview, resources.getColor(R.color.colorPrimaryDark)))
        //第二页
        val cs1 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text)
        cs1.setTitle(getString(R.string.intro2_title))
        addSlide(cs1)
        //第三页
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro3_title), getString(R.string.intro3_desc),
                R.drawable.logo_appwidget_preview, resources.getColor(R.color.colorPrimaryDark)))

        setDoneText(getString(R.string.intro_done))
        setSkipText(getString(R.string.intro_skip))
        showSkipButton(true)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private const val TAG = "IntroActivity"
    }
}
