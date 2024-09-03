package extinguish.shizuku_service

import android.os.Build
import android.util.Log
import extinguish.hiddenAPI.server.display.DisplayControlProxy
import extinguish.hiddenAPI.view.SurfaceControlProxy
import extinguish.shizuku_service.result.IntResult
import extinguish.shizuku_service.result.UnitResult
import kotlin.system.exitProcess

class DisplayControlService : IDisplayControl.Stub() {
    companion object {

        const val TAG = "DisplayControlService"

        private fun debugLog(msg: String) {
            if (BuildConfig.DEBUG) Log.d(TAG, msg)
        }

        const val POWER_MODE_OFF = SurfaceControlProxy.POWER_MODE_OFF
        const val POWER_MODE_NORMAL = SurfaceControlProxy.POWER_MODE_NORMAL

        const val BRIGHTNESS_MODE_MANUAL = 0
        const val BRIGHTNESS_MODE_AUTO = 1

        const val ERR_ILLEGAL_PARAMETER = 1
        const val ERR_FROM_HIDDEN_API = 2
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

    override fun setPowerModeToSurfaceControl(mode: Int): UnitResult {
        debugLog("#setPowerModeToSurfaceControl(mode = $mode)")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                debugLog("setPowerModeToSurfaceControl: getting displayToken from DisplayControl")
                val mainDisplayId = DisplayControlProxy.getPhysicalDisplayIds()[0]
                debugLog("setPowerModeToSurfaceControl: mainDisplayId = $mainDisplayId")
                with(DisplayControlProxy.getPhysicalDisplayToken(mainDisplayId)) {
                    SurfaceControlProxy.setDisplayPowerMode(
                        this, mode
                    )
                }
            } else {
                debugLog("setPowerModeToSurfaceControl: getting displayToken from SurfaceControl")
                with(SurfaceControlProxy.getInternalDisplayToken()) {
                    SurfaceControlProxy.setDisplayPowerMode(
                        this, mode
                    )
                }
            }
        } catch (e: Exception) {
            return UnitResult.Err(ERR_FROM_HIDDEN_API, e.toString())
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessToSurfaceControl(brightness: Float): UnitResult {
        debugLog("#setBrightnessToSurfaceControl(brightness = $brightness)")
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                debugLog("setBrightnessToSurfaceControl: getting displayToken from DisplayControl")
                val mainDisplayId = DisplayControlProxy.getPhysicalDisplayIds()[0]
                debugLog("setBrightnessToSurfaceControl: mainDisplayId = $mainDisplayId")
                with(DisplayControlProxy.getPhysicalDisplayToken(mainDisplayId)) {
                    SurfaceControlProxy.setDisplayBrightness(
                        this, brightness
                    )
                }
            } else {
                debugLog("setBrightnessToSurfaceControl: getting displayToken from SurfaceControl")
                with(SurfaceControlProxy.getInternalDisplayToken()) {
                    SurfaceControlProxy.setDisplayBrightness(
                        this, brightness
                    )
                }
            }
        } catch (e: Exception) {
            return UnitResult.Err(ERR_FROM_HIDDEN_API, e.toString())
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessToSetting(brightness: Int): UnitResult {
        debugLog("#setBrightnessToSetting(brightness = $brightness)")
        val output = shProcess.outputStream
        synchronized(shLock) {
            output.write("settings put system screen_brightness $brightness\n".toByteArray())
            output.flush()
        }
        return UnitResult.Ok()
    }

    override fun setBrightnessModeToSetting(mode: Int): UnitResult {
        debugLog("#setBrightnessModeToSetting(mode = $mode)")
        val output = shProcess.outputStream
        synchronized(shLock) {
            when (mode) {
                BRIGHTNESS_MODE_MANUAL, BRIGHTNESS_MODE_AUTO -> {
                    output.write("settings put system screen_brightness_mode $mode\n".toByteArray())
                    output.flush()
                }

                else -> return UnitResult.Err(
                    ERR_ILLEGAL_PARAMETER,
                    "`mode` should be DisplayControlService#BRIGHTNESS_MODE_MANUAL " +
                            "or DisplayControlService#BRIGHTNESS_MODE_AUTO"
                )
            }
        }
        return UnitResult.Ok()
    }

    override fun destroy() {
        debugLog("#destroy()")
        synchronized(shLock) {
            _shProcess?.destroy()
            _shProcess = null
        }
        exitProcess(0)
    }

}
