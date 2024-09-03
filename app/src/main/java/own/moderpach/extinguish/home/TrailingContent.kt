package own.moderpach.extinguish.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import own.moderpach.extinguish.R
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedIconButton

@Composable
fun RowScope.TrailingContentWithSettingsHelpAndSwitch(
    onClickSettings: () -> Unit,
    onClickHelp: () -> Unit,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    Row {
        ExtinguishOutlinedIconButton(
            onClick = onClickHelp,
            icon = painterResource(R.drawable.help_20px),
            contentDescription = stringResource(R.string.Help)
        )
        ExtinguishOutlinedIconButton(
            onClick = onClickSettings,
            icon = painterResource(R.drawable.settings_20px),
            contentDescription = stringResource(R.string.Settings)
        )
    }
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun RowScope.TrailingContentWithSettingsAndSwitch(
    onClickSettings: () -> Unit,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    ExtinguishOutlinedIconButton(
        onClick = onClickSettings,
        icon = painterResource(R.drawable.settings_20px),
        contentDescription = stringResource(R.string.Settings)
    )
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}

@Composable
fun RowScope.TrailingContentWithSettingsAndHelp(
    onClickSettings: () -> Unit,
    onClickHelp: () -> Unit,
) {
    Row {
        ExtinguishOutlinedIconButton(
            onClick = onClickHelp,
            icon = painterResource(R.drawable.help_20px),
            contentDescription = stringResource(R.string.Help)
        )
        ExtinguishOutlinedIconButton(
            onClick = onClickSettings,
            icon = painterResource(R.drawable.settings_20px),
            contentDescription = stringResource(R.string.Settings)
        )
    }
}