package own.moderpach.extinguish.test

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.SolutionsState

private const val TAG = "FakeSolutionStateManager"

class FakeSolutionStateManager : ISolutionsStateManager {
    override val state: StateFlow<SolutionsState> = MutableStateFlow(
        SolutionsState(
            isShizukuGranted = true,
            isShizukuRunning = true
        )
    )

    override fun initialize() {
        Log.d(TAG, "initialize")
    }

    override fun destroy() {
        Log.d(TAG, "destroy")
    }

    override fun update() {
        Log.d(TAG, "update")
    }

    override fun checkShizukuPermission(): Boolean {
        Log.d(TAG, "checkShizukuPermission")
        return true
    }

    override fun requestShizukuPermission() {
        Log.d(TAG, "requestShizukuPermission")
    }
}