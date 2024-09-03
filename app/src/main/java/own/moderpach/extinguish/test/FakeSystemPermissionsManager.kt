package own.moderpach.extinguish.test

import android.util.Log
import own.moderpach.extinguish.ISystemPermissionsManager

private const val TAG = "FakeSystemPermissionsManager"

class FakeSystemPermissionsManager : ISystemPermissionsManager {
    override fun requestPermission(
        permission: String,
        onRequestPermissionRationale: () -> Unit,
        actionOnGranted: () -> Unit
    ) {
    }

    override val isPostNotificationGranted: Boolean
        get() = true

    override fun requestPostNotification() {}

    override val isDisplayOnOtherAppsGranted: Boolean = true

    override fun requestDisplayOnOtherApps() {
        Log.d(TAG, "requestDisplayOnOtherApps")
    }
}