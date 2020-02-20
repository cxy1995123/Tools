package chen.com.library.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment

class FragmentSupporterActivity : BaseActivity() {

    private  var fragmentName: String? = null

    companion object{
        val REQUEST_CODE = 0x0012

        @JvmStatic
        fun launcher(activity: Context, fragmentClass: Class<*>, bundle: Bundle?,
                     @StyleRes res: Int=-1) {
            val intent = Intent()
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            intent.putExtra("FragmentName", fragmentClass.name)
            intent.putExtra("res", res)
            intent.setClass(activity, FragmentSupporterActivity::class.java)
            activity.startActivity(intent)
        }
        @JvmStatic
        fun launcher(activity: Fragment?, fragmentClass: Class<*>, bundle: Bundle=Bundle(),
                     @StyleRes res: Int =-1,requestCode:Int = REQUEST_CODE) {
            launcher(activity?.activity,fragmentClass, bundle, res, requestCode)
        }

        @JvmStatic
        fun launcher(activity: Activity?, fragmentClass: Class<*>, bundle: Bundle= Bundle(),
                     @StyleRes res: Int =-1, requestCode:Int = REQUEST_CODE) {
            val intent = Intent()
            if (bundle != null) {
                intent.putExtras(bundle)
            }
            intent.putExtra("FragmentName", fragmentClass.name)
            intent.putExtra("res", res)
            intent.setClass(activity!!, FragmentSupporterActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val res = intent.getIntExtra("res", -1)
        if (res != -1) {
            setTheme(res)
        }
        super.onCreate(savedInstanceState)
        setContentView(chen.com.library.R.layout.ui_main_fragment)
        fragmentName = intent.getStringExtra("FragmentName")
        show(fragmentName!!)
    }



    private fun show(name: String) {
        val intent = intent
        val extras = intent.extras
        val fragment = Fragment.instantiate(this, name, extras)
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(chen.com.library.R.id.container, fragment, name)
        transaction.commitAllowingStateLoss()
    }
}
