package own.moderpach.extinguish

import android.app.Activity
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.compositionLocalOf

enum class Permission(val permissionName: String) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    PostNotification(android.Manifest.permission.POST_NOTIFICATIONS)
}

enum class SpecificPermission {
    CanDrawOverlays
}

val LocalSystemPermissionsManager = compositionLocalOf<ISystemPermissionsManager> {
    createNoting()
}

private fun createNoting(): Nothing {
    throw IllegalAccessException("SystemPermissionsManager should be provided manually")
}

interface ISystemPermissionsManager {

    fun request(
        permission: Permission,
        onRequestPermissionRationale: () -> Unit,
        launcher: ActivityResultLauncher<String>,
        activity: Activity
    )

    fun check(
        permission: Permission
    ): Boolean

    fun requestSpecial(
        permission: SpecificPermission
    )

    fun checkSpecial(
        permission: SpecificPermission
    ): Boolean

}