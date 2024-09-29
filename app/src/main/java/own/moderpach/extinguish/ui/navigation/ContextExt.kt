package own.moderpach.extinguish.ui.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.net.Uri
import android.widget.Toast
import own.moderpach.extinguish.R

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
    if (hasTargetActivity(intent)) {
        startActivity(intent)
    } else {
        val text = getString(R.string.str_browser_not_found)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

fun Context.openShizuku() {
    val intent = Intent().setClassName(
        "moe.shizuku.privileged.api",
        "moe.shizuku.manager.MainActivity"
    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (hasTargetActivity(intent)) {
        startActivity(intent)
    } else {
        val text = getString(R.string.str_shizuku_not_found)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

fun Context.hasTargetActivity(intent: Intent): Boolean {
    return packageManager.queryIntentActivities(intent, MATCH_DEFAULT_ONLY).size > 0
}