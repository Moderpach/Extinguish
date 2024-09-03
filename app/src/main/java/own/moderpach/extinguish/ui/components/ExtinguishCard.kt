package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ExtinguishCardDefault {
    val shape @Composable get() = MaterialTheme.shapes.extraLarge
    val containerColor @Composable get() = MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor @Composable get() = MaterialTheme.colorScheme.onSurface
    val tonalElevation = 1.dp
    val shadowElevation = 0.dp
    val border @Composable get() = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val contentPadding = PaddingValues(0.dp)
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start
    val verticalArrangement: Arrangement.Vertical = Arrangement.Top
}

@Composable
fun ExtinguishCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = ExtinguishCardDefault.shape,
    containerColor: Color = ExtinguishCardDefault.containerColor,
    contentColor: Color = ExtinguishCardDefault.contentColor,
    tonalElevation: Dp = ExtinguishCardDefault.tonalElevation,
    shadowElevation: Dp = ExtinguishCardDefault.shadowElevation,
    border: BorderStroke? = ExtinguishCardDefault.border,
    contentPadding: PaddingValues = ExtinguishCardDefault.contentPadding,
    horizontalAlignment: Alignment.Horizontal = ExtinguishCardDefault.horizontalAlignment,
    verticalArrangement: Arrangement.Vertical = ExtinguishCardDefault.verticalArrangement,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val clickableCheckedModifer = onClick?.let {
        Modifier.clickable(onClick = it)
    } ?: Modifier
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
    ) {
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .then(clickableCheckedModifer),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content
        )
    }
}