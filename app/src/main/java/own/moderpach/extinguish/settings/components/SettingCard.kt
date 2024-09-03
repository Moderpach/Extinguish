package own.moderpach.extinguish.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishCardDefault

@Composable
fun SettingCard(
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
) = ExtinguishCard(
    modifier,
    onClick,
    shape,
    containerColor,
    contentColor,
    tonalElevation,
    shadowElevation,
    border,
    contentPadding,
    horizontalAlignment,
    verticalArrangement,
    content
)