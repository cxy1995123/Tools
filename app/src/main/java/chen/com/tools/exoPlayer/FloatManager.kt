package chen.com.tools.exoPlayer

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Process
import android.view.*
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import chen.com.library.tools.Util
import chen.com.tools.R
import com.google.android.exoplayer2.ui.PlayerView

class FloatManager {
    companion object {
        private var instance: FloatManager? = null
            get() {
                if (field == null) {
                    field = FloatManager()
                }
                return field
            }


        @Synchronized
        @JvmStatic
        fun get(): FloatManager? {
            return instance
        }
    }

    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var contentView: View? = null
    var statusBarHeight = 0

    fun destroyContentView() {
        if (contentView != null) {
            contentView?.setOnTouchListener(null)
            windowManager?.removeViewImmediate(contentView)
            contentView = null
        }
    }

    fun showFloatingWindow(context: Context?) {
        if (context == null) return
        if (contentView != null) return
        statusBarHeight = Util.getStatusBarHeight(context)
        val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", Process.myUid(), context.packageName)
        if (mode == 1 || mode == 0) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            // 新建悬浮窗控件
            contentView = View.inflate(context, R.layout.view_float_wndiwo2, null);
            // 设置LayoutParam
            layoutParams = WindowManager.LayoutParams()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                layoutParams?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            layoutParams?.format = PixelFormat.RGBA_8888 //窗口透明
            layoutParams?.gravity = Gravity.LEFT or Gravity.TOP //窗口位置
            layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams?.x = windowManager?.defaultDisplay?.width ?: 0 - 200
            layoutParams?.y = 0
            setListener(contentView)
            windowManager?.addView(contentView, layoutParams)

        } else {
            val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION",
                    Uri.parse("package:" + context.packageName))
            ContextCompat.startActivity(context, intent, null)
        }
    }

    private fun setListener(contentView: View?) {
        contentView?.setOnTouchListener(object : OnTouchListener {
            var startX = 0
            var startY = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x.toInt()
                        startY = event.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        layoutParams?.x = (event.rawX - startX).toInt()
                        layoutParams?.y = (event.rawY - startY - statusBarHeight).toInt()
                        windowManager?.updateViewLayout(contentView, layoutParams)
                        return true
                    }
                }
                return false
            }
        })
    }


}





