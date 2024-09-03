package own.moderpach.extinguish.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseOutCirc
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn

private const val EnterDuration = 150
private const val EnterDelay = 50
private const val ExitDuration = 100
private const val InitialScale = 0.98f

fun quickScaleIn(): EnterTransition = fadeIn(
    tween(
        durationMillis = EnterDuration,
        delayMillis = EnterDelay,
        easing = EaseOutCirc
    )
) + scaleIn(
    tween(
        durationMillis = EnterDuration,
        delayMillis = EnterDelay,
        easing = FastOutSlowInEasing
    ),
    initialScale = InitialScale
)

fun quickScaleOut(): ExitTransition = fadeOut(
    tween(
        durationMillis = ExitDuration,
        easing = EaseInCirc
    )
)