package chen.com.library.tools;


import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class StatusBarUtil {


    /**
     * 5.0 +
     * 透明状态栏
     * 内容会拓展到状态栏后面
     **/
    @TargetApi(19)
    public static void TransparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStatusBarColor(activity, Color.TRANSPARENT);
            }

        }
    }


    /**
     * 5.0 +
     * 设置状态栏颜色
     * 内容会拓展到状态栏后面
     **/
    @TargetApi(21)
    public static void setStatusBarColor(Activity activity, int color) {

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(color);
        }

    }


    public static void hideActionBar(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    /**
     * 5.0 +
     * 设置状态栏和导航栏颜色
     * 内容会拓展到状态栏和导航栏后面
     **/
    @TargetApi(21)
    public static void setStatusBarNavigationBarColor(Activity activity, int color) {

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(color);
            activity.getWindow().setStatusBarColor(color);
        }

    }

    /**
     * 透明状态栏和导航栏
     * 内容会拓展到状态栏和导航栏后面
     **/
    @TargetApi(19)
    public static void TransparentStatusBarNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarNavigationBarColor(activity, Color.TRANSPARENT);
        }
    }


    /**
     * 沉浸式状态栏 4.4+
     * onWindowFocusChanged
     **/
    @TargetApi(19)
    public static void FullScreen(Activity activity, boolean hasFocus) {
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }


    /**
     *SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 弱化状态栏和导航栏的图标 黑色
     *SYSTEM_UI_FLAG_VISIBLE 正常白色 状态栏
     *SYSTEM_UI_FLAG_HIDE_NAVIGATION	隐藏导航栏，用户点击屏幕会显示导航栏
     *SYSTEM_UI_FLAG_FULLSCREEN	隐藏状态栏
     *SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION	拓展布局到导航栏后面
     *SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN	拓展布局到状态栏后面
     *SYSTEM_UI_FLAG_LAYOUT_STABLE	稳定的布局，不会随系统栏的隐藏、显示而变化
     *SYSTEM_UI_FLAG_IMMERSIVE	沉浸模式，用户可以交互的界面
     *SYSTEM_UI_FLAG_IMMERSIVE_STICKY	沉浸模式，用户可以交互的界面。同时，用户上下拉系统栏时，会自动隐藏系统栏
     **/


    /**
     * 设置状态栏和导航栏的颜色
     * 内容不会拓展到状态栏和导航栏后面
     **/
    @TargetApi(21)
    public static void setStatusBarColor2(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

}
