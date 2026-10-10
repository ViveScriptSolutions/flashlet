package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.Flashcard

@Composable
fun CreateEditCardDialog(
    deckId: Long,
    cardToEdit: Flashcard?,
    onDismiss: () -> Unit,
    onSave: (deckId: Long, front: String, back: String, cardId: Long?) -> Unit
) {
    var front by remember { mutableStateOf(cardToEdit?.front ?: "") }
    var back by remember { mutableStateOf(cardToEdit?.back ?: "") }
    var frontError by remember { mutableStateOf(false) }
    var backError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (cardToEdit == null) "New Flashcard" else "Edit Flashcard",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = front,
                    onValueChange = {
                        front = it
                        if (frontError && it.isNotBlank()) frontError = false
                    },
                    label = { Text("Front (Question or Term) *") },
                    placeholder = { Text("e.g. Photosynthesis, Ephemeral") },
                    isError = frontError,
                    supportingText = if (frontError) {
                        { Text("Front cannot be empty") }
                    } else null,
                    minLines = 2,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_front_input")
                )

                OutlinedTextField(
                    value = back,
                    onValueChange = {
                        back = it
                        if (backError && it.isNotBlank()) backError = false
                    },
                    label = { Text("Back (Answer or Explanation) *") },
                    placeholder = { Text("e.g. The process by which green plants...") },
                    isError = backError,
                    supportingText = if (backError) {
                        { Text("Back cannot be empty") }
                    } else null,
                    minLines = 3,
                    maxLines = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_back_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isFrontBlank = front.isBlank()
                    val isBackBlank = back.isBlank()
                    frontError = isFrontBlank
                    backError = isBackBlank

                    if (!isFrontBlank && !isBackBlank) {
                        onSave(deckId, front, back, cardToEdit?.id)
                    }
                },
                modifier = Modifier.testTag("save_card_button")
            ) {
                Text(if (cardToEdit == null) "Add Card" else "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_card_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
