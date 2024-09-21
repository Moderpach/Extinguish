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
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.components.SettingListItemWithSwitch
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.Compatible: ExtinguishNavRoute get() = "Compatible"

fun NavGraphBuilder.compatible(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.Compatible,
) {
    CompatibleScreen(
        onBack,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibleScreen(
    onBack: () -> Unit,
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
                titleString = stringResource(R.string.Compatibility),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SettingLazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                SettingCard {
                    SettingListItemWithSwitch(
                        headline = stringResource(R.string.str_brightnessManualWhenScreenOff),
                        supporting = stringResource(R.string.str_brightnessManualWhenScreenOff_supporting),
                        checked = settingsRepository.compatibility.brightnessManualWhenScreenOff,
                        onCheckedChange = {
                            settingsRepository.compatibility.brightnessManualWhenScreenOff = it
                        }
                    )
                }
            }

        }
    }
}

@Preview
@Composable
private fun CompatibleScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        CompatibleScreen(
            onBack = {},
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}