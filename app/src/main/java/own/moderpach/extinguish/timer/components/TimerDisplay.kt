package own.moderpach.extinguish.timer.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.timer.data.TimerLiteral

data class TimerDisplayStyle(
    val numStyle: SpanStyle,
    val unitStyle: SpanStyle,
    val color: Color,
    val highlightInput: Boolean,
    val highlightColor: Color,
)

object TimerDisplayDefault {
    val ExtraSmall
        @Composable get() = TimerDisplayStyle(
            SpanStyle(
                fontSize = 22.sp
            ),
            SpanStyle(
                fontSize = 12.sp,
                letterSpacing = 4.sp
            ),
            MaterialTheme.colorScheme.onSurface,
            true,
            MaterialTheme.colorScheme.primary
        )
    val Medium
        @Composable get() = TimerDisplayStyle(
            SpanStyle(
                fontSize = 36.sp
            ),
            SpanStyle(
                fontSize = 20.sp,
                letterSpacing = 8.sp
            ),
            MaterialTheme.colorScheme.onSurface,
            true,
            MaterialTheme.colorScheme.primary
        )
    val Large
        @Composable get() = TimerDisplayStyle(
            SpanStyle(
                fontSize = 40.sp
            ),
            SpanStyle(
                fontSize = 20.sp,
                letterSpacing = 8.sp
            ),
            MaterialTheme.colorScheme.onSurface,
            true,
            MaterialTheme.colorScheme.primary
        )
}

@Composable
fun TimerDisplay(
    modifier: Modifier = Modifier,
    timer: TimerLiteral,
    style: TimerDisplayStyle = TimerDisplayDefault.Medium,
) {
    val numStyle = style.numStyle
    val unitStyle = style.unitStyle
    val hasHoursInput = timer.hours != 0
    val hasMinutesInput = hasHoursInput || timer.minutes != 0
    val hasSecondsInput = hasMinutesInput || timer.seconds != 0
    val hoursColor = getTextColor(hasHoursInput, style)
    val minutesColor = getTextColor(hasMinutesInput, style)
    val secondsColor = getTextColor(hasSecondsInput, style)
    val timerText = buildAnnotatedString {
        withStyle(numStyle.copy(color = hoursColor)) {
            append(timer[5].toString())
            append(timer[4].toString())
        }
        withStyle(unitStyle.copy(color = hoursColor)) {
            append(stringResource(R.string.str_Hour))
        }
        withStyle(numStyle.copy(color = minutesColor)) {
            append(timer[3].toString())
            append(timer[2].toString())
        }
        withStyle(unitStyle.copy(color = minutesColor)) {
            append(stringResource(R.string.str_Minute))
        }
        withStyle(numStyle.copy(color = secondsColor)) {
            append(timer[1].toString())
            append(timer[0].toString())
        }
        withStyle(unitStyle.copy(color = secondsColor)) {
            append(stringResource(R.string.str_Second))
        }
    }
    Text(timerText, modifier, textAlign = TextAlign.Center)
}

private fun getTextColor(
    hasInput: Boolean,
    style: TimerDisplayStyle
): Color {
    if (!style.highlightInput) return style.color
    if (hasInput) return style.highlightColor
    return style.color
}