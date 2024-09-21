package own.moderpach.extinguish

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AppViewModel : ViewModel() {
    val solutionDependencyState = MutableStateFlow(SolutionDependencyState())
    val permissionState = MutableStateFlow(PermissionState())
}

data class PermissionState(
    val isPostNotificationGranted: Boolean = false,
    val isDisplayOnOtherAppsGranted: Boolean = false,
)