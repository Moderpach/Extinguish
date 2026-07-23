package own.moderpach.extinguish.settings

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.LocalSystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.SpecificPermission
import own.moderpach.extinguish.settings.components.EnablerCard
import own.moderpach.extinguish.settings.components.RadioCard
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.components.SettingListItem
import own.moderpach.extinguish.settings.components.SettingListItemWithSwitch
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.test.FakeSystemPermissionsManager
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.VolumeKeyControl: ExtinguishNavRoute get() = "VolumeKeyControl"

fun NavGraphBuilder.volumeKeyControl(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.VolumeKeyControl,
) {
    VolumeKeyControlScreen(
        onBack,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeKeyControlScreen(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val systemPermissionsManager = LocalSystemPermissionsManager.current
    var isAccessibilityServiceEnabled by remember {
        mutableStateOf(systemPermissionsManager.checkSpecial(SpecificPermission.AccessibilityServiceEnabled))
    }
    // these can only change in system settings, so re-check when coming back
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityServiceEnabled =
                    systemPermissionsManager.checkSpecial(SpecificPermission.AccessibilityServiceEnabled)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                        // FLAG_REQUEST_FILTER_KEY_EVENTS requires API 33
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            RadioCard(
                                modifier = Modifier.selectableGroup(),
                                selected = settingsRepository.volumeKeyEvent.listeningMethod == SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Accessibility,
                                onClick = {
                                    settingsRepository.volumeKeyEvent.listeningMethod =
                                        SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Accessibility
                                },
                                headline = stringResource(R.string.str_accessibility),
                                supporting = stringResource(R.string.str_accessibility_supporting)
                            )
                            if (
                                settingsRepository.volumeKeyEvent.listeningMethod == SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Accessibility &&
                                !isAccessibilityServiceEnabled
                            ) {
                                SettingCard {
                                    SettingListItem(
                                        headline = stringResource(R.string.str_accessibilityServiceNotEnabled),
                                        supporting = stringResource(R.string.str_accessibilityServiceNotEnabled_supporting)
                                    )
                                    Button(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = 16.dp),
                                        onClick = {
                                            systemPermissionsManager.requestSpecial(SpecificPermission.AccessibilityServiceEnabled)
                                        }
                                    ) {
                                        Text(stringResource(R.string.Open_accessibility_settings))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Preview
@Composable
private fun VolumeKeyControlScreenPreview() = ExtinguishTheme {
    CompositionLocalProvider(
        LocalSystemPermissionsManager provides FakeSystemPermissionsManager()
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            VolumeKeyControlScreen(
                onBack = {},
                settingsRepository = FakeSettingsRepository()
            ) { }
        }
    }
}
