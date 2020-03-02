package chen.com.library.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.Collection;

import chen.com.library.tools.DisplayHelper;

import static java.security.AccessController.getContext;

public abstract class BaseFragment extends Fragment {

    public abstract int layoutRes();

    public abstract void onViewInit(@NonNull View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutRes(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewInit(view);
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


    private int getScreenHeight() {
        return DisplayHelper.getScreenHeight(getContext());
    }

    private int getScreenWidth() {
        return DisplayHelper.getScreenWidth(getContext());
    }


    protected boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    protected boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public boolean onBackPress() {
        return false;
    }

}
