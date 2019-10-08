package chen.com.library.activity;

import android.annotation.TargetApi;
import android.os.Build;

import android.util.TypedValue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import java.util.Collection;
import java.util.List;

import chen.com.library.fragment.BaseFragment;
import chen.com.library.tools.DisplayHelper;
import chen.com.library.tools.StatusBarUtil;


public class BaseActivity extends AppCompatActivity {



    protected void hideActionBar() {
        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } catch (NullPointerException e) {

        }
    }

    private int getScreenHeight() {
        return DisplayHelper.getScreenHeight(this);
    }

    private int getScreenWidth() {
        return DisplayHelper.getScreenWidth(this);
    }

    /**
     * 单位转换: dp -> px
     */
    protected int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 单位转换:sp -> px
     */
    protected int px2dp(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }


    protected boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    protected boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }


    public int getColorRes(int color_res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(color_res, getTheme());
        } else {
            return getResources().getColor(color_res);
        }
    }


    /**
     * 何状态栏颜色
     **/
    @TargetApi(21)
    protected void setStatusBarColor(int color) {
        StatusBarUtil.setStatusBarColor2(this, color);
    }

    /**
     * 内容会延展到状态栏背后
     **/
    protected void transparentStatusBar() {
        StatusBarUtil.TransparentStatusBar(this);
    }

    protected void transparentStatusBarNavigationBar() {
        StatusBarUtil.TransparentStatusBarNavigationBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (!isEmpty(fragments)) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment fragment = fragments.get(i);
                if (fragment != null && fragment.isAdded() && fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).onBackPress()) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();

    }


    @Override
    public void finish() {
        super.finish();
    }
}
