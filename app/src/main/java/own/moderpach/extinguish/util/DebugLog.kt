package own.moderpach.extinguish.util

import android.util.Log
import own.moderpach.extinguish.BuildConfig

fun debugLog(tag: String, msg: String) {
    if (BuildConfig.DEBUG) Log.d(tag, msg)
}