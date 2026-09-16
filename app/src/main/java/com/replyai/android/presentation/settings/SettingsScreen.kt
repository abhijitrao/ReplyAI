package com.replyai.android.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.replyai.android.domain.model.AiMode
import com.replyai.android.domain.model.OnlineProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.aiSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AI",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "AI mode",
                style = MaterialTheme.typography.titleMedium
            )

            AiMode.values().forEach { mode ->
                RadioSettingRow(
                    label = mode.displayName(),
                    selected = settings.mode == mode,
                    onSelected = { viewModel.updateMode(mode) }
                )
            }

            HorizontalDivider()

            Text(
                text = "Online provider",
                style = MaterialTheme.typography.titleMedium
            )

            OnlineProvider.values().forEach { provider ->
                RadioSettingRow(
                    label = provider.displayName(),
                    selected = settings.onlineProvider == provider,
                    onSelected = { viewModel.updateOnlineProvider(provider) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Model configuration will be connected to the provider in the next integration step.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RadioSettingRow(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelected
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun AiMode.displayName(): String = when (this) {
    AiMode.AUTOMATIC -> "Automatic"
    AiMode.ONLINE -> "Online"
    AiMode.OFFLINE -> "Offline"
}

private fun OnlineProvider.displayName(): String = when (this) {
    OnlineProvider.OPENAI -> "OpenAI"
    OnlineProvider.GEMINI -> "Google Gemini"
    OnlineProvider.CUSTOM -> "Custom API"
}
