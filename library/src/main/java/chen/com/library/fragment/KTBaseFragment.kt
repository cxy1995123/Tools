package chen.com.library.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import chen.com.library.tools.DisplayHelper

public abstract class KTBaseFragment : Fragment() {

    abstract fun layoutRes(): Int

    abstract fun onViewInit(view: View)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewInit(view)
    }

    /**
     * 单位转换: dp -> px
     */
    protected fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()).toInt()
    }

    /**
     * 单位转换:sp -> px
     */
    protected fun px2dp(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), getResources().getDisplayMetrics()).toInt()
    }


    private fun getScreenHeight(): Int {
        return DisplayHelper.getScreenHeight(getContext())
    }

    private fun getScreenWidth(): Int {
        return DisplayHelper.getScreenWidth(getContext())
    }


    protected fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    protected fun isEmpty(charSequence: CharSequence?): Boolean {
        return charSequence == null || charSequence.length == 0
    }

    fun onBackPress(): Boolean {
        return false
    }

}