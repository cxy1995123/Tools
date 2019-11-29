package chen.com.library.adapter;


import android.content.Context;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static android.view.View.NO_ID;


/**
 * MultiRecyclerViewAdapter its responsibility to sub adapters
 */
public class MultiRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Nullable
    private AtomicInteger mIndexGen;

    private int mIndex = 0;

    private final boolean mHasConsistItemType;

    private SparseArray<RecyclerView.Adapter> mItemTypeAry = new SparseArray<>();

    public int getAdapterCount(){
        return mAdapters.size();
    }

    public RecyclerView.Adapter getAdapter(int position){
        return mAdapters.get(position).second;
    }

    @NonNull
    private final List<Pair<AdapterDataObserver, RecyclerView.Adapter>> mAdapters = new ArrayList<>();

    private int mTotal = 0;

    /**
     * Delegate Adapter merge multi sub adapters, default is thread-unsafe
     *
     */
    public MultiRecyclerViewAdapter() {
        this(false, false);
    }

    /**
     * @param hasConsistItemType whether sub adapters itemTypes are consistent
     */
    public MultiRecyclerViewAdapter(boolean hasConsistItemType) {
        this(hasConsistItemType, false);
    }

    /**
     * @param hasConsistItemType whether sub adapters itemTypes are consistent
     * @param threadSafe         tell whether your adapter is thread-safe or not
     */
    MultiRecyclerViewAdapter(boolean hasConsistItemType, boolean threadSafe) {
        if (threadSafe) {
            mIndexGen = new AtomicInteger(0);
        }

        mHasConsistItemType = hasConsistItemType;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mHasConsistItemType) {
            RecyclerView.Adapter adapter = mItemTypeAry.get(viewType);
            if (adapter != null) {
                return adapter.onCreateViewHolder(parent, viewType);
            }

            return null;
        }


        // reverse Cantor Function
        int w = (int) (Math.floor(Math.sqrt(8 * viewType + 1) - 1) / 2);
        int t = (w * w + w) / 2;

        int index = viewType - t;
        int subItemType = w - index;

        int idx = findAdapterPositionByIndex(index);
        if (idx < 0) {
            return null;
        }

        Pair<AdapterDataObserver, RecyclerView.Adapter> p = mAdapters.get(idx);

        return p.second.onCreateViewHolder(parent, subItemType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Pair<AdapterDataObserver, RecyclerView.Adapter> pair = findAdapterByPosition(position);
        if (pair == null) {
            return;
        }

        pair.second.onBindViewHolder(holder, position - pair.first.mStartPosition);
        //pair.second.onBindViewHolderWithOffset(holder, position - pair.first.mStartPosition, position);
    }

    @Override
    public int getItemCount() {
        return mTotal;
    }

    /**
     * Big integer of itemType returned by delegated adapter may lead to failed
     *
     * @param position item position
     * @return integer represent item view type
     */
    @Override
    public int getItemViewType(int position) {
        Pair<AdapterDataObserver, RecyclerView.Adapter> p = findAdapterByPosition(position);
        if (p == null) {
            return RecyclerView.INVALID_TYPE;
        }

        int subItemType = p.second.getItemViewType(position - p.first.mStartPosition);

        if (subItemType < 0) {
            // negative integer, invalid, just return
            return subItemType;
        }

        if (mHasConsistItemType) {
            mItemTypeAry.put(subItemType, p.second);
            return subItemType;
        }


        int index = p.first.mIndex;

        return (int) getCantor(subItemType, index);
    }


    @Override
    public long getItemId(int position) {
        Pair<AdapterDataObserver, RecyclerView.Adapter> p = findAdapterByPosition(position);

        if (p == null) {
            return NO_ID;
        }

        long itemId = p.second.getItemId(position - p.first.mStartPosition);

        if (itemId < 0) {
            return NO_ID;
        }

        int index = p.first.mIndex;
        /*
         * Now we have a pairing function problem, we use cantor pairing function for itemId.
         */
        return getCantor(index, itemId);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        // do nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        int position = holder.getAdapterPosition();
        if (position > 0) {
            Pair<AdapterDataObserver, RecyclerView.Adapter> pair = findAdapterByPosition(position);
            if (pair != null) {
                pair.second.onViewRecycled(holder);
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getPosition();
        if (position > 0) {
            Pair<AdapterDataObserver, RecyclerView.Adapter> pair = findAdapterByPosition(position);
            if (pair != null) {
                pair.second.onViewAttachedToWindow(holder);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int position = holder.getPosition();
        if (position > 0) {
            Pair<AdapterDataObserver, RecyclerView.Adapter> pair = findAdapterByPosition(position);
            if (pair != null) {
                pair.second.onViewDetachedFromWindow(holder);
            }
        }
    }

    public void setAdapters(@Nullable List<RecyclerView.Adapter> adapters) {
        clear();

        if (adapters == null) {
            adapters = Collections.emptyList();
        }

        boolean hasStableIds = true;
        mTotal = 0;
        for (RecyclerView.Adapter adapter : adapters) {
            // every adapter has an unique index id
            AdapterDataObserver observer = new AdapterDataObserver(mTotal, mIndexGen == null ? mIndex++ : mIndexGen.incrementAndGet());
            adapter.registerAdapterDataObserver(observer);
            hasStableIds = hasStableIds && adapter.hasStableIds();

            mTotal += adapter.getItemCount();
            mAdapters.add(Pair.create(observer, adapter));
        }

        if (!hasObservers()) {
            super.setHasStableIds(hasStableIds);
        }
    }

    /**
     * Add adapters in <code>position</code>
     *
     * @param position the index where adapters added
     * @param adapters adapters
     */
    public void addAdapters(int position, @Nullable List<RecyclerView.Adapter> adapters) {
        if (adapters == null || adapters.size() == 0) {
            return;
        }

        boolean hasStableIds = super.hasStableIds();

        if (position < 0) {
            position = 0;
        }

        if (position > mAdapters.size()) {
            position = mAdapters.size();
        }

        for (RecyclerView.Adapter adapter : adapters) {
            // every adapter has an unique index id
            AdapterDataObserver observer = new AdapterDataObserver(mTotal, mIndexGen == null ? mIndex++ : mIndexGen.incrementAndGet());
            adapter.registerAdapterDataObserver(observer);
            hasStableIds = hasStableIds && adapter.hasStableIds();

            mTotal += adapter.getItemCount();

            mAdapters.add(position, Pair.create(observer, adapter));
            position++;
        }

        if (!hasObservers()) {
            super.setHasStableIds(hasStableIds);
        }
    }

    /**
     * Append adapters to the end
     *
     * @param adapters adapters will be appended
     */
    public void addAdapters(@Nullable List<RecyclerView.Adapter> adapters) {
        addAdapters(mAdapters.size(), adapters);
    }

    public void addAdapter(int position, @Nullable RecyclerView.Adapter adapter) {
        addAdapters(position, Collections.singletonList(adapter));
    }

    public void addAdapter(@Nullable RecyclerView.Adapter adapter) {
        addAdapters(Collections.singletonList(adapter));
    }

    public void removeFirstAdapter() {
        if (mAdapters != null && !mAdapters.isEmpty()) {
            RecyclerView.Adapter targetAdatper = mAdapters.get(0).second;
            removeAdapter(targetAdatper);
        }
    }

    public void removeLastAdapter() {
        if (mAdapters != null && !mAdapters.isEmpty()) {
            RecyclerView.Adapter targetAdatper = mAdapters.get(mAdapters.size() - 1).second;
            removeAdapter(targetAdatper);
        }
    }

    public void removeAdapter(int adapterIndex) {
        if (adapterIndex >= 0 && adapterIndex < mAdapters.size()) {
            RecyclerView.Adapter targetAdatper = mAdapters.get(adapterIndex).second;
            removeAdapter(targetAdatper);
        }
    }

    public void removeAdapter(@Nullable RecyclerView.Adapter targetAdapter) {
        if (targetAdapter == null) {
            return;
        }
        removeAdapters(Collections.singletonList(targetAdapter));
    }

    public void removeAdapters(@Nullable List<RecyclerView.Adapter> targetAdapters) {
        if (targetAdapters == null || targetAdapters.isEmpty()) {
            return;
        }
        for (int i = 0, size = targetAdapters.size(); i < size; i++) {
            RecyclerView.Adapter one = targetAdapters.get(i);
            Iterator<Pair<AdapterDataObserver, RecyclerView.Adapter>> itr = mAdapters.iterator();
            while (itr.hasNext()) {
                Pair<AdapterDataObserver, RecyclerView.Adapter> pair = itr.next();
                RecyclerView.Adapter theOther = pair.second;
                if (theOther.equals(one)) {
                    theOther.unregisterAdapterDataObserver(pair.first);
                    itr.remove();
                    break;
                }
            }
        }

        List<RecyclerView.Adapter> newAdapter = new ArrayList<>();
        Iterator<Pair<AdapterDataObserver, RecyclerView.Adapter>> itr = mAdapters.iterator();
        while (itr.hasNext()) {
            newAdapter.add(itr.next().second);
        }
        setAdapters(newAdapter);
    }

    public void clear() {
        mTotal = 0;
        mIndex = 0;
        if (mIndexGen != null) {
            mIndexGen.set(0);
        }

        for (Pair<AdapterDataObserver, RecyclerView.Adapter> p : mAdapters) {
            p.second.unregisterAdapterDataObserver(p.first);
        }

        mItemTypeAry.clear();
        mAdapters.clear();
    }


    @Nullable
    protected Pair<AdapterDataObserver, RecyclerView.Adapter> findAdapterByPosition(int position) {
        final int count = mAdapters.size();
        if (count == 0) {
            return null;
        }

        int s = 0, e = count - 1, m;
        Pair<AdapterDataObserver, RecyclerView.Adapter> rs = null;

        // binary search range
        while (s <= e) {
            m = (s + e) / 2;
            rs = mAdapters.get(m);
            int endPosition = rs.first.mStartPosition + rs.second.getItemCount() - 1;

            if (rs.first.mStartPosition > position) {
                e = m - 1;
            } else if (endPosition < position) {
                s = m + 1;
            } else if (rs.first.mStartPosition <= position && endPosition >= position) {
                break;
            }

            rs = null;
        }

        return rs;
    }


    protected int findAdapterPositionByIndex(int index) {
        final int count = mAdapters.size();
        if (count == 0) {
            return -1;
        }

        int s = 0, e = count - 1, m = -1;
        Pair<AdapterDataObserver, RecyclerView.Adapter> rs = null;

        // binary search range
        while (s <= e) {
            m = (s + e) / 2;
            rs = mAdapters.get(m);
            if (rs.first.mIndex > index) {
                e = m - 1;
            } else if (rs.first.mIndex < index) {
                s = m + 1;
            } else if (rs.first.mIndex == index) {
                break;
            }
            rs = null;
        }

        return rs == null ? -1 : m;
    }

    private class AdapterDataObserver extends RecyclerView.AdapterDataObserver {
        int mStartPosition;

        int mIndex = -1;

        public AdapterDataObserver(int startPosition, int index) {
            this.mStartPosition = startPosition;
            this.mIndex = index;
        }

        public void updateStartPositionAndIndex(int startPosition, int index) {
            this.mStartPosition = startPosition;
            this.mIndex = index;
        }

        private boolean updateLayout(){
            if (mIndex < 0) {
                return false;
            }

            final int idx = findAdapterPositionByIndex(mIndex);
            if (idx < 0) {
                return false;
            }

            Pair<AdapterDataObserver, RecyclerView.Adapter> p = mAdapters.get(idx);

            mTotal = mStartPosition + p.second.getItemCount();

            for (int i = idx + 1; i < mAdapters.size(); i++) {
                Pair<AdapterDataObserver, RecyclerView.Adapter> pair = mAdapters.get(i);
                // update startPosition for adapters in following
                pair.first.mStartPosition = mTotal;
                mTotal += pair.second.getItemCount();
            }

            return true;
        }

        @Override
        public void onChanged() {
            if (!updateLayout()) {
                return;
            }
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (!updateLayout()) {
                return;
            }
            notifyItemRangeRemoved(mStartPosition + positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (!updateLayout()) {
                return;
            }
            notifyItemRangeInserted(mStartPosition + positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (!updateLayout()) {
                return;
            }
            notifyItemMoved(mStartPosition + fromPosition, mStartPosition + toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (!updateLayout()) {
                return;
            }
            notifyItemRangeChanged(mStartPosition + positionStart, itemCount);
        }
    }

    /**
     * return an adapter that only contains one item, and using SimpleLayoutHelper
     *
     * @param view the only view, no binding is required
     * @return adapter
     */
    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> simpleAdapter(@NonNull View view) {
        return new SimpleViewAdapter(view);
    }

    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> line(Context context, int height, int color) {
        View view = new View(context);
        view.setBackgroundColor(color);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(params);
        return new SimpleViewAdapter(view);
    }

    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> line(Context context, int height) {
        return line(context, height, 0x00000000);
    }

    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> line(Context context) {
        context.getResources().getDisplayMetrics();
        int dipValue = 8;
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
        return line(context, height, 0x00000000);
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View view) {
            super(view);
        }
    }

    public static class SimpleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private View mView;

        public SimpleViewAdapter(@NonNull View view) {
            this.mView = view;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private static long getCantor(long k1, long k2) {
        return (k1 + k2) * (k1 + k2 + 1) / 2 + k2;
    }

    public boolean contains(RecyclerView.Adapter adapter){
        return indexOfAdapter(adapter) > 0;
    }

    public int indexOfAdapter(RecyclerView.Adapter adapter){
        for(int i=0; i<mAdapters.size(); i++){
            Pair<AdapterDataObserver, RecyclerView.Adapter> pair = mAdapters.get(i);
            if(pair.second == adapter){
                return i;
            }
        }
        return -1;
    }
}
