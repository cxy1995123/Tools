package cn.com.twosnail.health.doctor.kt.adapter.base

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class  DefaultViewHolder<T>(item: View, private val context: Context) : RecyclerView.ViewHolder(item),
    View.OnClickListener,LayoutContainer {

    override val containerView: View? = item
    
    var obj: T? = null

    constructor(item: View, fragment: Fragment) : this(item, fragment.activity!!)

    constructor(item: View, activity: Activity) : this(item, context = activity)
    
    fun onBind(obj: T?) {
        this.obj = obj
        onBindView(obj)
    }

    fun getContext():Context{
       return context
    }

    abstract fun onBindView(obj: T?)

    init {
        itemView.setOnClickListener(::onClick)
    }

    var onItemClickListener: RecyclerViewItemClickListener<T>? = null

    fun <X : View> findViewById(int: Int): X {
        return itemView.findViewById(int)
    }

    override fun onClick(v: View?) {
        if (v != null && onItemClickListener != null) {
            onItemClickListener!!.onItemClick(adapterPosition, itemView, obj)
        }
    }

}