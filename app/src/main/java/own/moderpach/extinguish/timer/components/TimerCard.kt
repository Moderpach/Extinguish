package own.moderpach.extinguish.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.timer.data.TimerLiteral
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun TimerCard(
    modifier: Modifier = Modifier,
    timer: TimerLiteral,
    onDelete: (TimerLiteral) -> Unit,
) {
    val numStyle = SpanStyle(fontSize = 36.sp)
    val unitStyle = SpanStyle(fontSize = 20.sp, letterSpacing = 8.sp)
    val timerText = buildAnnotatedString {
        if (timer.hours != 0) {
            withStyle(numStyle) {
                append(timer.hours.toString())
            }
            withStyle(unitStyle) {
                append(stringResource(R.string.str_Hour))
            }
        }
        if (timer.minutes != 0) {
            withStyle(numStyle) {
                append(timer.minutes.toString())
            }
            withStyle(unitStyle) {
                append(stringResource(R.string.str_Minute))
            }
        }
        if (timer.seconds != 0) {
            withStyle(numStyle) {
                append(timer.seconds.toString())
            }
            withStyle(unitStyle) {
                append(stringResource(R.string.str_Second))
            }
        }
    }
    ExtinguishCard(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape
            ) {
                Icon(
                    painterResource(R.drawable.timer_24px),
                    null,
                    Modifier.padding(8.dp)
                )
            }
            IconButton(
                onClick = {
                    onDelete(timer)
                }
            ) {
                Icon(painterResource(R.drawable.delete_24px), stringResource(R.string.Delete))
            }
        }
        Text(timerText)
    }
}

@Preview
@Composable
private fun TimerCardPreview() = ExtinguishTheme {
    TimerCard(
        timer = TimerLiteral.fromSeconds(6435),
        onDelete = {}
    )
}
