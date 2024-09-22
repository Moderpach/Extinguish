package own.moderpach.extinguish.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.RadioCard
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.Solution: ExtinguishNavRoute get() = "Solution"

fun NavGraphBuilder.solution(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.Solution,
) {
    SolutionScreen(
        onBack,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionScreen(
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
                titleString = stringResource(R.string.Solutions),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            contentPadding = innerPadding.add(horizontal = 12.dp).add(bottom = 16.dp),
        ) {
            item {
                RadioCard(
                    modifier = Modifier.selectableGroup(),
                    selected = settingsRepository.solution == SettingsTokens.SolutionValue.ShizukuPowerOffScreen,
                    headline = stringResource(R.string.str_ShizukuPowerOffScreen),
                    supporting = stringResource(R.string.str_ShizukuPowerOffScreen_supporting),
                    onClick = {
                        settingsRepository.solution =
                            SettingsTokens.SolutionValue.ShizukuPowerOffScreen
                    }
                )
            }
            item {
                RadioCard(
                    modifier = Modifier.selectableGroup(),
                    selected = settingsRepository.solution == SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1,
                    headline = stringResource(R.string.str_ShizukuScreenBrightnessNeg1),
                    supporting = stringResource(R.string.str_ShizukuScreenBrightnessNeg1_supporting),
                    onClick = {
                        settingsRepository.solution =
                            SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1
                    }
                )
            }
        }

    }
}

@Preview
@Composable
private fun SolutionScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        SolutionScreen(
            onBack = {},
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}
