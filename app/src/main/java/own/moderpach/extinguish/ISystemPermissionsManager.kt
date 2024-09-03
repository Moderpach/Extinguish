package own.moderpach.extinguish

import androidx.activity.result.ActivityResultLauncher

interface ISystemPermissionsManager {
    fun requestPermission(
        permission: String,
        onRequestPermissionRationale: () -> Unit,
        actionOnGranted: () -> Unit
    )

    val isPostNotificationGranted: Boolean
    fun requestPostNotification()


    //manage system normal permissions
    val isDisplayOnOtherAppsGranted: Boolean

    fun requestDisplayOnOtherApps()

}