package own.moderpach.extinguish.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.ISystemPermissionsManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.components.SettingCard
import own.moderpach.extinguish.settings.components.SettingLazyColumn
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.test.FakeSolutionStateManager
import own.moderpach.extinguish.test.FakeSystemPermissionsManager
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBarWithNavigationBack
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.ExternalControl: ExtinguishNavRoute get() = "ExternalControl"

fun NavGraphBuilder.externalControl(
    onBack: () -> Unit,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.ExternalControl,
) {
    ExternalControlScreen(
        onBack,
        solutionsStateManager,
        systemPermissionsManager,
        settingsRepository,
        onNavigateTo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalControlScreen(
    onBack: () -> Unit,
    solutionsStateManager: ISolutionsStateManager,
    systemPermissionsManager: ISystemPermissionsManager,
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var notes: List<String> by remember {
        mutableStateOf(emptyList())
    }

    LaunchedEffect(Unit) {
        val mNotes = mutableListOf<String>()
        context.resources.openRawResource(R.raw.external_control_note).bufferedReader()
            .forEachLine {
                mNotes.add(it)
            }
        notes = mNotes
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            ExtinguishTopAppBarWithNavigationBack(
                onBack = onBack,
                titleString = stringResource(R.string.External_control),
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        ProvideTextStyle(
            MaterialTheme.typography.bodyMedium
        ) {
            val externalControlNote = context.resources.openRawResource(R.raw.external_control_note)
            externalControlNote.bufferedReader()

            SettingLazyColumn(
                contentPadding = innerPadding.add(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                notes.forEach {
                    item {
                        if (it.isEmpty()) {
                            Spacer(Modifier.height(16.dp))
                        } else if (it.first() == '`') {
                            val code = it.slice(1..<it.length)
                            CodeBlock(
                                text = code
                            ) {
                                val clip: ClipData = ClipData.newPlainText(code, code)
                                clipboardManager.setPrimaryClip(clip)
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.Copied), Toast.LENGTH_SHORT
                                    ).show()

                            }
                        } else {
                            Text(it)
                        }
                    }
                }
            }
        }
    }


}

@Composable
private fun CodeBlock(
    modifier: Modifier = Modifier,
    text: String,
    onCopy: () -> Unit
) = SettingCard(
    modifier.padding(vertical = 8.dp)
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProvideTextStyle(
            MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        ) {
            LazyRow(
                Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                item {
                    Text(
                        text,
                        overflow = TextOverflow.Visible,
                        maxLines = 1,
                    )
                }
            }

        }
        IconButton(
            onClick = onCopy
        ) {
            Icon(painterResource(R.drawable.content_copy_24px), stringResource(R.string.Copy))
        }
    }
}


@Preview
@Composable
private fun ExternalControlScreenPreview() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        ExternalControlScreen(
            onBack = {},
            solutionsStateManager = FakeSolutionStateManager(),
            systemPermissionsManager = FakeSystemPermissionsManager(),
            settingsRepository = FakeSettingsRepository()
        ) { }
    }
}