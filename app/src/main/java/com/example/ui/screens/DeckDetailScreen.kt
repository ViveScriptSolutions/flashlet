package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ads.AdBanner
import com.example.data.export.DeckExporter
import com.example.data.model.Flashcard
import com.example.ui.viewmodel.FlashletViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deckId: Long,
    viewModel: FlashletViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allDecksWithStats by viewModel.decksWithStats.collectAsState()
    val currentDeckWithStats = allDecksWithStats.firstOrNull { it.deck.id == deckId }
    val deck = currentDeckWithStats?.deck

    val cardsFlow = remember(deckId) { viewModel.repository.getCardsForDeckFlow(deckId) }
    val cards by cardsFlow.collectAsState(initial = emptyList())

    var cardSearchQuery by remember { mutableStateOf("") }
    var cardToDeleteState by remember { mutableStateOf<Flashcard?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    val filteredCards = remember(cards, cardSearchQuery) {
        if (cardSearchQuery.isBlank()) cards
        else cards.filter {
            it.front.contains(cardSearchQuery, ignoreCase = true) ||
            it.back.contains(cardSearchQuery, ignoreCase = true)
        }
    }

    val accentColor = remember(deck?.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(deck?.colorHex ?: "#4F46E5"))
        } catch (e: Exception) {
            Color(0xFF4F46E5)
        }
    }

    Scaffold(
        modifier = modifier.testTag("deck_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = deck?.title ?: "Deck",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Decks")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (deck != null) {
                                coroutineScope.launch {
                                    val cardsToShare = viewModel.repository.getCardsForDeck(deck.id)
                                    DeckExporter.shareDeck(context, deck, cardsToShare)
                                }
                            }
                        },
                        modifier = Modifier.testTag("share_deck_detail_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Deck")
                    }

                    IconButton(
                        onClick = {
                            if (deck != null) {
                                viewModel.deckToEdit.value = deck
                                viewModel.showCreateDeckDialog.value = true
                            }
                        },
                        modifier = Modifier.testTag("edit_deck_detail_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Deck")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.activeDeckForNewCard.value = deckId
                    viewModel.cardToEdit.value = null
                },
                containerColor = accentColor,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_card_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        },
        bottomBar = {
            AdBanner()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Deck Summary Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!deck?.description.isNullOrBlank()) {
                        Text(
                            text = deck!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                    }

                    // Stat badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "${cards.size} Total",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        val remembered = cards.count { it.difficulty == 1 }
                        if (remembered > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "$remembered Known",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF059669)
                                    )
                                }
                            }
                        }

                        val needPractice = cards.count { it.difficulty == 2 }
                        if (needPractice > 0) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "$needPractice Practice",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Study Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startStudySession(deckId, false) },
                            enabled = cards.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("start_study_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Study All (${cards.size})")
                        }

                        val difficultCount = cards.count { it.difficulty == 2 }
                        if (difficultCount > 0) {
                            OutlinedButton(
                                onClick = { viewModel.startStudySession(deckId, true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("study_difficult_button")
                            ) {
                                Text("Difficult ($difficultCount)")
                            }
                        }
                    }
                }
            }

            // Cards Search Filter
            if (cards.size > 3) {
                OutlinedTextField(
                    value = cardSearchQuery,
                    onValueChange = { cardSearchQuery = it },
                    placeholder = { Text("Filter cards in deck...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (cardSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { cardSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Cards list
            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (cards.isEmpty()) "This deck has no cards yet" else "No cards match filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (cards.isEmpty()) "Tap '+' below to add your first question and answer card." else "Clear search to see all cards.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (cards.isEmpty()) {
                            Button(
                                onClick = {
                                    viewModel.activeDeckForNewCard.value = deckId
                                    viewModel.cardToEdit.value = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Add Card")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCards, key = { it.id }) { card ->
                        CardRowItem(
                            card = card,
                            onEdit = {
                                viewModel.activeDeckForNewCard.value = deckId
                                viewModel.cardToEdit.value = card
                            },
                            onDelete = {
                                cardToDeleteState = card
                            }
                        )
                    }
                }
            }
        }
    }

    cardToDeleteState?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDeleteState = null },
            title = { Text("Delete Card?") },
            text = { Text("Are you sure you want to delete this card?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCard(card)
                        cardToDeleteState = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDeleteState = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CardRowItem(
    card: Flashcard,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_item_${card.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FRONT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (card.difficulty) {
                        1 -> Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Learned",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        2 -> Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "Needs Review",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Card", modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Card", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "BACK",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = card.back,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
