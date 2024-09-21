package own.moderpach.extinguish

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.StateFlow

interface ISolutionDependencyManager {
    val state: StateFlow<SolutionDependencyState>

    fun updateImmediately()
    fun requestShizukuPermission(onShouldShowRequestPermissionRationale: () -> Unit)
    fun destroy()
}

val LocalSolutionDependencyManager = compositionLocalOf<ISolutionDependencyManager> {
    createNoting()
}

private fun createNoting(): Nothing {
    throw IllegalAccessException("LocalSolutionDependencyManager should be provided manually")
}