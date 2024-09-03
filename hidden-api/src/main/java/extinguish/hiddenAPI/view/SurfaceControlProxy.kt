package extinguish.hiddenAPI.view

import android.os.IBinder
import java.lang.reflect.Method

/**
 * Proxy class for android.view.SurfaceControl.
 *
 * Get display token from SurfaceControl is no longer possible
 * since android 14.
 * */
class SurfaceControlProxy {
    companion object {
        private val CLASS: Class<*> by lazy {
            Class.forName("android.view.SurfaceControl")
        }

        const val POWER_MODE_OFF = 0
        const val POWER_MODE_NORMAL = 2


        private val METHOD_getInternalDisplayToken: Method by lazy {
            CLASS.getMethod("getInternalDisplayToken")
        }

        @Deprecated("Removed since Android 14.")
        fun getInternalDisplayToken(): IBinder {
            return METHOD_getInternalDisplayToken
                .invoke(null) as IBinder
        }

        private val METHOD_getBuiltInDisplay: Method by lazy {
            CLASS.getMethod("getBuiltInDisplay", Int::class.java)
        }

        @Deprecated(
            message = "Removed since Android 10",
            ReplaceWith("getInternalDisplayToken()")
        )
        fun getBuiltInDisplay(): IBinder {
            return METHOD_getBuiltInDisplay
                .invoke(null, 0) as IBinder
        }

        private val METHOD_setDisplayPowerMode: Method by lazy {
            CLASS.getMethod("setDisplayPowerMode", IBinder::class.java, Int::class.java)
        }

        /**
         * Sets the power mode of a display.
         *
         * @param displayToken
         *      The token for the display whose power mode is set.
         * @param mode
         *      Mode can be POWER_MODE_OFF or POWER_MODE_NORMAL.
         *
         */
        fun setDisplayPowerMode(displayToken: IBinder?, mode: Int) {
            METHOD_setDisplayPowerMode
                .invoke(null, displayToken, mode)
        }

        private val METHOD_setDisplayBrightness: Method by lazy {
            CLASS.getMethod("setDisplayBrightness", IBinder::class.java, Float::class.java)
        }
        private val METHOD_setDisplayBrightness2: Method by lazy {
            CLASS.getMethod(
                "setDisplayBrightness",
                IBinder::class.java,
                Float::class.java,
                Float::class.java,
                Float::class.java,
                Float::class.java
            )
        }

        /**
         * Sets the brightness of a display.
         *
         * @param displayToken
         *      The token for the display whose brightness is set.
         * @param brightness
         *      A number between 0.0f (minimum brightness) and 1.0f (maximum brightness), or -1.0f to
         *      turn the backlight off.
         *
         * @return Whether the method succeeded or not.
         *
         * @throws IllegalArgumentException if:
         *      - displayToken is null;
         *      - brightness is NaN or greater than 1.0f.
         *
         */
        fun setDisplayBrightness(displayToken: IBinder?, brightness: Float): Boolean {
            return METHOD_setDisplayBrightness
                .invoke(null, displayToken, brightness) as Boolean
        }

        /**
         * Sets the brightness of a display.
         *
         * @param displayToken
         *      The token for the display whose brightness is set.
         * @param brightness
         *      A number between 0.0f (minimum brightness) and 1.0f (maximum brightness), or -1.0f to
         *      turn the backlight off.
         *
         * @return Whether the method succeeded or not.
         *
         * @throws IllegalArgumentException if:
         *      - displayToken is null;
         *      - brightness is NaN or greater than 1.0f.
         *
         */
        fun setDisplayBrightness(
            displayToken: IBinder?,
            sdrBrightness: Float,
            sdrBrightnessNits: Float,
            displayBrightness: Float,
            displayBrightnessNits: Float,
        ): Boolean {
            return METHOD_setDisplayBrightness2
                .invoke(
                    null,
                    displayToken,
                    sdrBrightness,
                    sdrBrightnessNits,
                    displayBrightness,
                    displayBrightnessNits
                ) as Boolean
        }
    }
}