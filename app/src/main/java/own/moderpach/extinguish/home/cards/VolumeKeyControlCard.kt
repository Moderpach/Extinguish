package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.home.TrailingContentWithSettingsAndSwitch
import own.moderpach.extinguish.settings.VolumeKeyControl
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val HomeScreenCardKeys.volumeKeyControl: HomeScreenCardKey get() = "VolumeKeyControl"

fun LazyStaggeredGridScope.volumeKeyControlCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.volumeKeyControl
) {
    VolumeKeyControlCard(
        extinguishServiceState,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun VolumeKeyControlCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    ExtinguishCard {
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Volume_key_control))
            },
            supportingContent = {
                val supportingText = buildAnnotatedString {
                    if (settingsRepository.volumeKeyEvent.clickToTurnScreenOn) {
                        append(stringResource(R.string.Enabled))
                    } else append(stringResource(R.string.Disabled))
                    append(" ")
                    append(stringResource(R.string.str_clickToTurnScreenOn))
                    append("\n")
                    if (settingsRepository.volumeKeyEvent.clickToTurnScreenOff) {
                        append(stringResource(R.string.Enabled))
                    } else append(stringResource(R.string.Disabled))
                    append(" ")
                    append(stringResource(R.string.str_clickToTurnScreenOff))
                }
                Text(supportingText)
            },
            trailingContent = {
                TrailingContentWithSettingsAndSwitch(
                    onClickSettings = { onNavigateTo(ExtinguishNavGraph.VolumeKeyControl) },
                    checked = settingsRepository.volumeKeyEvent.enabled,
                    onCheckedChange = { settingsRepository.volumeKeyEvent.enabled = it }
                )
            },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
    }
}
