package chen.com.tools.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import chen.com.library.tools.ArrayUtil
import chen.com.tools.R

class Menu(var title: String, val csvid: Long = -1) {

    val childList: MutableList<Menu> = mutableListOf()

    fun hasChild(): Boolean {
        return !ArrayUtil.isEmpty(childList)
    }

    fun childSize(): Int {
        return childList.size
    }

    fun addChild(child: Menu) {
        childList.add(child)
    }

    fun getChild(index: Int): Menu {
        return childList[index]
    }


}


class ExpandableListviewAdapter : BaseExpandableListAdapter() {

    private val groups = mutableListOf<Menu>()

    init {
        val menu1 = Menu("一岁内随访", -1)
        menu1.addChild(Menu("新生儿随访"))
        menu1.addChild(Menu("满月儿随访"))
        menu1.addChild(Menu("三月儿随访"))

        val menu2 = Menu("1-2岁半随访", -1)
        menu2.addChild(Menu("新生儿随访"))
        menu2.addChild(Menu("满月儿随访"))
        menu2.addChild(Menu("三月儿随访"))

        val menu3 = Menu("3-6岁儿童随访", -1)
        menu3.addChild(Menu("新生儿随访"))
        menu3.addChild(Menu("满月儿随访"))
        menu3.addChild(Menu("三月儿随访"))

        groups.add(menu1)
        groups.add(menu2)
        groups.add(menu3)

    }

    override fun getGroup(p0: Int): Menu {
        return groups[p0]
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(p0: Int, p1: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_text, parent, false)
        view.findViewById<TextView>(R.id.title).text = getGroup(p0).title
        return view
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_text2, parent, false)
        view.findViewById<TextView>(R.id.title).text = getChild(p0,p1).title
        return view
    }

    override fun getChildrenCount(p0: Int): Int {
        return groups[0].childSize()
    }

    override fun getChild(p0: Int, p1: Int): Menu {
        return groups[0].getChild(p1)
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }


    override fun getChildId(p0: Int, p1: Int): Long {
        return (p0 + p1).toLong()
    }

    override fun getGroupCount(): Int {
        return groups.size
    }
}