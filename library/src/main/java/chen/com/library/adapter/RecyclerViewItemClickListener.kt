package cn.com.twosnail.health.doctor.kt.adapter.base

import android.view.View

public interface RecyclerViewItemClickListener<T>   {
   public fun onItemClick(position: Int, item: View, obj: T?)
}