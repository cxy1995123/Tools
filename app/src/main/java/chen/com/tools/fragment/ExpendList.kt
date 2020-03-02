package chen.com.tools.fragment

import android.content.Context
import android.view.View
import android.widget.ExpandableListView
import chen.com.library.fragment.BaseFragment
import chen.com.tools.R
import chen.com.tools.activity.FragmentSupporterActivity
import chen.com.tools.adapter.ExpandableListviewAdapter
import kotlinx.android.synthetic.main.fragment_expend_list.*

class ExpendList : BaseFragment() {

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            FragmentSupporterActivity.launcher(context, ExpendList::class.java, null)
        }
    }


    override fun layoutRes(): Int {
        return R.layout.fragment_expend_list
    }

    override fun onViewInit(view: View) {
        expendList.setAdapter(ExpandableListviewAdapter())
        expendList.setOnChildClickListener(object : ExpandableListView.OnChildClickListener {
            override fun onChildClick(p0: ExpandableListView?, p1: View?, p2: Int, p3: Int, p4: Long): Boolean {
                return true
            }
        })
    }

}