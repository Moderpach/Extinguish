package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
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
import own.moderpach.extinguish.home.TrailingContentWithSettingsAndHelp
import own.moderpach.extinguish.settings.ExternalControl
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedIconButton
import own.moderpach.extinguish.ui.components.Placeholder

val HomeScreenCardKeys.externalControl: HomeScreenCardKey get() = "ExternalControl"

fun LazyStaggeredGridScope.externalControlCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.externalControl
) {
    ExternalControlCard(
        extinguishServiceState,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun ExternalControlCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    ExtinguishCard {
        Example()
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.External_control))
            },
            trailingContent = {
                ExtinguishOutlinedIconButton(
                    onClick = { onNavigateTo(ExtinguishNavGraph.ExternalControl) },
                    icon = painterResource(R.drawable.settings_20px),
                    contentDescription = stringResource(R.string.Settings)
                )
            },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun Example() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
    ) {
        ProvideTextStyle(
            MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        ) {
            Text(
                ">adb shell",
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                ">am startservice -n own.moderpach.extinguish/.service.ExtinguishService",
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}