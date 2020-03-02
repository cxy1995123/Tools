package chen.com.library.document

import android.content.Context
import android.os.Environment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import chen.com.library.R
import chen.com.library.data.TimeDate
import chen.com.library.fragment.BaseFragment
import chen.com.library.tools.ArrayUtil
import cn.com.twosnail.health.doctor.kt.adapter.base.DefaultAdapter
import cn.com.twosnail.health.doctor.kt.adapter.base.DefaultViewHolder
import cn.com.twosnail.health.doctor.kt.adapter.base.RecyclerViewItemClickListener
import kotlinx.android.synthetic.main.fragment_file_manager.*
import java.io.File

open class DocumentManagerFragment : BaseFragment() {

    override fun layoutRes(): Int {
        return R.layout.fragment_file_manager
    }

    private var currentFile: File? = null
    var adapter: FileAdapter? = null
    override fun onViewInit(view: View) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FileAdapter(this)
        recyclerView.adapter = adapter
        currentFile = Environment.getExternalStorageDirectory()
        adapter!!.add(currentFile!!)
        adapter!!.setOnItemClickListener(object : RecyclerViewItemClickListener<File> {
            override fun onItemClick(position: Int, item: View, obj: File?) {
                if (obj == null) return
                if (obj.isDirectory) {
                    currentFile = obj
                    val files = currentFile!!.listFiles()
                    adapter!!.clear()
                    val collection = ArrayUtil.toList2(files)
                    adapter!!.addAll(collection)
                    adapter!!.notifyDataSetChanged()
                } else {

                }
            }
        })
    }

    override fun onBackPress(): Boolean {
        if (currentFile!!.parent == null) {
            activity!!.finish()
        } else {
            adapter!!.clear()
            adapter!!.addAll(ArrayUtil.toList2(currentFile!!.parentFile.listFiles()))
            adapter!!.notifyDataSetChanged()
        }
        return true
    }
}

class FileAdapter(fragment: Fragment) : DefaultAdapter<File>(fragment) {

    override fun itemLayoutRes(): Int {
        return R.layout.item_file
    }

    override fun createHolder(view: View): DefaultViewHolder<File> {
        return FileViewHolder(fragment!!, view)
    }

}

class FileViewHolder(fragment: Fragment, view: View) : DefaultViewHolder<File>(view, fragment) {

    var title: TextView? = null
    var time: TextView? = null

    init {
        title = findViewById(R.id.title)
        time = findViewById(R.id.time)
    }

    override fun onBindView(obj: File?) {
        if (obj == null) return
        title?.text = obj.name
        time?.text = TimeDate.getInstance(obj.lastModified()).simpleTime
    }

    override fun onClick(v: View?) {
        super.onClick(v)
    }

}
