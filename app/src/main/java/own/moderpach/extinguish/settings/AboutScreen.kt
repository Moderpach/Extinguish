package own.moderpach.extinguish.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.BuildConfig
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.components.SettingListItem
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

val ExtinguishNavGraph.About: ExtinguishNavRoute get() = "About"

fun NavGraphBuilder.about(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.About,
) {
    AboutScreen(
        onBack,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var licenses by remember { mutableStateOf("") }
    var solutionsSources by remember { mutableStateOf("") }
    var userAgreement by remember { mutableStateOf("") }
    var privacyPolicy by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        licenses = buildString {
            context.resources.openRawResource(R.raw.licenses).bufferedReader().forEachLine {
                append(it)
                append("\n")
            }
        }
        solutionsSources = buildString {
            context.resources.openRawResource(R.raw.solutions_sources).bufferedReader()
                .forEachLine {
                    append((it))
                    append("\n")
                }
        }
        userAgreement = buildString {
            context.resources.openRawResource(R.raw.user_agreement).bufferedReader().forEachLine {
                append((it))
                append("\n")
            }
        }
        privacyPolicy = buildString {
            context.resources.openRawResource(R.raw.privacy_policy).bufferedReader().forEachLine {
                append((it))
                append("\n")
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            ExtinguishTopAppBarWithNavigationBack(
                onBack = onBack,
                titleString = stringResource(R.string.About),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        SettingLazyColumn(
            contentPadding = innerPadding,
        ) {
            item {
                SettingCard {
                    SettingListItem(
                        headline = stringResource(R.string.Version),
                        supporting = BuildConfig.VERSION_NAME
                    )
                    SettingListItem(
                        headline = stringResource(R.string.Developer),
                        supporting = stringResource(R.string.moderpach)
                    )
                    SettingListItem(
                        headline = stringResource(R.string.Repository),
                        supporting = "https://github.com/Moderpach/Extinguish",
                        onClick = {
                            Intent(Intent.ACTION_VIEW).apply {
                                setData(Uri.parse("https://github.com/Moderpach/Extinguish"))
                                context.startActivity(this)
                            }
                        }
                    )
                }
            }
            item {
                SettingCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.User_agreement),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        userAgreement,
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }
            item {
                SettingCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.Privacy_policy),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        privacyPolicy,
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }
            item {
                SettingCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.Licenses),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        licenses,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        stringResource(R.string.Solutions_sources),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        solutionsSources,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AboutScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        AboutScreen(
            onBack = {},
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}