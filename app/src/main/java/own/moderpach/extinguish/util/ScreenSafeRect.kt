package own.moderpach.extinguish.util

import android.graphics.Rect
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
val NAVIGATION_BARS = WindowInsets.Type.navigationBars()

@RequiresApi(Build.VERSION_CODES.R)
val STATUS_BARS = WindowInsets.Type.statusBars()

@RequiresApi(Build.VERSION_CODES.R)
fun WindowManager.getScreenSafeRectIgnoringInsetsVisibility() = run {
    val maximumWindowRect = this.maximumWindowMetrics.bounds
    val windowInsets = this.maximumWindowMetrics.windowInsets
    val navigationBarRect = windowInsets.getInsetsIgnoringVisibility(NAVIGATION_BARS)
    val statusBarRect = windowInsets.getInsetsIgnoringVisibility(STATUS_BARS)
    val displayCutout = windowInsets.displayCutout

    val safeRect = Rect()
    safeRect.left = maximumWindowRect.left +
            own.moderpach.extinguish.util.ext.max(
                (displayCutout?.safeInsetLeft ?: 0),
                navigationBarRect.left,
                statusBarRect.left
            )
    safeRect.right = maximumWindowRect.right -
            own.moderpach.extinguish.util.ext.max(
                (displayCutout?.safeInsetRight ?: 0),
                navigationBarRect.right,
                statusBarRect.right
            )
    safeRect.top = maximumWindowRect.top +
            own.moderpach.extinguish.util.ext.max(
                (displayCutout?.safeInsetTop ?: 0),
                navigationBarRect.top,
                statusBarRect.top
            )
    safeRect.bottom = maximumWindowRect.bottom -
            own.moderpach.extinguish.util.ext.max(
                (displayCutout?.safeInsetBottom ?: 0),
                navigationBarRect.bottom,
                statusBarRect.bottom
            )

    safeRect
}