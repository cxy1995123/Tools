package cn.com.twosnail.health.doctor.kt.adapter.base

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import chen.com.library.fragment.BaseFragment

abstract class DefaultAdapter<T>(context: Context) : RecyclerView.Adapter<DefaultViewHolder<T>>() {
    
    private var list: MutableList<T> = mutableListOf()
    
    private var mContext: Context = context
    
    private var onItemClickListener: RecyclerViewItemClickListener<T>? = null
    
    fun getItemClickListener(): RecyclerViewItemClickListener<T>? {
        return onItemClickListener
    }
    
    var fragment: Fragment? = null
    var activity: Activity? = null
        get():Activity? {
            return if (field != null) {
                field
            } else {
                getActivity(mContext)
            }
        }
    
    constructor(activity: Activity) : this(context = activity) {
        this.activity = activity
    }
    
    constructor(fragment: Fragment) : this(context = fragment.activity!!) {
        this.fragment = fragment
        if (fragment.activity != null)
            this.activity = fragment.activity
    }
    
    constructor(fragment: BaseFragment) : this(context = fragment.context!!) {
        this.fragment = fragment
        if (fragment.activity != null)
            this.activity = fragment.activity
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolder<T> {
        val itemView: View = LayoutInflater.from(mContext).inflate(itemLayoutRes(), parent, false)
        val viewHolder = createHolder(itemView)
        viewHolder.onItemClickListener = onItemClickListener
        return viewHolder
    }
    
    override fun getItemCount(): Int {
        return list.size
    }
    
    
    override fun onBindViewHolder(holder: DefaultViewHolder<T>, position: Int) {
        holder.onBind(getItem(position))
    }
    
    abstract fun itemLayoutRes(): Int
    
    abstract fun createHolder(view: View): DefaultViewHolder<T>
    
    public open fun updateItem(t: T) {
    
    }
    
    fun getItem(position: Int): T? {
        if (position >= 0 && position < list.size) {
            return list[position]
        }
        return null
    }
    
    fun add(obj: T) {
        list.add(obj)
    }
    
    fun add(position: Int, obj: T) {
        list.add(position, obj)
    }
    
    fun addAll(list: List<T>) {
        this.list.addAll(list)
    }
    
    fun addAll(position: Int, list: List<T>) {
        this.list.addAll(position, list)
    }
    
    fun remove(position: Int): Boolean {
        return if (position < list.size) {
            list.removeAt(position) != null
        } else {
            false
        }
    }
    
    fun setOnItemClickListener(onItemClickListener: RecyclerViewItemClickListener<T>) {
        this.onItemClickListener = onItemClickListener
    }
    
    fun getContext(): Context {
        return mContext
    }
    
    fun getList(): MutableList<T> {
        return list
    }
    
    fun getActivity(context: Context): Activity? {
        var mContext = context
        // Gross way of unwrapping the Activity so we can get the FragmentManager
        while (mContext is ContextWrapper) {
            if (mContext is Activity) {
                return mContext
            }
            mContext = mContext.baseContext
        }
        return null
    }
    
    fun clear() {
        list.clear()
    }
    
    fun isEmpty(): Boolean {
        return list.isNullOrEmpty()
    }
    
}