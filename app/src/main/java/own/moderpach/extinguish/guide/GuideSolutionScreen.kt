package own.moderpach.extinguish.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.GuideSolution: ExtinguishNavRoute get() = "GuideSolution"

fun NavGraphBuilder.guideSolution(
    settingsRepository: ISettingsRepository,
    onNext: () -> Unit,
) = extinguishComposable(
    ExtinguishNavGraph.GuideSolution,
) {
    GuideSolutionScreen(settingsRepository, onNext)
}

@Composable
fun GuideSolutionScreen(
    settingsRepository: ISettingsRepository,
    onNext: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            Box(
                Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    Modifier
                        .windowInsetsPadding(
                            WindowInsets.systemBars
                                .union(WindowInsets.displayCutout)
                                .only(WindowInsetsSides.Top + WindowInsetsSides.Vertical)
                        )
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.Select_a_solution),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        stringResource(R.string.str_Select_a_solution_supporting1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.str_Select_a_solution_supporting2),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        floatingActionButton = {
            Button(onClick = onNext) {
                Text(stringResource(R.string.Next))
            }
        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            contentPadding = innerPadding.add(horizontal = 12.dp).add(bottom = 64.dp),
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
private fun GuideSolutionScreenPreview() = MaterialTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        GuideSolutionScreen(
            FakeSettingsRepository(),
            onNext = {}
        )
    }
}