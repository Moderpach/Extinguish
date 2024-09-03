package own.moderpach.extinguish.ui.components

import android.app.Activity
import android.content.Context
import android.view.Window
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat

private const val ANIMATION_DURATION = 400


@Composable
fun ExtinguishBottomDialog(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    background: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 8.dp,
    dialogState: DialogState,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    ExtinguishDialog(
        dialogState = dialogState,
        properties = properties,
        onDismissRequest = onDismissRequest
    ) {
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = dialogState.isShowing,
            enter = slideInVertically(
                tween(ANIMATION_DURATION, easing = FastOutSlowInEasing),
                initialOffsetY = { it }),
            exit = slideOutVertically(
                tween(ANIMATION_DURATION, easing = FastOutSlowInEasing),
                targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .then(modifier)
                    .clip(shape = shape)
                    .shadow(elevation, shape)
                    .background(background),
                content = content
            )
        }
    }
}

@Composable
fun ExtinguishDialog(
    dialogState: DialogState,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    BackHandler(properties.dismissOnBackPress) {
        dialogState.dismiss()
        onDismissRequest.invoke()
    }
    AnimatedVisibility(
        visible = dialogState.isShowing,
        enter = fadeIn(tween(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing))
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = properties.dismissOnClickOutside
            ) {
                dialogState.dismiss()
                onDismissRequest.invoke()
            }
        )
    }
    Box(modifier = Modifier.fillMaxSize(), content = content)
}

@Composable
fun rememberDialogState(
    showInitially: Boolean = false,
) = remember {
    DialogState(showInitially)
}

open class DialogState(
    showInitially: Boolean = false,
) {
    var isShowing by mutableStateOf(showInitially)
        private set

    fun show() = run {
        isShowing = true
    }

    fun dismiss() = run {
        isShowing = false
    }
}