package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.R
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

object ExtinguishButtonDefault {
    interface Style {
        val shape: Shape
        val contentColor: Color
        val containerColor: Color
        val shadowElevation: Dp
        val tonalElevation: Dp
        val border: BorderStroke
    }

    val outlinedButton
        @Composable get() = object : Style {
            override val shape: Shape = RoundedCornerShape(50)
            override val contentColor: Color = MaterialTheme.colorScheme.onSurface
            override val containerColor: Color = Color.Transparent
            override val shadowElevation: Dp = 0.dp
            override val tonalElevation: Dp = 0.dp
            override val border: BorderStroke =
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }

    val outlinedIconButton
        @Composable get() = object : Style {
            override val shape: Shape = CircleShape
            override val contentColor: Color = MaterialTheme.colorScheme.onSurface
            override val containerColor: Color = Color.Transparent
            override val shadowElevation: Dp = 0.dp
            override val tonalElevation: Dp = 0.dp
            override val border: BorderStroke =
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }

}

object ExtinguishButtonToken {
    val minSize = 36.dp
    val padding = PaddingValues(12.dp, 4.dp)
    val spacing = 8.dp
}

@Composable
fun ExtinguishOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ExtinguishButtonDefault.outlinedButton.shape,
    contentColor: Color = ExtinguishButtonDefault.outlinedButton.contentColor,
    containerColor: Color = ExtinguishButtonDefault.outlinedButton.containerColor,
    shadowElevation: Dp = ExtinguishButtonDefault.outlinedButton.shadowElevation,
    tonalElevation: Dp = ExtinguishButtonDefault.outlinedButton.tonalElevation,
    border: BorderStroke? = ExtinguishButtonDefault.outlinedButton.border,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: Painter? = null,
    text: String
) = ExtinguishOutlinedButton(
    onClick,
    modifier,
    enabled,
    shape,
    contentColor,
    containerColor,
    shadowElevation,
    tonalElevation,
    border,
    interactionSource
) {
    icon?.let {
        Icon(it, null)
    }
    Text(text)
}

@Composable
fun ExtinguishOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ExtinguishButtonDefault.outlinedButton.shape,
    contentColor: Color = ExtinguishButtonDefault.outlinedButton.contentColor,
    containerColor: Color = ExtinguishButtonDefault.outlinedButton.containerColor,
    shadowElevation: Dp = ExtinguishButtonDefault.outlinedButton.shadowElevation,
    tonalElevation: Dp = ExtinguishButtonDefault.outlinedButton.tonalElevation,
    border: BorderStroke? = ExtinguishButtonDefault.outlinedButton.border,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = Surface(
    onClick = onClick,
    modifier = modifier.semantics { role = Role.Button },
    enabled = enabled,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    shadowElevation = shadowElevation,
    tonalElevation = tonalElevation,
    border = border,
    interactionSource = interactionSource
) {
    ProvideTextStyle(
        MaterialTheme.typography.labelLarge
    ) {
        Box(
            modifier = Modifier.heightIn(min = ExtinguishButtonToken.minSize),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(ExtinguishButtonToken.padding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ExtinguishButtonToken.spacing),
                content = content
            )
        }
    }
}

@Composable
fun ExtinguishOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ExtinguishButtonDefault.outlinedIconButton.shape,
    contentColor: Color = ExtinguishButtonDefault.outlinedIconButton.contentColor,
    containerColor: Color = ExtinguishButtonDefault.outlinedIconButton.containerColor,
    shadowElevation: Dp = ExtinguishButtonDefault.outlinedIconButton.shadowElevation,
    tonalElevation: Dp = ExtinguishButtonDefault.outlinedButton.tonalElevation,
    border: BorderStroke? = ExtinguishButtonDefault.outlinedIconButton.border,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    icon: Painter,
    contentDescription: String
) = ExtinguishOutlinedIconButton(
    onClick,
    modifier,
    enabled,
    shape,
    contentColor,
    containerColor,
    shadowElevation,
    tonalElevation,
    border,
    interactionSource
) {
    Icon(icon, contentDescription)
}

@Composable
fun ExtinguishOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ExtinguishButtonDefault.outlinedIconButton.shape,
    contentColor: Color = ExtinguishButtonDefault.outlinedIconButton.contentColor,
    containerColor: Color = ExtinguishButtonDefault.outlinedIconButton.containerColor,
    shadowElevation: Dp = ExtinguishButtonDefault.outlinedIconButton.shadowElevation,
    tonalElevation: Dp = ExtinguishButtonDefault.outlinedButton.tonalElevation,
    border: BorderStroke? = ExtinguishButtonDefault.outlinedIconButton.border,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = Surface(
    onClick = onClick,
    modifier = modifier.semantics { role = Role.Button },
    enabled = enabled,
    shape = shape,
    color = containerColor,
    contentColor = contentColor,
    shadowElevation = shadowElevation,
    tonalElevation = tonalElevation,
    border = border,
    interactionSource = interactionSource
) {
    Box(
        modifier = Modifier.sizeIn(
            minWidth = ExtinguishButtonToken.minSize,
            minHeight = ExtinguishButtonToken.minSize
        ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ExtinguishButtonToken.spacing),
            content = content
        )
    }
}

@Preview
@Composable
private fun ExtinguishButtonPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExtinguishOutlinedButton(
                onClick = {},
            ) {
                Icon(painterResource(R.drawable.swap_horiz_20px), null)
                Text("切换")
            }
        }
    }
}