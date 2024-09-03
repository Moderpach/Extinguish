package own.moderpach.extinguish.guide.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun PreviousAndNext(
    modifier: Modifier = Modifier,
    showPrevious: Boolean = true,
    showNext: Boolean = true,
    enabledPrevious: Boolean = true,
    enabledNext: Boolean = true,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
) {
    Row(
        modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            modifier = Modifier.alpha(
                if (showPrevious) 1f else 0f
            ),
            enabled = showPrevious && enabledPrevious,
            onClick = onClickPrevious
        ) {
            Text(stringResource(R.string.Previous))
        }
        Button(
            modifier = Modifier.alpha(
                if (showNext) 1f else 0f
            ),
            enabled = showNext && enabledNext,
            onClick = onClickNext
        ) {
            Text(stringResource(R.string.Next))
        }
    }
}

@Preview
@Composable
private fun PreviousAndNextPreview() = ExtinguishTheme {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PreviousAndNext(
            modifier = Modifier.fillMaxWidth(),
            onClickPrevious = {},
            onClickNext = {},
        )
        PreviousAndNext(
            modifier = Modifier.fillMaxWidth(),
            showPrevious = false,
            onClickPrevious = {},
            onClickNext = {},
        )
        PreviousAndNext(
            modifier = Modifier.fillMaxWidth(),
            showNext = false,
            onClickPrevious = {},
            onClickNext = {},
        )
        PreviousAndNext(
            modifier = Modifier.fillMaxWidth(),
            enabledPrevious = false,
            onClickPrevious = {},
            onClickNext = {},
        )
        PreviousAndNext(
            modifier = Modifier.fillMaxWidth(),
            enabledNext = false,
            onClickPrevious = {},
            onClickNext = {},
        )

    }
}