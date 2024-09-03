package own.moderpach.extinguish.settings.data

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("settings")

fun stringSettingElement(name: String, default: String) =
    SettingElement(stringPreferencesKey(name), default)

fun intSettingElement(name: String, default: Int) =
    SettingElement(intPreferencesKey(name), default)

fun floatSettingElement(name: String, default: Float) =
    SettingElement(floatPreferencesKey(name), default)

fun booleanSettingElement(name: String, default: Boolean) =
    SettingElement(booleanPreferencesKey(name), default)

data class SettingElement<T>(
    val key: Preferences.Key<T>,
    val default: T
)
