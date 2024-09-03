package own.moderpach.extinguish.timer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.timer.data.TimerLiteral
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun TimerListItem(
    modifier: Modifier = Modifier,
    timer: TimerLiteral,
    onDelete: (TimerLiteral) -> Unit,
) {
    ExtinguishListItem(
        leadingContent = {
            Icon(
                painterResource(R.drawable.timer_24px),
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineTextStyle = MaterialTheme.typography.titleMedium,
        headlineContent = {
            val timerText = buildAnnotatedString {
                if (timer.hours != 0) {
                    append(timer.hours.toString())
                    append(stringResource(R.string.str_Hour))
                }
                if (timer.minutes != 0) {
                    append(timer.minutes.toString())
                    append(stringResource(R.string.str_Minute))
                }
                if (timer.seconds != 0) {
                    append(timer.seconds.toString())
                    append(stringResource(R.string.str_Second))
                }
            }
            Text(timerText)
        },
        trailingContent = {
            IconButton(
                onClick = {
                    onDelete(timer)
                }
            ) {
                Icon(painterResource(R.drawable.delete_24px), stringResource(R.string.Delete))
            }
        }
    )
}

@Preview
@Composable
private fun TimerListItemPreview() = ExtinguishTheme {
    Box(Modifier.background(MaterialTheme.colorScheme.background)) {
        TimerListItem(
            timer = TimerLiteral.fromSeconds(6435),
            onDelete = {}
        )
    }
}