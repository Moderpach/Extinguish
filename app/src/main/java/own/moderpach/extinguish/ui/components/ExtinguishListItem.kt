package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun ExtinguishListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: @Composable() (() -> Unit)? = null,
    supportingContent: @Composable() (() -> Unit)? = null,
    leadingContent: @Composable() (RowScope.() -> Unit)? = null,
    trailingContent: @Composable() (RowScope.() -> Unit)? = null,
    shape: Shape = ListItemDefaults.shape,
    containerColor: Color = Color.Transparent,
    contentColor: Color = ListItemDefaults.contentColor,
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    onClick: (() -> Unit)? = null,
    morePaddingForPureText: Boolean = true,
    headlineTextStyle: TextStyle = MaterialTheme.typography.titleMedium,
    overlineTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    supportingTextStyle: TextStyle = MaterialTheme.typography.bodySmall
) {

    val minHeight =
        if (overlineContent == null && supportingContent == null) 56.dp
        else if (overlineContent != null && supportingContent != null) 88.dp
        else 72.dp

    val decoratedLeadingContent: @Composable (RowScope.() -> Unit)? = leadingContent?.let {
        { LeadingContent(content = it) }
    }

    val decoratedTrailingContent: @Composable (RowScope.() -> Unit)? = trailingContent?.let {
        { TrailingContent(content = it) }
    }

    val outerPaddingValues =
        PaddingValues(
            start = if (leadingContent == null && morePaddingForPureText) 24.dp else 16.dp,
            top = if (supportingContent == null && overlineContent == null) 8.dp else 16.dp,
            end = if (trailingContent == null && morePaddingForPureText) 24.dp else 16.dp,
            bottom = if (supportingContent == null && overlineContent == null) 8.dp else 16.dp,
        )

    val clickableCheckedModifier = onClick?.let {
        modifier.clickable(onClick = onClick)
    } ?: modifier

    ExtinguishListItem(
        modifier = clickableCheckedModifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        minHeight = minHeight,
        paddingValues = outerPaddingValues,
    ) {
        if (decoratedLeadingContent != null) {
            decoratedLeadingContent()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            if (overlineContent != null) {
                ProvideTextStyle(
                    overlineTextStyle
                ) {
                    overlineContent()
                }
            }
            ProvideTextStyle(
                headlineTextStyle
            ) {
                headlineContent()
            }
            if (supportingContent != null) {
                ProvideTextStyle(
                    supportingTextStyle
                ) {
                    supportingContent()
                }
            }
        }
        if (decoratedTrailingContent != null) {
            decoratedTrailingContent()
        }
    }
}

@Composable
private fun ExtinguishListItem(
    modifier: Modifier = Modifier,
    shape: Shape = ListItemDefaults.shape,
    containerColor: Color = ListItemDefaults.containerColor,
    contentColor: Color = ListItemDefaults.contentColor,
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    minHeight: Dp,
    paddingValues: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = minHeight)
                .padding(paddingValues)
                .semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
private fun RowScope.LeadingContent(
    content: @Composable RowScope.() -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    content = content
)

@Composable
private fun RowScope.TrailingContent(
    content: @Composable RowScope.() -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    content = content
)

@Preview
@Composable
fun ListItemPreview() {
    ExtinguishTheme {
        androidx.compose.material3.ListItem(headlineContent = { })
    }
}