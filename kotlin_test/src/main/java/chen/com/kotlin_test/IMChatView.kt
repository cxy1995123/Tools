package chen.com.kotlin_test

import android.app.Activity
import android.view.View
import android.widget.EditText

interface IMChatView {
    fun getActivity():Activity?
    fun onKeyBoardChange(isShow: Boolean, keyBoardHealth: Int)
    fun getEmojView(): View?
    fun showEmojView()
    fun hideEmojView()
    fun hideKeyBoard()
    fun updateSoftInputMethod(softInputMode: Int)
    fun getInputEditText(): EditText?
    fun layoutRes(): Int
}