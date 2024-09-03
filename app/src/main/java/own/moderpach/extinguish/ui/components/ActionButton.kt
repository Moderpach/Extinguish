package own.moderpach.extinguish.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import own.moderpach.extinguish.R
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

/*
@Composable
fun ActionButton(
    icon: Painter,
    text: String,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        contentColor = MaterialTheme.colorScheme.onSurface
    ),
    onClick: () -> Unit,
) {
    ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.colorAtElevation(
            MaterialTheme.colorScheme.error,
            18.dp
        ), contentColor = MaterialTheme.colorScheme.onSurface
    )
    Button(
        onClick = onClick,
        colors = colors,
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 4.dp,
            end = 12.dp,
            bottom = 4.dp
        ),
        modifier = Modifier.height(36.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LargeActionButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: Painter,
    text: String,
    colors: CardColors,
    onClick: () -> Unit
) {
    val mModifier =
        if (enabled) modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
        else modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    OutlinedCard(
        colors = colors,
        modifier = if (enabled) Modifier else Modifier.alpha(0.5f)
    ) {
        Row(
            modifier = mModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = "",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

private object TimerButtonToken {
    val pressSpec = tween<Int>(
        durationMillis = 250,
        easing = LinearEasing
    )
}

@Composable
fun TimerButton(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 28.sp,
    shape: Shape = CircleShape,
    background: Color = Color.Transparent,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clickableWithPhysicalEffect(
                onClick = { onClick() }
            )
            .background(background)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            //AdaptiveText(text = text, style = MaterialTheme.typography.headlineLarge, color = tint)
            Text(text = text, color = tint, fontSize = fontSize)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconSize: Dp = 28.dp,
    shape: Shape = CircleShape,
    background: Color = Color.Transparent,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = onClick
) {
    Box(
        modifier = Modifier
            .clickableWithPhysicalEffect(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongClick()
                }
            )
            .background(background)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                painter = icon,
                contentDescription = "",
                modifier = Modifier.size(iconSize),
                tint = tint
            )
        }
    }
}

fun Modifier.clickableWithPhysicalEffect(
    onClick: (Offset) -> Unit,
    onDoubleClick: ((Offset) -> Unit)? = null,
    onLongClick: ((Offset) -> Unit)? = null,
) = composed {
    val indication = LocalIndication.current
    val interactionSource = remember {
        MutableInteractionSource()
    }
    this.clickableWithPhysicalEffect(
        interactionSource,
        indication,
        onClick, onDoubleClick, onLongClick
    )
}

fun Modifier.clickableWithPhysicalEffect(
    interactionSource: MutableInteractionSource,
    indication: Indication?,
    onClick: (Offset) -> Unit,
    onDoubleClick: ((Offset) -> Unit)? = null,
    onLongClick: ((Offset) -> Unit)? = null,
) = composed {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    val cornerPercent = remember {
        Animatable(100, Int.VectorConverter)
    }
    val shape = RoundedCornerShape(cornerPercent.value)
    this
        .clip(shape)
        .indication(interactionSource, indication)
        .hoverable(interactionSource)
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = onClick.let {
                    {
                        clickVibration(vibrator)
                        it(it)
                    }
                },
                onDoubleTap = onDoubleClick?.let {
                    {
                        clickVibration(vibrator)
                        it(it)
                    }
                },
                onLongPress = onLongClick?.let {
                    {
                        clickVibration(vibrator)
                        it(it)
                    }
                },
                onPress = {
                    val pressInteraction = PressInteraction.Press(it)
                    coroutineScope.launch {
                        interactionSource.emit(pressInteraction)
                    }
                    coroutineScope.launch {
                        cornerPercent.animateTo(20, TimerButtonToken.pressSpec)
                    }
                    tickVibration(vibrator)
                    awaitRelease()
                    coroutineScope.launch {
                        interactionSource.emit(PressInteraction.Release(pressInteraction))
                    }
                    coroutineScope.launch {
                        cornerPercent.animateTo(100, TimerButtonToken.pressSpec)
                    }
                }
            )
        }
}

private fun tickVibration(vibrator: Vibrator) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val effect =
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
        vibrator.vibrate(effect)
    }
}

private fun clickVibration(vibrator: Vibrator) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val effect =
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        vibrator.vibrate(effect)
    }
}

@Preview
@Composable
fun PreviewNumActionButton() {
    ExtinguishTheme {
        Column {
            Row {
                TimerButton(text = "1")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "2")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "3")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                TimerButton(text = "4")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "5")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "6")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                TimerButton(text = "7")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "8")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "9")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                TimerButton(text = "00")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(text = "0")
                Spacer(modifier = Modifier.width(4.dp))
                TimerButton(icon = painterResource(id = R.drawable.backspace_40px))
            }
        }
    }
}
*/