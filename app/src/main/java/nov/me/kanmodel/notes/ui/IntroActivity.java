package nov.me.kanmodel.notes.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import nov.me.kanmodel.notes.R;

/**
 * Created by KanModel on 2017/12/27.
 * 介绍页面
 */

public class IntroActivity extends AppIntro {
    private static final String TAG = "IntroActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFadeAnimation();
        setFlowAnimation();
        setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setSeparatorColor(getResources().getColor(R.color.colorPrimaryDark));
        Log.d(TAG, "onCreate: 开始");
//        com.github.paolorotolo.appintro.AppIntro2Fragment.newInstance();
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro1_title), getString(R.string.intro1_desc),
                R.drawable.logo_appwidget_preview, getResources().getColor(R.color.colorPrimaryDark)));
        CustomSlideBigText cs1 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
        cs1.setTitle(getString(R.string.intro2_title));
        addSlide(cs1);

//        CustomSlideBigText cs2 = CustomSlideBigText.newInstance(R.layout.custom_slide_big_text);
//        cs2.setTitle(getString(R.string.intro3_desc));
//        addSlide(cs2);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro3_title), getString(R.string.intro3_desc),
                R.drawable.logo_appwidget_preview, getResources().getColor(R.color.colorPrimaryDark)));

        setDoneText(getString(R.string.intro_done));
        setSkipText(getString(R.string.intro_skip));
        showSkipButton(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
