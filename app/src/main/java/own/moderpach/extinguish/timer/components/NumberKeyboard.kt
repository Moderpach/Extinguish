package own.moderpach.extinguish.timer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

sealed interface NumberKeyboardEvent {
    data class Number(val num: Int) : NumberKeyboardEvent
    data object BackSpace : NumberKeyboardEvent
    data object Clear : NumberKeyboardEvent
}

object BackSpaceIcon {
    val Small @Composable get() = painterResource(R.drawable.backspace_20px)
    val Medium @Composable get() = painterResource(R.drawable.backspace_24px)
}

@Composable
fun NumberKeyBoard(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    backSpaceIcon: Painter = BackSpaceIcon.Small,
    onEvent: (NumberKeyboardEvent) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..3) {
                KeyboardButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = i.toString(),
                    textStyle = textStyle,
                    onClick = {
                        onEvent(NumberKeyboardEvent.Number(i))
                    }
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 4..6) {
                KeyboardButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = i.toString(),
                    textStyle = textStyle,
                    onClick = {
                        onEvent(NumberKeyboardEvent.Number(i))
                    }
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 7..9) {
                KeyboardButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    text = i.toString(),
                    textStyle = textStyle,
                    onClick = {
                        onEvent(NumberKeyboardEvent.Number(i))
                    }
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = "00",
                textStyle = textStyle,
                onClick = {
                    onEvent(NumberKeyboardEvent.Number(0))
                    onEvent(NumberKeyboardEvent.Number(0))
                }
            )
            KeyboardButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = "0",
                textStyle = textStyle,
                onClick = { onEvent(NumberKeyboardEvent.Number(0)) }
            )
            KeyboardButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = backSpaceIcon,
                contentDescription = "backspace",
                onClick = { onEvent(NumberKeyboardEvent.BackSpace) }
            ) {
                onEvent(NumberKeyboardEvent.Clear)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeyboardButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    shape: Shape = RoundedCornerShape(50),
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .padding(2.dp)
            .size(64.dp),
        color = containerColor,
        contentColor = contentColor,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = textStyle)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeyboardButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    contentDescription: String?,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    shape: Shape = RoundedCornerShape(50),
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .padding(2.dp)
            .size(64.dp),
        color = containerColor,
        contentColor = contentColor,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription)
        }
    }
}

@Preview
@Composable
private fun NumberKeyBoardPreview() = ExtinguishTheme {
    Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
        NumberKeyBoard(
            modifier = Modifier.padding(16.dp)
        ) {}
    }
}