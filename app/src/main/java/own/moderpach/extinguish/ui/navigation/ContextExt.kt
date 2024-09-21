package own.moderpach.extinguish.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import own.moderpach.extinguish.R

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
    if (intent.resolveActivity(packageManager) != null) {
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
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        val text = getString(R.string.str_shizuku_not_found)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}