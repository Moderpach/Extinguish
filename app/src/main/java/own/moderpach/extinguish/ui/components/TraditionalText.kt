package own.moderpach.extinguish.ui.components

import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TraditionalText(
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    text: String,
    fontSize: TextUnit = 16.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    lineSpacingAdd: Float = 0f,
    lineSpacingMulti: Float = 1f
) {
    AndroidView(
        modifier = modifier, // Occupy the max size in the Compose UI tree
        factory = { context ->
            TextView(context).apply {
                setTextIsSelectable(selectable)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.value)
                setTextColor(color.toArgb())
                setLineSpacing(lineSpacingAdd, lineSpacingMulti)
                setText(text)
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
            view.apply {
                setTextIsSelectable(selectable)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.value)
                setTextColor(color.toArgb())
                setText(text)
            }
        }
    )

}