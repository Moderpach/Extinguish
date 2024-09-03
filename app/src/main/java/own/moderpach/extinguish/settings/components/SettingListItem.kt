package own.moderpach.extinguish.settings.components

import android.widget.Switch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import kotlin.math.roundToInt

@Composable
fun SettingListItemWithSwitch(
    modifier: Modifier = Modifier,
    headline: String,
    supporting: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) = SettingListItem(
    modifier, headline, supporting,
    trailingContent = {
        Switch(checked, onCheckedChange)
    },
    onClick = {
        onCheckedChange(!checked)
    }
)

@Composable
fun SettingListItemWithSlider(
    modifier: Modifier = Modifier,
    overline: String,
    leadingContent: @Composable() (RowScope.() -> Unit)? = null,
    supporting: String? = null,
    value: Float,
    steps: Int = 0,
    onValueChangeFinished: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    var tempValue by remember {
        mutableFloatStateOf(value)
    }
    ExtinguishListItem(
        modifier = modifier,
        overlineContent = {
            ProvideTextStyle(
                MaterialTheme.typography.titleMedium
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(overline)
                    Text("${(tempValue * 100).roundToInt()}%")
                }
            }
        },
        headlineContent = {
            Slider(
                value = tempValue,
                valueRange = valueRange,
                steps = steps,
                onValueChangeFinished = {
                    onValueChangeFinished(tempValue)
                },
                onValueChange = {
                    tempValue = it
                }
            )
        },
        supportingContent = supporting?.let {
            { Text(it) }
        },
        leadingContent = leadingContent,
        morePaddingForPureText = true
    )
}

@Composable
fun SettingListItem(
    modifier: Modifier = Modifier,
    headline: String,
    supporting: String? = null,
    leadingContent: @Composable() (RowScope.() -> Unit)? = null,
    trailingContent: @Composable() (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) = ExtinguishListItem(
    modifier = modifier,
    headlineContent = {
        Text(headline)
    },
    supportingContent = supporting?.let {
        { Text(it) }
    },
    leadingContent = leadingContent,
    trailingContent = trailingContent,
    onClick = onClick,
    morePaddingForPureText = true
)