package own.moderpach.extinguish.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val TAG = "NavGraphBuilderExt"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.extinguishComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition = {
            slideInHorizontally(
                spring(stiffness = Spring.StiffnessMediumLow),
                initialOffsetX = { it })
        },
        exitTransition = {
            slideOutHorizontally(
                spring(stiffness = Spring.StiffnessMediumLow),
                targetOffsetX = { -it / 4 })
        },
        popEnterTransition = {
            slideInHorizontally(
                spring(stiffness = Spring.StiffnessMediumLow),
                initialOffsetX = { -it / 4 })
        },
        popExitTransition = {
            slideOutHorizontally(
                spring(stiffness = Spring.StiffnessMediumLow),
                targetOffsetX = { it })
        },
    ) { backStackEntry ->
        val slideAnimation = transition.animations.find {
            it.label == "Built-in slide"
        }
        val scrimColor = MaterialTheme.colorScheme.scrim
        Box(
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    val dimAlpha = slideAnimation?.let {
                        val initialX = (it.animation.initialValue as IntOffset).x
                        val targetX = (it.animation.targetValue as IntOffset).x
                        val currentX = (it.value as IntOffset).x
                        if (targetX - initialX == 0) 0f
                        else if (currentX < 0 && targetX < 0) currentX / 2f / targetX
                        else if (currentX < 0 && initialX < 0) currentX / 2f / initialX
                        else 0f
                    } ?: 0f
                    drawRect(
                        color = scrimColor.copy(dimAlpha),
                        size = size
                    )
                }
        ) {
            this@composable.content(backStackEntry)
        }
    }
}

