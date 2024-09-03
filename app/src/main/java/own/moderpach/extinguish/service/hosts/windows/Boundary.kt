package own.moderpach.extinguish.service.hosts.windows

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import own.moderpach.extinguish.util.ext.getScreenRectNew
import own.moderpach.extinguish.util.getScreenSafeRectIgnoringInsetsVisibility

typealias Boundary = Rect

fun WindowManager.calculateSafePositionBoundary(
    windowSize: IntSize
): Rect {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val safe = this.getScreenSafeRectIgnoringInsetsVisibility()
        Rect(safe).apply {
            right -= windowSize.width
            bottom -= windowSize.height
        }
    } else {
        val screen = this.getScreenRectNew()
        Rect(screen).apply {
            right -= windowSize.width
            bottom -= windowSize.height
        }
    }
}