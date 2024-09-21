package own.moderpach.extinguish.service.hosts

import android.content.Context
import android.util.Log
import extinguish.ipc.result.EventResult
import extinguish.shizuku_service.IEventsListener
import extinguish.shizuku_service.IEventsProvider

private const val TAG = "ScreenEventHost"

class ScreenEventHost(
    private val owner: Context,
    val service: IEventsProvider,
    var onScreenEvent: () -> Unit = {},
) {
    var isRegister = false
    var isAwake = false

    private val listener = object : IEventsListener.Stub() {
        override fun onEvent(event: EventResult) {
            Log.d(TAG, "get event $event")
            Log.d(TAG, "isAwake = $isAwake")
            if (isAwake && event.v0 == "0001" && event.v1 == "014a" && event.v2 == "00000000") {
                onScreenEvent()
            }
        }
    }

    fun register() {
        if (!isRegister) {
            isRegister = true
            service.registerListener(listener)
            isAwake = true
        }
    }

    fun unregister() {
        if (isRegister) {
            isRegister = false
            service.unregisterListener(listener)
            isAwake = false
        }
    }

    fun sleep() {
        isAwake = false
    }

    fun wake() {
        isAwake = true
    }

}