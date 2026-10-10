package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ads.AdConfig
import com.example.ui.dialogs.AboutDialog
import com.example.ui.dialogs.CreateEditCardDialog
import com.example.ui.dialogs.CreateEditDeckDialog
import com.example.ui.dialogs.ImportDeckDialog
import com.example.ui.screens.DeckDetailScreen
import com.example.ui.screens.DeckListScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.screens.StudySummaryScreen
import com.example.ui.theme.FlashletTheme
import com.example.ui.viewmodel.FlashletViewModel
import com.example.ui.viewmodel.Screen
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    private val viewModel: FlashletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (AdConfig.IS_ADS_ENABLED) {
            try {
                MobileAds.initialize(this) {
                    com.example.ads.InterstitialAdManager.loadAd(this@MainActivity)
                }
            } catch (e: Exception) {
                // Graceful fallback if offline or Google Play Services unavailable
            }
        }

        setContent {
            FlashletTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlashletApp(viewModel = viewModel, activity = this)
                }
            }
        }
    }
}

@Composable
fun FlashletApp(
    viewModel: FlashletViewModel,
    activity: ComponentActivity
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Dialog state collectors
    val showCreateDeckDialog by viewModel.showCreateDeckDialog.collectAsState()
    val deckToEdit by viewModel.deckToEdit.collectAsState()
    val activeDeckForNewCard by viewModel.activeDeckForNewCard.collectAsState()
    val cardToEdit by viewModel.cardToEdit.collectAsState()
    val showImportDialog by viewModel.showImportDialog.collectAsState()
    val showAboutDialog by viewModel.showAboutDialog.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                is Screen.DeckList -> {
                    DeckListScreen(viewModel = viewModel)
                }
                is Screen.DeckDetail -> {
                    DeckDetailScreen(deckId = screen.deckId, viewModel = viewModel)
                }
                is Screen.Study -> {
                    StudyScreen(viewModel = viewModel)
                }
                is Screen.StudySummary -> {
                    androidx.compose.runtime.LaunchedEffect(screen) {
                        com.example.ads.InterstitialAdManager.showAd(activity)
                    }
                    StudySummaryScreen(
                        deckId = screen.deckId,
                        deckTitle = screen.deckTitle,
                        total = screen.total,
                        remembered = screen.remembered,
                        difficultCards = screen.difficultCards,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Dialogs
        if (showCreateDeckDialog) {
            CreateEditDeckDialog(
                deckToEdit = deckToEdit,
                onDismiss = {
                    viewModel.showCreateDeckDialog.value = false
                    viewModel.deckToEdit.value = null
                },
                onSave = { title, description, category, colorHex, id ->
                    viewModel.saveDeck(title, description, category, colorHex, id)
                }
            )
        }

        activeDeckForNewCard?.let { deckId ->
            CreateEditCardDialog(
                deckId = deckId,
                cardToEdit = cardToEdit,
                onDismiss = {
                    viewModel.activeDeckForNewCard.value = null
                    viewModel.cardToEdit.value = null
                },
                onSave = { targetDeckId, front, back, id ->
                    viewModel.saveCard(targetDeckId, front, back, id)
                }
            )
        }

        if (showImportDialog) {
            ImportDeckDialog(
                onDismiss = { viewModel.showImportDialog.value = false },
                onImport = { jsonText ->
                    viewModel.importDeckFromJson(jsonText) { success, message ->
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { viewModel.showAboutDialog.value = false }
            )
        }
    }
}
