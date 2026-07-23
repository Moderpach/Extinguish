package own.moderpach.extinguish.service.hosts

import android.content.Context
import android.media.AudioManager
import android.util.Log
import extinguish.ipc.result.EventResult
import extinguish.shizuku_service.IEventsListener
import extinguish.shizuku_service.IEventsProvider

private const val TAG = "VolumeKeyEventShizukuHost"

class VolumeKeyEventShizukuHost(
    private val owner: Context,
    val service: IEventsProvider,
    var onKeyEvent: () -> Unit = {},
) {

    var isRegister = false
    var isAwake = false

    private val audioManager = owner.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val watchedStreams = intArrayOf(
        AudioManager.STREAM_MUSIC,
        AudioManager.STREAM_RING,
        AudioManager.STREAM_SYSTEM,
        AudioManager.STREAM_ALARM,
        AudioManager.STREAM_NOTIFICATION,
        AudioManager.STREAM_VOICE_CALL,
    )
    private var lockedVolumes: IntArray = IntArray(0)

    private fun restoreLockedVolume() {
        if (lockedVolumes.isEmpty()) return
        for (i in watchedStreams.indices) {
            try {
                audioManager.setStreamVolume(watchedStreams[i], lockedVolumes[i], 0)
            } catch (e: Exception) {
                Log.d(TAG, "restoreLockedVolume: failed for stream ${watchedStreams[i]}", e)
            }
        }
    }

    private fun captureLockedVolume() {
        lockedVolumes = IntArray(watchedStreams.size) { i ->
            try {
                audioManager.getStreamVolume(watchedStreams[i])
            } catch (e: Exception) {
                0
            }
        }
    }

    private val listener = object : IEventsListener.Stub() {
        override fun onEvent(event: EventResult) {
            Log.d(TAG, "get event - $event")
            if (isAwake && event.v0 == "0001" && (event.v1 == "0072" || event.v1 == "0073")) {
                restoreLockedVolume()
                if (event.v2 == "00000000") {
                    onKeyEvent()
                }
            }
        }
    }

    fun register() {
        if (!isRegister) {
            isRegister = true
            service.registerListener(listener)
            isAwake = true
            captureLockedVolume()
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
        captureLockedVolume()
    }

}