package chen.com.library.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import chen.com.library.R;


public abstract class TestFragmentActivity extends BaseActivity {

    public final static int REQUEST_CODE = 0x0012;

    String fragmentName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        int res = getIntent().getIntExtra("res", -1);
        if (res != -1) {
            setTheme(res);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main_fragment);
        fragmentName = getIntent().getStringExtra("FragmentName");
        show(fragmentName);
    }


    public static void launcher(Context activity, Class fragmentClass){
        launcher(activity, fragmentClass, null,0);
    }

    public static void launcher(Context activity, Class fragmentClass, Bundle bundle){
        launcher(activity, fragmentClass, bundle,0);
    }

    public static void launcher(Fragment fragment, Class fragmentClass) {
        launcher(fragment.getActivity(), fragmentClass);
    }

    public static void launcher(Fragment fragment, Class fragmentClass, Bundle bundle) {
        launcher(fragment.getActivity(), fragmentClass, bundle);
    }

    public static void launcher(Fragment fragment, Class fragmentClass, Bundle bundle, @StyleRes int res) {
        launcher(fragment.getActivity(), fragmentClass, bundle, res);
    }


    public static void launcher(Activity activity, Class fragmentClass) {
        launcher(activity, fragmentClass, null);
    }

    public static void launcher(Activity activity, Class fragmentClass, @StyleRes int res) {
        launcher(activity, fragmentClass, null, res);
    }

    public static void launcher(Activity activity, Class fragmentClass, Bundle bundle) {
        launcher(activity, fragmentClass, bundle, -1);
    }

    public  static void launcher(Context activity, Class fragmentClass, Bundle bundle, @StyleRes int res) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra("FragmentName", fragmentClass.getName());
        intent.putExtra("res", res);
        intent.setClass(activity, TestFragmentActivity.class);
        activity.startActivity(intent);
    }


    public static void launcher(Activity activity, Class fragmentClass, Bundle bundle, @StyleRes int res) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra("FragmentName", fragmentClass.getName());
        intent.putExtra("res", res);
        intent.setClass(activity, TestFragmentActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }


    private void show(String name) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Fragment fragment = Fragment.instantiate(this, name, extras);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment, name);
        transaction.commitAllowingStateLoss();
    }


}
