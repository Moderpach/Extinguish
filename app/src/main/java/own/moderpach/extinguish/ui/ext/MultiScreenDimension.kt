package own.moderpach.extinguish.ui.ext

import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


fun Modifier.widthLimiter(): Modifier = this.widthIn(256.dp, 640.dp)