package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem

val HomeScreenCardKeys.screenEventControl: HomeScreenCardKey get() = "ScreenEventControl"

fun LazyStaggeredGridScope.screenEventControlCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.screenEventControl
) {
    ScreenEventControlCard(
        extinguishServiceState,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun ScreenEventControlCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    ExtinguishCard {
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Touch_screen_to_turn_it_on))
            },
            supportingContent = {
                Text(stringResource(R.string.str_Touch_screen_to_turn_it_on_supporting))
            },
            trailingContent = {
                Switch(
                    checked = settingsRepository.screenEvent.enabled,
                    onCheckedChange = { settingsRepository.screenEvent.enabled = it }
                )
            },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
    }
}
