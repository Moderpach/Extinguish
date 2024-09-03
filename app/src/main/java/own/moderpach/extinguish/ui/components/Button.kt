package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

@Composable
fun RoundedCornerButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    shape: Shape = ShapeDefaults.ExtraSmall,
    borderStroke: BorderStroke? = null,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color =
        if (enabled)
            MaterialTheme.colorScheme.onSecondaryContainer
        else
            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .then(modifier)
            .clip(shape)
            .background(containerColor)
            .clickable(enabled, onClick = onClick)
            .then(
                if (borderStroke != null) {
                    Modifier.border(
                        borderStroke,
                        shape
                    )
                } else {
                    Modifier
                }
            )
            .then(
                if (enabled) {
                    Modifier
                } else {
                    Modifier.alpha(0.8f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.W600
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Preview
@Composable
fun PreviewButton() {
    ExtinguishTheme {
        Column {
            RoundedCornerButton(onClick = { }) {
                Text(text = "这是一个按钮")
            }
        }
    }
}