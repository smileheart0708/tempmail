package com.temp.mail.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.ui.theme.TempMailTheme
import org.koin.androidx.compose.koinViewModel

class SettingsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
       enableEdgeToEdge()
       super.onCreate(savedInstanceState)
       setContent {
           val viewModel: SettingsViewModel = koinViewModel()
           val theme by viewModel.theme.collectAsState()
           TempMailTheme(
               darkTheme = when (theme) {
                   "Light" -> false
                   "Dark" -> true
                   else -> resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
               }
           ) {
               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   Scaffold(
                       topBar = {
                           TopAppBar(
                               title = { Text(text = stringResource(id = R.string.action_settings)) },
                               navigationIcon = {
                                   IconButton(onClick = { finish() }) {
                                       Icon(
                                           imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                           contentDescription = "Back"
                                       )
                                   }
                               }
                           )
                       }
                   ) { paddingValues ->
                       Column(
                           modifier = Modifier
                               .fillMaxSize()
                               .padding(paddingValues)
                       ) {
                           SettingsScreen(viewModel)
                       }
                   }
               }
           }
       }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val theme by viewModel.theme.collectAsState()
    val themes = listOf("System", "Light", "Dark")
    val themeLabels = mapOf(
        "System" to stringResource(id = R.string.system_default),
        "Light" to stringResource(id = R.string.light_mode),
        "Dark" to stringResource(id = R.string.dark_mode)
    )
    Column {
        Text(
            text = stringResource(id = R.string.personalization),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.theme))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                themes.forEachIndexed { index, item ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = themes.size),
                        onCheckedChange = {
                            viewModel.setTheme(item)
                        },
                        checked = theme == item
                    ) {
                        Text(themeLabels[item] ?: item)
                    }
                }
            }
        }
    }
}