package own.moderpach.extinguish

import kotlinx.coroutines.flow.StateFlow

data class SolutionsState(
    val isShizukuRunning: Boolean = false,
    val isShizukuGranted: Boolean = false
)

interface ISolutionsStateManager {
    val state: StateFlow<SolutionsState>
    fun initialize()
    fun destroy()
    fun update()
    fun checkShizukuPermission(): Boolean
    fun requestShizukuPermission()
}