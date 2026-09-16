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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.replyai.android.data.AppContainer
import com.replyai.android.domain.usecase.GenerateReplyUseCase
import com.replyai.android.presentation.home.HomeScreen
import com.replyai.android.presentation.home.HomeViewModel
import com.replyai.android.presentation.settings.SettingsScreen
import com.replyai.android.presentation.settings.SettingsViewModel
import com.replyai.android.ui.theme.ReplyAiTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReplyAiTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var showSettings by remember { mutableStateOf(false) }
                    val settings by appContainer.settingsRepository.aiSettings
                        .collectAsStateWithLifecycle()

                    if (showSettings) {
                        SettingsScreen(
                            viewModel = remember {
                                SettingsViewModel(
                                    repository = appContainer.settingsRepository,
                                    secureSecretStore = appContainer.secureSecretStore
                                )
                            },
                            onBack = { showSettings = false }
                        )
                    } else {
                        val provider = remember(settings) {
                            appContainer.aiProviderResolver.resolve(settings)
                        }
                        val viewModel = remember(provider) {
                            HomeViewModel(GenerateReplyUseCase(provider))
                        }

                        HomeScreen(
                            viewModel = viewModel,
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
