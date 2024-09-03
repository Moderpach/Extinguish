package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.R

@Composable
fun IconWithBackground(
    painter: Painter,
    contentDescription: String,
    innerPadding: Dp,
    size: Dp,
    background: Color,
    tint: Color,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(innerPadding)
            .size(size),
        tint = tint
    )
}