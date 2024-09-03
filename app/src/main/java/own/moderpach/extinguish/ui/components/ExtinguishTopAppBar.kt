package own.moderpach.extinguish.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.union
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import own.moderpach.extinguish.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtinguishTopAppBar(
    titleString: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) = TopAppBar(
    title = {
        Text(titleString)
    },
    modifier,
    scrollBehavior = scrollBehavior,
    windowInsets = TopAppBarDefaults.windowInsets.union(WindowInsets.displayCutout)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtinguishTopAppBarWithNavigationBack(
    titleString: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) = TopAppBar(
    title = {
        Text(titleString)
    },
    modifier,
    navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(
                painterResource(R.drawable.arrow_back_24px),
                stringResource(R.string.navigate_back)
            )
        }
    },
    scrollBehavior = scrollBehavior,
    windowInsets = TopAppBarDefaults.windowInsets.union(WindowInsets.displayCutout)
)