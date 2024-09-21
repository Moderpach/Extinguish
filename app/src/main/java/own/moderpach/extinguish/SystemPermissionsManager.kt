package own.moderpach.extinguish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class SystemPermissionsManager(
    private val context: Context,
) : ISystemPermissionsManager {

    //manage system normal permissions
    override fun request(
        permission: Permission,
        onRequestPermissionRationale: () -> Unit,
        launcher: ActivityResultLauncher<String>,
        activity: Activity
    ) {
        if (
            ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission.permissionName
            )
        ) {
            onRequestPermissionRationale()
            return
        }
        launcher.launch(permission.permissionName)
    }

    override fun check(permission: Permission): Boolean {
        return context.checkSelfPermission(
            permission.permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }

    //manage system special permissions

    override fun requestSpecial(permission: SpecificPermission) {
        if (checkSpecial(permission)) return
        when (permission) {
            SpecificPermission.CanDrawOverlays -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun checkSpecial(permission: SpecificPermission): Boolean {
        return when (permission) {
            SpecificPermission.CanDrawOverlays -> Settings.canDrawOverlays(context)
        }
    }
}


