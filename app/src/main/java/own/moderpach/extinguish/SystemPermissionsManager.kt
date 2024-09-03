package own.moderpach.extinguish

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SystemPermissionsManager(
    private val context: ComponentActivity
) : ISystemPermissionsManager {

    //manage system normal permissions
    private val actionsOnGranted: HashMap<String, () -> Unit> = hashMapOf()
    private val requestNormalPermissionLauncher = context.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (it.value) actionsOnGranted[it.key]?.let { action -> action() }
        }
    }


    override fun requestPermission(
        permission: String,
        onRequestPermissionRationale: () -> Unit,
        actionOnGranted: () -> Unit
    ) {
        actionsOnGranted[permission] = actionOnGranted
        when {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context, permission
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                onRequestPermissionRationale()
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.

                requestNormalPermissionLauncher.launch(arrayOf(permission))
            }
        }

    }

    @get:RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override val isPostNotificationGranted
        get() = context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun requestPostNotification() {
        requestPermission(
            android.Manifest.permission.POST_NOTIFICATIONS,
            {}
        ) {}
    }

    //manage system special permissions

    override val isDisplayOnOtherAppsGranted
        get() = Settings.canDrawOverlays(context)

    override fun requestDisplayOnOtherApps() {
        with(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) {
            context.startActivity(this)
        }
    }
}


