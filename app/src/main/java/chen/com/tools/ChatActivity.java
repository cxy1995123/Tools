package chen.com.tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import chen.com.library.systembar.StatusBarCompat;
import chen.com.library.tools.Util;
import chen.com.library.view.RefreshLayout2;

public class ChatActivity extends BaseChatActivity {
    private Toolbar mToolbar;
    RefreshLayout2 refreshLayout2;
    RecyclerView listView;
    EditText inputEd;
    View inputView;
    View imToolsView;
    View toolsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.translucentStatusBar(this, true);
        refreshLayout2 = findViewById(R.id.refreshLayout);
        listView = findViewById(R.id.listView);
        inputEd = findViewById(R.id.editText);
        toolsBtn = findViewById(R.id.toolsBtn);
        imToolsView = findViewById(R.id.imTools);
        inputView = findViewById(R.id.imInput);
        mToolbar = findViewById(R.id.mToolbar);
        refreshLayout2.setOnRefreshListener(new RefreshLayout2.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout2 layout) {
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout2.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(RefreshLayout2 layout) {
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout2.refreshComplete();
                    }
                }, 1000);
            }
        });
        mToolbar.getLayoutParams().height += Util.getStatusBarHeight(this);
        mToolbar.setPadding(0, Util.getStatusBarHeight(this), 0, 0);

        mToolbar.setOnClickListener(this);
        refreshLayout2.setOnClickListener(this);
        imToolsView.setVisibility(View.GONE);
        toolsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (keyBoardIsShow && !toolsBarIsShow) {
//                    softHideKeyBoardUtil.revertContentHeith();
//                    updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
//                    imToolsView.setVisibility(imToolsView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                    toolsBarIsShow = imToolsView.getVisibility() == View.VISIBLE;
//                    Util.hideKeyBord(inputEd);
//                } else {
//                    updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                    imToolsView.setVisibility(imToolsView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                    toolsBarIsShow = imToolsView.getVisibility() == View.VISIBLE;
//                }
                if (toolsBarIsShow) {
                    hideEmojView();
                } else {
                    showEmojView();
                }
            }
        });
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public View getEmojView() {
        return imToolsView;
    }

    @Override
    public EditText getInputEditText() {
        return inputEd;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main2;
    }
}
