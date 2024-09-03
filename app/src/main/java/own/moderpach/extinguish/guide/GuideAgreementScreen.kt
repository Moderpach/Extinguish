package own.moderpach.extinguish.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.GuideAgreement: ExtinguishNavRoute get() = "GuideAgreement"

fun NavGraphBuilder.guideAgreement(
    settingsRepository: ISettingsRepository,
    onAgree: () -> Unit,
) = extinguishComposable(
    ExtinguishNavGraph.GuideAgreement,
) {
    GuideAgreementScreen(settingsRepository, onAgree)
}

@Composable
fun GuideAgreementScreen(
    settingsRepository: ISettingsRepository,
    onAgree: () -> Unit,
) {
    val context = LocalContext.current

    var userAgreement by remember { mutableStateOf("") }
    var privacyPolicy by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                        stringResource(R.string.User_agreement_and_Privacy_policy),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        stringResource(R.string.str_User_agreement_and_Privacy_policy_supporting),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        floatingActionButton = {
            Button(onClick = onAgree) {
                Text(stringResource(R.string.Agree))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding.add(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                Spacer(Modifier.height(128.dp))
            }
        }
    }
}

@Preview
@Composable
private fun GuideAgreementScreenPreview() = MaterialTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        GuideAgreementScreen(
            FakeSettingsRepository(),
            onAgree = {}
        )
    }
}