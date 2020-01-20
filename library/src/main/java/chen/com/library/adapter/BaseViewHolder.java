package chen.com.library.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private Activity activity;
    private Fragment fragment;
    private Context context;

    public int getColor(@ColorRes int id) {
        return getContext().getResources().getColor(id);
    }

    protected AbstractAdapter.OnItemClickListener<T> onItemClickListener;

    protected abstract void onBindViewHolder(T value);

    public BaseViewHolder(View itemView, Fragment fragment) {
        this(itemView, fragment.getActivity());
        this.fragment = fragment;
    }

    public BaseViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
    }

    public BaseViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    public void setOnItemClickListener(AbstractAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return itemView.findViewById(id);
    }

    public Activity getActivity() {
        return activity;
    }

    public Context getContext() {
        if (fragment != null) {
            return fragment.getActivity();
        } else if (activity != null) {
            return activity;
        } else {
            return context;
        }
    }

    public Fragment getFragment() {
        return fragment;
    }

}