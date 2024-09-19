package extinguish.shizuku_service

import android.os.Build
import android.os.IBinder
import android.util.Log
import extinguish.hiddenAPI.server.display.DisplayControlProxy
import extinguish.hiddenAPI.view.SurfaceControlProxy
import extinguish.ipc.result.UnitResult
import kotlin.system.exitProcess

class DisplayControlService : IDisplayControl.Stub() {
    companion object {

        const val TAG = "DisplayControlService"

        const val POWER_MODE_OFF = SurfaceControlProxy.POWER_MODE_OFF
        const val POWER_MODE_NORMAL = SurfaceControlProxy.POWER_MODE_NORMAL

        const val BRIGHTNESS_MODE_MANUAL = 0
        const val BRIGHTNESS_MODE_AUTO = 1
    }

    private val shLock = Object()
    private var _shProcess: Process? = null
    val shProcess: Process
        get() = synchronized(shLock) {
            if (_shProcess?.isAlive == true) _shProcess
            Runtime.getRuntime().exec(arrayOf("sh")).also {
                _shProcess = it
            }
        }

    private fun getPrimaryPhysicalDisplayToken(): IBinder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val primaryPhysicalDisplayId = DisplayControlProxy.getPhysicalDisplayIds()[0]
            return DisplayControlProxy.getPhysicalDisplayToken(primaryPhysicalDisplayId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return SurfaceControlProxy.getInternalDisplayToken()
        }
        return SurfaceControlProxy.getBuiltInDisplay()
    }

    override fun setPowerModeToSurfaceControl(mode: Int): UnitResult {
        try {
            val displayToken = getPrimaryPhysicalDisplayToken()
            SurfaceControlProxy.setDisplayPowerMode(
                displayToken, mode
            )
        } catch (e: Exception) {
            return UnitResult.Err(e)
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessToSurfaceControl(brightness: Float): UnitResult {
        try {
            val displayToken = getPrimaryPhysicalDisplayToken()
            SurfaceControlProxy.setDisplayBrightness(
                displayToken, brightness
            )
        } catch (e: Exception) {
            return UnitResult.Err(e)
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessToSetting(brightness: Int): UnitResult {
        val output = shProcess.outputStream
        synchronized(shLock) {
            output.write("settings put system screen_brightness $brightness\n".toByteArray())
            output.flush()
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessModeToSetting(mode: Int): UnitResult {
        if (mode != BRIGHTNESS_MODE_MANUAL && mode != BRIGHTNESS_MODE_AUTO) {
            return UnitResult.Err(
                IllegalArgumentException("mode should be 0 or 1")
            )
        }
        val output = shProcess.outputStream
        synchronized(shLock) {
            output.write("settings put system screen_brightness_mode $mode\n".toByteArray())
            output.flush()
        }
        return UnitResult.Ok()
    }

    override fun destroy() {
        Log.d(TAG, "destroy: $TAG")
        synchronized(shLock) {
            _shProcess?.destroy()
            _shProcess = null
        }
        exitProcess(0)
    }

}
