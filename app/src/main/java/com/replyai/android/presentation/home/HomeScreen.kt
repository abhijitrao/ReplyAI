package com.replyai.android.presentation.home

import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.replyai.android.domain.model.GeneratedReply
import com.replyai.android.domain.model.ResponseLength
import com.replyai.android.domain.model.Tone
import com.replyai.android.presentation.common.ShareText

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onInputChanged = viewModel::updateInput,
        onToneChanged = viewModel::updateTone,
        onLengthChanged = viewModel::updateResponseLength,
        onGenerate = viewModel::generate,
        onSettingsClick = onSettingsClick
    )
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onInputChanged: (String) -> Unit,
    onToneChanged: (Tone) -> Unit,
    onLengthChanged: (ResponseLength) -> Unit,
    onGenerate: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ReplyAI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Say it naturally. Reply professionally.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(onClick = onSettingsClick) {
                Text("Settings")
            }
        }

        Text(
            text = "What do you want to say?",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = state.inputText,
            onValueChange = onInputChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Type in Hinglish or your natural language...") },
            supportingText = {
                Text("Your meaning will be preserved while improving the wording.")
            }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            SelectionMenu(
                label = "Tone",
                selected = state.tone.label(),
                modifier = Modifier.weight(1f),
                options = Tone.entries,
                optionLabel = { it.label() },
                onSelected = onToneChanged
            )

            Spacer(modifier = Modifier.width(12.dp))

            SelectionMenu(
                label = "Length",
                selected = state.responseLength.label(),
                modifier = Modifier.weight(1f),
                options = ResponseLength.entries,
                optionLabel = { it.label() },
                onSelected = onLengthChanged
            )
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isGenerating
        ) {
            if (state.isGenerating) {
                CircularProgressIndicator(strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Generate Reply")
        }

        state.generatedReply?.let { reply ->
            ReplyCard(
                generatedReply = reply,
                onCopy = { copyToClipboard(context, reply.reply) },
                onShare = { ShareText.share(context, reply.reply) }
            )
        }
    }
}

@Composable
private fun <T> SelectionMenu(
    label: String,
    selected: String,
    modifier: Modifier,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected, maxLines = 1)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun ReplyCard(
    generatedReply: GeneratedReply,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "AI Reply",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = generatedReply.reply,
            style = MaterialTheme.typography.bodyLarge
        )

        if (generatedReply.translation.isNotBlank()) {
            Text(
                text = generatedReply.translation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            TextButton(onClick = onCopy) {
                Text("Copy")
            }
            TextButton(onClick = onShare) {
                Text("Share")
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("ReplyAI reply", text))
}

private fun Tone.label(): String = when (this) {
    Tone.PROFESSIONAL -> "Professional"
    Tone.POLITE -> "Polite"
    Tone.FRIENDLY -> "Friendly"
    Tone.FORMAL -> "Formal"
    Tone.CASUAL -> "Casual"
    Tone.FIRM -> "Firm"
    Tone.URGENT -> "Urgent"
}

private fun ResponseLength.label(): String = when (this) {
    ResponseLength.SHORT -> "Short"
    ResponseLength.MEDIUM -> "Medium"
    ResponseLength.DETAILED -> "Detailed"
}
