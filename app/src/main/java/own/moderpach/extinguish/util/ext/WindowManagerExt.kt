package own.moderpach.extinguish.util.ext

import android.graphics.Rect
import android.os.Build
import android.view.WindowManager

fun WindowManager.LayoutParams.addFlags(flags: Int) {
    this.setFlags(flags, flags)
}

fun WindowManager.LayoutParams.clearFlags(flags: Int) {
    this.setFlags(0, flags)
}

fun WindowManager.LayoutParams.setFlags(flags: Int, mask: Int) {
    this.flags = this.flags and mask.inv() or (flags and mask)
}

fun getScreenRect(windowManager: WindowManager) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val r = Rect()
        r.set(windowManager.maximumWindowMetrics.bounds)
        r
    } else {
        val r = Rect()
        windowManager.defaultDisplay.getRectSize(r)
        r
    }

fun WindowManager.getScreenRectNew() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val r = Rect()
        r.set(this.maximumWindowMetrics.bounds)
        r
    } else {
        val r = Rect()
        this.defaultDisplay.getRectSize(r)
        r
    }