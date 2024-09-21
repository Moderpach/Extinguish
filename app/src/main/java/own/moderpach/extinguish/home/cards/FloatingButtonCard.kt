package own.moderpach.extinguish.home.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.home.TrailingContentWithSettingsAndSwitch
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.FloatingButton
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.timer.TimerPreset
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedButton
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val HomeScreenCardKeys.floatingButton: HomeScreenCardKey get() = "FloatingButton"

fun LazyStaggeredGridScope.floatingButtonCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.floatingButton
) {
    FloatingButtonCard(
        extinguishServiceState,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun FloatingButtonCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    ExtinguishCard {
        Example()
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Floating_button))
            },
            trailingContent = {
                TrailingContentWithSettingsAndSwitch(
                    onClickSettings = {
                        onNavigateTo(ExtinguishNavGraph.FloatingButton)
                    },
                    checked = settingsRepository.floatingButton.enabled,
                    onCheckedChange = { settingsRepository.floatingButton.enabled = it }
                )
            },
            morePaddingForPureText = false,
            headlineTextStyle = MaterialTheme.typography.titleMedium,
            overlineTextStyle = MaterialTheme.typography.labelMedium,
            supportingTextStyle = MaterialTheme.typography.bodySmall
        )
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Timers))
            },
            trailingContent = {
                ExtinguishOutlinedButton(
                    onClick = { onNavigateTo(ExtinguishNavGraph.TimerPreset) },
                    icon = painterResource(R.drawable.timer_20px),
                    text = stringResource(R.string.Preset_timers)
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
    val innerPadding = 6.dp
    val itemSpacing = 2.dp
    val floatingButtonSize = 36.dp
    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(MaterialTheme.colorScheme.background),
        Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .size(
                        width = floatingButtonSize + innerPadding * 2,
                        height = floatingButtonSize * 2 + innerPadding * 2 + itemSpacing
                    )
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            )
            Column(
                modifier = Modifier.zIndex(0.5f),
                verticalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                Row(
                    Modifier
                        .padding(start = innerPadding)
                        .height(floatingButtonSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painterResource(R.drawable.extinguish_20px),
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.Turn_screen_off),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Row(
                    Modifier
                        .padding(start = innerPadding)
                        .height(floatingButtonSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painterResource(R.drawable.timer_20px),
                        null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.Show_timer_popup),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun FloatingButtonExamplePreview() = ExtinguishTheme {
    Box(
        Modifier
            .size(300.dp, 300.dp), Alignment.Center
    ) {
        Example()
    }
}