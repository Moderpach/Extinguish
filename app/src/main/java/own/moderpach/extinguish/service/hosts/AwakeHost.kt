package own.moderpach.extinguish.service.hosts

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
import own.moderpach.extinguish.util.ext.addFlags
import own.moderpach.extinguish.util.ext.clearFlags

class AwakeHost(
    val context: Context,
    layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.START or Gravity.TOP
        width = 1
        height = 1
        x = 0
        y = 0
        addFlags(FLAG_NOT_TOUCH_MODAL)
        addFlags(FLAG_NOT_FOCUSABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            addFlags(FLAG_LAYOUT_NO_LIMITS)
            addFlags(FLAG_LAYOUT_IN_SCREEN)
        }
        addFlags(FLAG_SPLIT_TOUCH)
    }
) {
    private val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val mView = View(context)
    private val mLayoutParams = layoutParams

    fun create() {
        if (!mView.isAttachedToWindow) windowManager.addView(mView, mLayoutParams)
    }

    fun startKeepAwake() {
        if (mView.isAttachedToWindow) {
            mLayoutParams.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            windowManager.updateViewLayout(mView, mLayoutParams)
        }
    }

    fun stopKeepAwake() {
        if (mView.isAttachedToWindow) {
            mLayoutParams.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            windowManager.updateViewLayout(mView, mLayoutParams)
        }
    }

    fun destroy() {
        if (mView.isAttachedToWindow) windowManager.removeView(mView)
    }
}