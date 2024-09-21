package own.moderpach.extinguish.test

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.Permission
import own.moderpach.extinguish.SpecificPermission

private const val TAG = "FakeSystemPermissionsManager"

class FakeSystemPermissionsManager : ISystemPermissionsManager {
    override fun request(
        permission: Permission,
        onRequestPermissionRationale: () -> Unit,
        launcher: ActivityResultLauncher<String>,
        activity: Activity
    ) {
    }

    override fun check(permission: Permission): Boolean {
        return false
    }

    override fun requestSpecial(permission: SpecificPermission) {}

    override fun checkSpecial(permission: SpecificPermission): Boolean {
        return false
    }

}