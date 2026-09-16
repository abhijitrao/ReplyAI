package com.replyai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.replyai.android.data.settings.SettingsRepository
import com.replyai.android.presentation.home.HomeScreen
import com.replyai.android.presentation.home.HomeViewModel
import com.replyai.android.presentation.settings.SettingsScreen
import com.replyai.android.presentation.settings.SettingsViewModel
import com.replyai.android.ui.theme.ReplyAiTheme

class MainActivity : ComponentActivity() {

    private val settingsRepository by lazy { SettingsRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReplyAiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var showSettings by remember { mutableStateOf(false) }

                    if (showSettings) {
                        SettingsScreen(
                            viewModel = SettingsViewModel(settingsRepository),
                            onBack = { showSettings = false }
                        )
                    } else {
                        HomeScreen(
                            viewModel = HomeViewModel(),
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
