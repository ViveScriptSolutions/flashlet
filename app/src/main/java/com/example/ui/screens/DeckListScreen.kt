package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ads.AdBanner
import com.example.data.export.DeckExporter
import com.example.data.model.Deck
import com.example.data.model.DeckWithStats
import com.example.ui.viewmodel.FlashletViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

private val CATEGORIES = listOf("All", "Languages", "Exam Prep", "Science", "Technology", "Geography")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    viewModel: FlashletViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val decksWithStats by viewModel.decksWithStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var deckToDeleteState by remember { mutableStateOf<Deck?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("deck_list_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_flashlet_icon_clean),
                            contentDescription = "Flashlet App Icon",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = "Flashlet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(
                        onClick = { isSearchVisible = !isSearchVisible },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Search Decks"
                        )
                    }

                    IconButton(
                        onClick = { DeckExporter.shareAppQuote(context) },
                        modifier = Modifier.testTag("share_quote_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Study Quote"
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("deck_list_menu_button")
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Import Deck (JSON)") },
                                leadingIcon = { Icon(Icons.Default.FileUpload, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.showImportDialog.value = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore Sample Decks") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.restoreSampleDecks()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("About Flashlet") },
                                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    viewModel.showAboutDialog.value = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.deckToEdit.value = null
                    viewModel.showCreateDeckDialog.value = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Deck") },
                modifier = Modifier.testTag("create_deck_fab")
            )
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
            // Search Input if toggled
            if (isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by deck name or note...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("search_text_input")
                )
            }

            // Categories horizontal bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CATEGORIES) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.setSelectedCategory(category) },
                        label = { Text(category) },
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }

            if (decksWithStats.isEmpty()) {
                EmptyDecksView(
                    searchQuery = searchQuery,
                    onCreateClick = {
                        viewModel.deckToEdit.value = null
                        viewModel.showCreateDeckDialog.value = true
                    },
                    onRestoreSamples = {
                        viewModel.restoreSampleDecks()
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Hero card overview
                    item {
                        HeroBannerCard(
                            decksCount = decksWithStats.size,
                            totalCards = decksWithStats.sumOf { it.totalCards },
                            overallMastery = if (decksWithStats.sumOf { it.totalCards } > 0) {
                                (decksWithStats.sumOf { it.rememberedCards } * 100) / decksWithStats.sumOf { it.totalCards }
                            } else 0,
                            onShareScore = {
                                val totalCards = decksWithStats.sumOf { it.totalCards }
                                val remembered = decksWithStats.sumOf { it.rememberedCards }
                                val percentage = if (totalCards > 0) (remembered * 100) / totalCards else 0
                                DeckExporter.shareStudyResult(
                                    context = context,
                                    deckTitle = "All Decks Combined",
                                    totalCards = totalCards,
                                    rememberedCount = remembered,
                                    percentage = percentage
                                )
                            }
                        )
                    }

                    items(decksWithStats, key = { it.deck.id }) { item ->
                        DeckCardItem(
                            deckWithStats = item,
                            onStudy = {
                                viewModel.startStudySession(item.deck.id)
                            },
                            onBrowseCards = {
                                viewModel.navigateTo(Screen.DeckDetail(item.deck.id))
                            },
                            onEdit = {
                                viewModel.deckToEdit.value = item.deck
                                viewModel.showCreateDeckDialog.value = true
                            },
                            onDuplicate = {
                                viewModel.duplicateDeck(item.deck.id)
                            },
                            onShare = {
                                coroutineScope.launch {
                                    val cards = viewModel.repository.getCardsForDeck(item.deck.id)
                                    DeckExporter.shareDeck(context, item.deck, cards)
                                }
                            },
                            onResetProgress = {
                                viewModel.resetDeckProgress(item.deck.id)
                            },
                            onDelete = {
                                deckToDeleteState = item.deck
                            }
                        )
                    }
                }
            }
        }
    }

    deckToDeleteState?.let { deck ->
        AlertDialog(
            onDismissRequest = { deckToDeleteState = null },
            title = { Text("Delete Deck?") },
            text = { Text("Are you sure you want to delete \"${deck.title}\"? All cards inside this deck will be permanently removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeck(deck)
                        deckToDeleteState = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deckToDeleteState = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HeroBannerCard(
    decksCount: Int,
    totalCards: Int,
    overallMastery: Int,
    onShareScore: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_flashlet_banner),
                    contentDescription = "Study Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Master Anything Fast",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Digital flashcards with tap-to-flip recall",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(
                            text = "$decksCount Decks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$totalCards Cards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column {
                        Text(
                            text = "Overall Recall",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$overallMastery%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedButton(
                    onClick = onShareScore,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("share_stats_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Share Progress", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun DeckCardItem(
    deckWithStats: DeckWithStats,
    onStudy: () -> Unit,
    onBrowseCards: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onShare: () -> Unit,
    onResetProgress: () -> Unit,
    onDelete: () -> Unit
) {
    val deck = deckWithStats.deck
    var menuExpanded by remember { mutableStateOf(false) }

    val accentColor = remember(deck.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(deck.colorHex))
        } catch (e: Exception) {
            Color(0xFF4F46E5)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBrowseCards() }
            .testTag("deck_card_${deck.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(7.dp)
                    .height(130.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = CircleShape,
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = deck.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Deck") },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate Deck") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onDuplicate()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Deck (JSON)") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onShare()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Reset Progress") },
                                leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onResetProgress()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Deck", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }

                if (deck.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = deck.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Stats Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${deckWithStats.totalCards} cards",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = "${deckWithStats.masteryPercentage}% mastered",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (deckWithStats.masteryPercentage > 70) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (deckWithStats.needPracticeCards > 0) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "${deckWithStats.needPracticeCards} practice",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (deckWithStats.totalCards > 0) deckWithStats.rememberedCards.toFloat() / deckWithStats.totalCards.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.2f)
                )

                Spacer(Modifier.height(12.dp))

                // Buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBrowseCards,
                        modifier = Modifier.testTag("browse_cards_${deck.id}")
                    ) {
                        Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cards")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onStudy,
                        enabled = deckWithStats.totalCards > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        modifier = Modifier.testTag("study_deck_${deck.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Study")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyDecksView(
    searchQuery: String,
    onCreateClick: () -> Unit,
    onRestoreSamples: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_flashlet_icon_clean),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Text(
                text = if (searchQuery.isNotBlank()) "No matching decks found" else "No decks yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (searchQuery.isNotBlank()) "Try a different search keyword or category filter." else "Create your first custom deck to begin memorizing, or restore ready-to-study sample decks.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onCreateClick,
                    modifier = Modifier.testTag("empty_create_deck_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Create Deck")
                }

                OutlinedButton(
                    onClick = onRestoreSamples,
                    modifier = Modifier.testTag("empty_restore_samples_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Load Samples")
                }
            }
        }
    }
}
