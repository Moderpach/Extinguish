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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.components.Placeholder

val HomeScreenCardKeys.tileControl: HomeScreenCardKey get() = "TileControl"

fun LazyStaggeredGridScope.tileControlCard(
    extinguishServiceState: ExtinguishService.State,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.tileControl
) {
    TileControlCard(
        extinguishServiceState,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun TileControlCard(
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
                Text(stringResource(R.string.Quick_settings_tiles_control))
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    painterResource(R.drawable.linked_services_24px),
                    null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Placeholder(
                width = 32.dp,
                height = 12.dp,
            )
        }
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            ) {
                Icon(
                    painterResource(R.drawable.extinguish_24px),
                    null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Placeholder(
                width = 32.dp,
                height = 12.dp,
            )
        }
    }
}

@Preview
@Composable
private fun TileExamplePreview() {
    Box(
        Modifier
            .size(350.dp, 300.dp), Alignment.Center
    ) {
        Example()
    }
}