package own.moderpach.extinguish.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.EnablerCard
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.components.SettingListItemWithSlider
import own.moderpach.extinguish.settings.components.SettingListItemWithSwitch
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.test.FakeSolutionStateManager
import own.moderpach.extinguish.test.FakeSystemPermissionsManager
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.FloatingButton: ExtinguishNavRoute get() = "FloatingButton"

fun NavGraphBuilder.floatingButton(
    onBack: () -> Unit,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.FloatingButton,
) {
    FloatingButtonScreen(
        onBack,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingButtonScreen(
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
                titleString = stringResource(R.string.Floating_button),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SettingLazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                EnablerCard(
                    text = stringResource(R.string.Enabled),
                    checked = settingsRepository.floatingButton.enabled,
                    onCheckedChange = {
                        settingsRepository.floatingButton.enabled = it
                    }
                )
            }
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_autoMoveToEdge),
                        checked = settingsRepository.floatingButton.autoMoveToEdge,
                        onCheckedChange = {
                            settingsRepository.floatingButton.autoMoveToEdge = it
                        }
                    )
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_fadeWhenUnused),
                        checked = settingsRepository.floatingButton.fadeWhenUnused,
                        onCheckedChange = {
                            settingsRepository.floatingButton.fadeWhenUnused = it
                        }
                    )
                    SettingListItemWithSlider(
                        overline = stringResource(R.string.str_fadeTransparency),
                        value = settingsRepository.floatingButton.fadeTransparency,
                        onValueChangeFinished = {
                            settingsRepository.floatingButton.fadeTransparency = it
                        },
                        valueRange = 0.2f..1f,
                        steps = 15
                    )
                }
            }
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_blackStyle),
                        checked = settingsRepository.floatingButton.blackStyle,
                        onCheckedChange = {
                            settingsRepository.floatingButton.blackStyle = it
                        }
                    )
                }
            }
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_showTimerButton),
                        checked = settingsRepository.floatingButton.showTimerButton,
                        onCheckedChange = {
                            settingsRepository.floatingButton.showTimerButton = it
                        }
                    )
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_mergeTimerButton),
                        supporting = stringResource(R.string.str_mergeTimerButton_supporting),
                        checked = settingsRepository.floatingButton.mergeTimerButton,
                        onCheckedChange = {
                            settingsRepository.floatingButton.mergeTimerButton = it
                        }
                    )
                }
            }
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_hideWhenScreenOff),
                        checked = settingsRepository.floatingButton.hideWhenScreenOff,
                        onCheckedChange = {
                            settingsRepository.floatingButton.hideWhenScreenOff = it
                        }
                    )
                }
            }
        }

    }
}

@Preview
@Composable
private fun FloatingButtonScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        FloatingButtonScreen(
            onBack = {},
            solutionsStateManager = FakeSolutionStateManager(),
            systemPermissionsManager = FakeSystemPermissionsManager(),
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}
