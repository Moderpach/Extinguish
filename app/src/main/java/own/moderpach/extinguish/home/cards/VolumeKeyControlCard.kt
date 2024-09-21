package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.home.TrailingContentWithSettingsAndSwitch
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.VolumeKeyControl
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem

val HomeScreenCardKeys.volumeKeyControl: HomeScreenCardKey get() = "VolumeKeyControl"

fun LazyStaggeredGridScope.volumeKeyControlCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.volumeKeyControl
) {
    VolumeKeyControlCard(
        extinguishServiceState,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun VolumeKeyControlCard(
    extinguishServiceState: ExtinguishService.State,
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
