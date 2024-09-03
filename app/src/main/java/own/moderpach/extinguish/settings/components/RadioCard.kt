package own.moderpach.extinguish.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishCardDefault
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun RadioCard(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)?,
    headline: String,
    supporting: String?
) {
    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    else ExtinguishCardDefault.border
    ExtinguishCard(
        border = border,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        onClick = onClick,
    ) {
        ExtinguishListItem(
            headlineContent = {
                Text(headline)
            },
            trailingContent = {
                RadioButton(selected, null)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            morePaddingForPureText = false
        )
        supporting?.let {
            Text(
                supporting,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RadioCardPreview() = ExtinguishTheme {
    RadioCard(
        selected = true,
        headline = "ABCD",
        onClick = {},
        supporting = "Test text".repeat(25)
    )
}