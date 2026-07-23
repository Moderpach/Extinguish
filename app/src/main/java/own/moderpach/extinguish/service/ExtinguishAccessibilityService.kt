package own.moderpach.extinguish.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "ExtinguishAccessibilityService"

/**
 * Intercepts volume keys without taking window focus, unlike the Window method.
 * Controlled by [own.moderpach.extinguish.service.hosts.VolumeKeyEventAccessibilityHost].
 */
class ExtinguishAccessibilityService : AccessibilityService() {

    companion object {
        @JvmStatic
        val isConnected = MutableStateFlow(false)

        @Volatile
        private var isAwake = false

        @Volatile
        private var onVolumeKeyEvent: (() -> Unit)? = null

        @JvmStatic
        fun setAwake(awake: Boolean) {
            isAwake = awake
        }

        @JvmStatic
        fun setOnVolumeKeyEvent(listener: (() -> Unit)?) {
            onVolumeKeyEvent = listener
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "onServiceConnected")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            serviceInfo = serviceInfo?.apply {
                flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
            }
        }
        isConnected.value = true
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val isVolumeKeyCode = event.keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
        if (isVolumeKeyCode && isAwake) {
            // consume DOWN too, otherwise the system still changes the volume
            if (event.action == KeyEvent.ACTION_UP) {
                onVolumeKeyEvent?.invoke()
            }
            return true
        }
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        super.onDestroy()
        isConnected.value = false
    }
}
