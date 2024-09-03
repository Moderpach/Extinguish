package own.moderpach.extinguish.guide

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.test.FakeSolutionStateManager
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.GuideShizukuRunning: ExtinguishNavRoute get() = "GuideShizukuRunning"

fun NavGraphBuilder.guideShizukuRunning(
    solutionsStateManager: ISolutionsStateManager,
    settingsRepository: ISettingsRepository,
    reselectSolution: () -> Unit,
    onNext: () -> Unit,
) = extinguishComposable(
    ExtinguishNavGraph.GuideShizukuRunning,
) {
    GuideShizukuRunningScreen(solutionsStateManager, settingsRepository, reselectSolution, onNext)
}

@Composable
fun GuideShizukuRunningScreen(
    solutionsStateManager: ISolutionsStateManager,
    settingsRepository: ISettingsRepository,
    reselectSolution: () -> Unit,
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
                        stringResource(R.string.Shizuku_is_not_running),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        stringResource(R.string.str_Shizuku_is_not_running_supporting),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        floatingActionButton = {
            Button(
                onClick = onNext,
                enabled = solutionsStateManager.state.collectAsState().value.isShizukuRunning
            ) {
                Text(stringResource(R.string.Complete))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.add(horizontal = 24.dp).add(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    stringResource(R.string.str_You_can),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                Button(onClick = reselectSolution) {
                    Text(stringResource(R.string.Reselect_solution))
                }
            }
            item {
                Button(onClick = {
                    Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse("https://shizuku.rikka.app/introduction/"))
                        context.startActivity(this)
                    }
                }) {
                    Text(stringResource(R.string.Learn_more_about_Shizuku))
                    Icon(painterResource(R.drawable.open_in_new_20px), null)
                }
            }
            item {
                Button(onClick = {
                    Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse("https://shizuku.rikka.app/download/"))
                        context.startActivity(this)
                    }
                }) {
                    Text(stringResource(R.string.Download_Shizuku))
                    Icon(painterResource(R.drawable.open_in_new_20px), null)
                }
            }
            item {
                Button(onClick = {
                    Intent(Intent.ACTION_VIEW).apply {
                        setData(Uri.parse("https://shizuku.rikka.app/guide/setup/"))
                        context.startActivity(this)
                    }
                }) {
                    Text(stringResource(R.string.User_manual_of_Shizuku))
                    Icon(painterResource(R.drawable.open_in_new_20px), null)
                }
            }
            item {
                Button(onClick = {
                    Intent().apply {
                        setClassName(
                            "moe.shizuku.privileged.api",
                            "moe.shizuku.manager.MainActivity"
                        )
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(this)
                    }
                }) {
                    Text(stringResource(R.string.Open_Shizuku))
                    Icon(painterResource(R.drawable.open_in_new_20px), null)
                }
            }
            item {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}

@Preview
@Composable
private fun GuideShizukuRunningScreenPreview() = MaterialTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        GuideShizukuRunningScreen(
            FakeSolutionStateManager(),
            FakeSettingsRepository(),
            reselectSolution = {},
            onNext = {}
        )
    }
}