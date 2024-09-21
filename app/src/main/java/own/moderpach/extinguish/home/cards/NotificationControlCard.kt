package own.moderpach.extinguish.home.cards

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.home.HomeScreenCardKey
import own.moderpach.extinguish.home.HomeScreenCardKeys
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishCard
import own.moderpach.extinguish.ui.components.ExtinguishListItem
import own.moderpach.extinguish.ui.components.ExtinguishOutlinedIconButton
import own.moderpach.extinguish.ui.components.Placeholder

val HomeScreenCardKeys.notificationControl: HomeScreenCardKey get() = "NotificationControl"

fun LazyStaggeredGridScope.notificationControlCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = item(
    key = HomeScreenCardKeys.notificationControl
) {
    NotificationControlCard(
        extinguishServiceState,
        settingsRepository,
        onNavigateTo
    )
}

@Composable
fun NotificationControlCard(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    val context = LocalContext.current
    ExtinguishCard {
        Example()
        ExtinguishListItem(
            headlineContent = {
                Text(stringResource(R.string.Notification_control))
            },
            trailingContent = {
                ExtinguishOutlinedIconButton(
                    onClick = {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            context.startActivity(this)
                        }
                    },
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .scale(0.75f)
                .width(300.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.shapes.extraLarge
                )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(2.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.extinguish_20px),
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Placeholder(width = 96.dp, height = 14.dp)
            }
            Column(
                Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Placeholder(width = 165.dp, height = 16.dp)
                Placeholder(width = 96.dp, height = 12.dp)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, bottom = 12.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Placeholder(
                    width = 64.dp,
                    height = 16.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Placeholder(
                    width = 96.dp,
                    height = 16.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Placeholder(
                    width = 48.dp,
                    height = 16.dp,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}


@Preview
@Composable
private fun NotificationExamplePreview() {
    Box(
        Modifier
            .size(300.dp, 300.dp), Alignment.Center
    ) {
        Example()
    }
}