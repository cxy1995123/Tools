package chen.com.kotlin_test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

abstract class ChatBaseFragment : Fragment(), IMChatView, View.OnClickListener {
    
    protected var toolsBarIsShow = false
    protected var keyBoardIsShow = false
    
    var manager: SoftKeyBoardManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes(), container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager = SoftKeyBoardManager(this)
    }
    
    override fun onKeyBoardChange(isShow: Boolean, keyBoardHealth: Int) {
        keyBoardIsShow = isShow
        
        if (isShow) {
            val params: ViewGroup.LayoutParams? = getEmojView()?.getLayoutParams()
            if (params?.height != keyBoardHealth) {
                params?.height = keyBoardHealth
                getEmojView()?.layoutParams = params
            }
        }
        
        if (keyBoardIsShow && toolsBarIsShow) {
            toolsBarIsShow = false
            getEmojView()?.visibility = View.GONE
        }
    }
    
    override fun showEmojView() {
        if (keyBoardIsShow) {
            manager?.revertContentHeith()
            updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        }
        getEmojView()!!.visibility = View.VISIBLE
        toolsBarIsShow = true
        hideKeyBoard()
    }
    
    override fun hideEmojView() {
        updateSoftInputMethod(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getEmojView()!!.visibility = View.GONE
        toolsBarIsShow = false
    }
    
    override fun hideKeyBoard() {
        try {
            val manager = getInputEditText()?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(getInputEditText()?.windowToken, 0)
        } catch (e: Exception) {
        }
    }
    
    override fun updateSoftInputMethod(softInputMode: Int) {
        if (!isAdded) {
            val params: WindowManager.LayoutParams? = activity?.getWindow()?.getAttributes()
            if (params?.softInputMode != softInputMode) {
                params?.softInputMode = softInputMode
                activity!!.window.attributes = params
            }
        }
    }
    
    fun isBack(): Boolean {
        return !keyBoardIsShow && !toolsBarIsShow
    }
    
    override fun onClick(v: View?) {
        if (keyBoardIsShow) {
            hideKeyBoard()
        }
        if (toolsBarIsShow) {
            hideEmojView()
        }
    }
}