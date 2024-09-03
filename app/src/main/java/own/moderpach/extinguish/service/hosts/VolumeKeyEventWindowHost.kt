package own.moderpach.extinguish.service.hosts

import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import own.moderpach.extinguish.util.ext.addFlags

private const val TAG = "VolumeKeyEventWindowHost"

class VolumeKeyEventWindowHost(
    private val owner: Context,
    var onKeyEvent: () -> Unit
) {

    val windowManager = owner.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val mLayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.END or Gravity.BOTTOM
        width = 1
        height = 1

        addFlags(FLAG_NOT_TOUCH_MODAL)
        addFlags(FLAG_SPLIT_TOUCH)
    }

    private val listener = View.OnKeyListener { _, keyCode, event ->
        val isVolumeKeyCode = keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        if (isVolumeKeyCode && event.action == KeyEvent.ACTION_UP) {
            Log.d(TAG, "get key event $keyCode")
            onKeyEvent()
            return@OnKeyListener false
        }
        true
    }

    val mView = View(owner).apply {
        setOnKeyListener(listener)
    }

    fun create() {
        if (!mView.isAttachedToWindow)
            windowManager.addView(mView, mLayoutParams)
    }

    fun sleep() {
        if (mView.isAttachedToWindow) mView.isVisible = false
    }

    fun wake() {
        if (mView.isAttachedToWindow) mView.isVisible = true
    }

    fun destroy() {
        if (mView.isAttachedToWindow)
            windowManager.removeView(mView)
    }
}