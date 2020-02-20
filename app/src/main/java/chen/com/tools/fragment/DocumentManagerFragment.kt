package chen.com.tools.fragment

import android.app.Activity
import chen.com.library.document.DocumentManagerFragment
import chen.com.tools.activity.FragmentSupporterActivity

class DocumentManagerFragment : DocumentManagerFragment() {
    companion object {
        @JvmStatic
        fun launch(activity: Activity) {
            FragmentSupporterActivity.launcher(activity, DocumentManagerFragment::class.java)
        }
    }
}