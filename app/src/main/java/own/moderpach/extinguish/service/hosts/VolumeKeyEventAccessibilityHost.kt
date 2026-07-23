package own.moderpach.extinguish.service.hosts

import android.util.Log
import own.moderpach.extinguish.service.ExtinguishAccessibilityService

private const val TAG = "VolumeKeyEventAccessibilityHost"

class VolumeKeyEventAccessibilityHost(
    var onKeyEvent: () -> Unit
) {

    private val listener: () -> Unit = { onKeyEvent() }

    fun create() {
        ExtinguishAccessibilityService.setOnVolumeKeyEvent(listener)
    }

    fun sleep() {
        Log.d(TAG, "sleep: ")
        ExtinguishAccessibilityService.setAwake(false)
    }

    fun wake() {
        Log.d(TAG, "wake: ")
        ExtinguishAccessibilityService.setAwake(true)
    }

    fun destroy() {
        ExtinguishAccessibilityService.setAwake(false)
        ExtinguishAccessibilityService.setOnVolumeKeyEvent(null)
    }
}
