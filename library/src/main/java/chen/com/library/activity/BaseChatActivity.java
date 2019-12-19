package chen.com.library.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import chen.com.library.tools.Util;

@SuppressLint("Registered")
public abstract class BaseChatActivity extends Activity implements IMChatView, View.OnClickListener {

    private SoftKeyBoardManager manager;

    protected boolean toolsBarIsShow = false;
    protected boolean keyBoardIsShow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        manager = new SoftKeyBoardManager(this);
    }


    @Override
    public void onKeyBoardChange(boolean isShow, int keyBoardHealth) {
        Log.i("ChatActivity", "onKeyBoardChange: " + isShow + ",KeyBoardHealth:" + keyBoardHealth);
        keyBoardIsShow = isShow;

        if (isShow) {
            ViewGroup.LayoutParams params = getEmojView().getLayoutParams();
            if (params.height != keyBoardHealth) {
                params.height = keyBoardHealth;
                getEmojView().setLayoutParams(params);
            }
        }

        if (keyBoardIsShow && toolsBarIsShow) {
            toolsBarIsShow = false;
            getEmojView().setVisibility(View.GONE);
        }
    }


    @Override
    public void showEmojView() {
        if (keyBoardIsShow) {
            manager.revertContentHeith();
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        getEmojView().setVisibility(View.VISIBLE);
        toolsBarIsShow = true;
        hideKeyBoard();
    }

    @Override
    public void hideEmojView() {
        updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getEmojView().setVisibility(View.GONE);
        toolsBarIsShow = false;
    }


    @Override
    public void hideKeyBoard() {
        Util.hideKeyBord(getInputEditText());
    }

    @Override
    public void updateSoftInputMethod(int softInputMode) {
        if (!isFinishing()) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            if (params.softInputMode != softInputMode) {
                params.softInputMode = softInputMode;
                getActivity().getWindow().setAttributes(params);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (keyBoardIsShow) {
            hideKeyBoard();
        }
        if (toolsBarIsShow) {
            hideEmojView();
        }
    }


    private boolean isBack() {
        return !keyBoardIsShow && !toolsBarIsShow;
    }

    @Override
    public void onBackPressed() {

        if (isBack()){
            super.onBackPressed();
        }else {
            if (keyBoardIsShow){
                hideKeyBoard();
            }

            if (toolsBarIsShow) {
                hideEmojView();
            }
        }
    }
}
