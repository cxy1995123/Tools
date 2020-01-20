package chen.com.tools;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import chen.com.library.adapter.AbstractAdapter;
import chen.com.library.adapter.BaseViewHolder;
import chen.com.library.systembar.StatusBarCompat;

public class RecyclerViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mToolbar = findViewById(R.id.mToolbar);
        StatusBarCompat.translucentStatusBar(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SoftKeyboardHelper2 helper = new SoftKeyboardHelper2(this);
        helper.setFullScreen(true);
        helper.setKeyboardChangeListener(new SoftKeyboardHelper2.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyboardChange(boolean isShow, int softKeyBoardHeight) {
                Log.i("RecyclerViewActivity", "onSoftKeyboardChange: " + softKeyBoardHeight);
            }
        });
        final TextAdapter adapter = new TextAdapter(this);
        for (int i = 0; i < 20; i++) {
            adapter.add(String.valueOf(i));
        }
        recyclerView.setAdapter(adapter);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);

                List<String> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add(String.valueOf(Math.random() * 10000000));
                }
                adapter.addAll(0, list);
                adapter.notifyItemRangeInserted(0, 10);

            }
        });
        TestFloatView view = new TestFloatView(this);
        view.show(getWindow().getDecorView());
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }


    class TextAdapter extends AbstractAdapter<String> {

        public TextAdapter(Activity activity) {
            super(activity);
        }

        @Override
        public int createItem(int viewType) {
            return android.R.layout.simple_expandable_list_item_1;
        }

        @Override
        public BaseViewHolder<String> createHolder(View itemView, @NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(itemView, getActivity());
        }
    }

    class ViewHolder extends BaseViewHolder<String> {

        TextView textView;

        public ViewHolder(View itemView, Activity activity) {
            super(itemView, activity);
            textView = findViewById(android.R.id.text1);
        }

        @Override
        protected void onBindViewHolder(String value) {
            textView.setText(value);
        }
    }

}
