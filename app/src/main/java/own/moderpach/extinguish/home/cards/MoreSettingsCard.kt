package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.About
import own.moderpach.extinguish.settings.Compatible
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem

val HomeScreenCardKeys.moreSettings: HomeScreenCardKey get() = "MoreSettings"

fun LazyStaggeredGridScope.moreSettingsCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.moreSettings
) {
    MoreSettingsCard(
        extinguishServiceState,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun MoreSettingsCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    ExtinguishCard(
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Text(
            stringResource(R.string.More_settings),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Compatibility_options))
            },
            onClick = { onNavigateTo(ExtinguishNavGraph.Compatible) },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.About))
            },
            onClick = { onNavigateTo(ExtinguishNavGraph.About) },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
    }
}