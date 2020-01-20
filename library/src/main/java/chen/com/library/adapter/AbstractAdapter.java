package chen.com.library.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> {

    protected Fragment fragment;
    protected Activity activity;
    protected Context context;
    public List<T> list;
    private OnItemClickListener<T> itemClickListener;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setItemClickListener(OnItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OnItemClickListener<T> getItemClickListener() {
        return itemClickListener;
    }

    public void clear() {
        list.clear();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(RecyclerView.ViewHolder viewHolder, View itemView, T t, int pos);
    }

    public AbstractAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        } else if (context instanceof ContextWrapper) {
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    this.activity = (Activity) context;
                    return;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
    }

    public AbstractAdapter(Fragment fragment) {
        this(fragment.getActivity());
        this.fragment = fragment;
    }

    public AbstractAdapter(Activity activity) {
        this.activity = activity;
        this.list = new ArrayList<>();
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

    public abstract int createItem(int viewType);

    public abstract BaseViewHolder<T> createHolder(View itemView, @NonNull ViewGroup parent, int viewType);

    @NonNull
    @Override
    public BaseViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder<T> holder = createHolder(inflate(createItem(viewType), parent), parent, viewType);
        holder.setOnItemClickListener(getItemClickListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
        holder.onBindViewHolder(getItem(position));
    }

    public T getItem(int pos) {
        if (pos < list.size()) {
            return list.get(pos);
        }
        return null;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Activity getActivity() {
        return activity;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }

    public void addAll(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return;
        list.addAll(collection);
    }

    public void addAll(int index, Collection<T> collection) {
        if (collection == null || collection.isEmpty()) return;
        list.addAll(index, collection);
    }

    public void add(T t) {
        list.add(t);
    }

    public void add(int index, T t) {
        list.add(index, t);
    }

    public boolean remove(T t) {
        return list.remove(t);
    }

    public T remove(int index) {
        return list.remove(index);
    }

    public boolean update(T t) {
        if (list.contains(t)) {
            list.set(list.indexOf(t), t);
            return true;
        }
        return false;
    }

    public View inflate(int res, ViewGroup group) {
        return LayoutInflater.from(getContext()).inflate(res, group, false);
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
