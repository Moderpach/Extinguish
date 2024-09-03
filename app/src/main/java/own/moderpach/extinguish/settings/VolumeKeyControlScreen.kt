package own.moderpach.extinguish.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.EnablerCard
import own.moderpach.extinguish.settings.components.RadioCard
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.components.SettingListItem
import own.moderpach.extinguish.settings.components.SettingListItemWithSwitch
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.test.FakeSolutionStateManager
import own.moderpach.extinguish.test.FakeSystemPermissionsManager
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.VolumeKeyControl: ExtinguishNavRoute get() = "VolumeKeyControl"

fun NavGraphBuilder.volumeKeyControl(
    onBack: () -> Unit,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.VolumeKeyControl,
) {
    VolumeKeyControlScreen(
        onBack,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeKeyControlScreen(
    onBack: () -> Unit,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            ExtinguishTopAppBarWithNavigationBack(
                onBack = onBack,
                titleString = stringResource(R.string.Volume_key_control),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SettingLazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                EnablerCard(
                    text = stringResource(R.string.Enabled),
                    checked = settingsRepository.volumeKeyEvent.enabled,
                    onCheckedChange = {
                        settingsRepository.volumeKeyEvent.enabled = it
                    }
                )
            }
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_clickToTurnScreenOn),
                        checked = settingsRepository.volumeKeyEvent.clickToTurnScreenOn,
                        onCheckedChange = {
                            settingsRepository.volumeKeyEvent.clickToTurnScreenOn = it
                        }
                    )
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_clickToTurnScreenOff),
                        checked = settingsRepository.volumeKeyEvent.clickToTurnScreenOff,
                        onCheckedChange = {
                            settingsRepository.volumeKeyEvent.clickToTurnScreenOff = it
                        }
                    )
                }
            }
            item {
                SettingCard(
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    SettingListItem(
                        headline = stringResource(R.string.str_volumeKeyListeningMethod)
                    )
                    Column(
                        Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioCard(
                            modifier = Modifier.selectableGroup(),
                            selected = settingsRepository.volumeKeyEvent.listeningMethod == SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Shell,
                            onClick = {
                                settingsRepository.volumeKeyEvent.listeningMethod =
                                    SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Shell
                            },
                            headline = stringResource(R.string.str_shell),
                            supporting = stringResource(R.string.str_shell_supporting)
                        )
                        RadioCard(
                            modifier = Modifier.selectableGroup(),
                            selected = settingsRepository.volumeKeyEvent.listeningMethod == SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Window,
                            onClick = {
                                settingsRepository.volumeKeyEvent.listeningMethod =
                                    SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Window
                            },
                            headline = stringResource(R.string.str_android_window),
                            supporting = stringResource(R.string.str_android_window_supporting)
                        )
                    }
                }
            }
        }

    }
}

@Preview
@Composable
private fun VolumeKeyControlScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        VolumeKeyControlScreen(
            onBack = {},
            solutionsStateManager = FakeSolutionStateManager(),
            systemPermissionsManager = FakeSystemPermissionsManager(),
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}
