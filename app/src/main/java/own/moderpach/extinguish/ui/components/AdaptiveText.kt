package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

@Composable
fun AdaptiveText(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    fontWeight: FontWeight? = style.fontWeight,
    fontFamily: FontFamily? = style.fontFamily,
    color: Color = style.color,
    modifier: Modifier = Modifier
) {
    var resizedTextSize by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }
    Text(
        text = text,
        style = style,
        softWrap = false,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        color = color,
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
            }
        },
        onTextLayout = { result ->
            if (result.didOverflowHeight) {
                resizedTextSize = resizedTextSize.copy(
                    fontSize = resizedTextSize.fontSize * 0.95
                )
            } else {
                shouldDraw = true
            }
        }
    )
}