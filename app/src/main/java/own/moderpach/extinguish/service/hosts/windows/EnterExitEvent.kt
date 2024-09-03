package own.moderpach.extinguish.service.hosts.windows

import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "EnterExitEvent"

enum class EnterExitEvent {
    Enter,
    Exit
}

@Composable
fun OnEnterExitEvent(
    lifecycleOwner: LifecycleOwner,
    enterExitRequestFlow: Flow<EnterExitEvent>,
    onEnter: () -> Unit,
    onExit: () -> Unit,
) = LaunchedEffect(lifecycleOwner) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        withContext(Dispatchers.Main.immediate) {
            enterExitRequestFlow.collect {
                Log.d(TAG, "OnEnterExitEvent: $it")
                when (it) {
                    EnterExitEvent.Enter -> onEnter()
                    EnterExitEvent.Exit -> onExit()
                }
            }
        }
    }
}

object EnterExitAnimationDefault {
    val EnterTarget get() = 1f
    val ExitTarget get() = 0f
    val EnterAnimationSpec: AnimationSpec<Float> = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = 0.001f
    )
    val ExitAnimationSpec: AnimationSpec<Float> = spring(
        stiffness = Spring.StiffnessMedium,
        visibilityThreshold = 0.01f
    )
}