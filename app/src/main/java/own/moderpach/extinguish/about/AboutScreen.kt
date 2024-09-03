package own.moderpach.extinguish.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.ui.navigation.extinguishComposable

val ExtinguishNavGraph.About: ExtinguishNavRoute get() = "home"

fun NavGraphBuilder.about(
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    onNavigateBack: () -> Unit,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.About,
) {
    AboutScreen(solutionsStateManager, systemPermissionsManager, onNavigateBack, onNavigateTo)
}

@Composable
fun AboutScreen(
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    onNavigateBack: () -> Unit,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    LocalContext
}