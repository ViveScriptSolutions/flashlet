package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ImportDeckDialog(
    onDismiss: () -> Unit,
    onImport: (jsonString: String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Import Flashlet Deck",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Paste a shared Flashlet JSON export to recreate the complete deck and cards instantly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let {
                                jsonText = it
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Paste Clipboard")
                    }

                    OutlinedButton(
                        onClick = {
                            jsonText = """
                            {
                              "format": "flashlet-deck-v1",
                              "title": "Spanish Travel Phrases",
                              "description": "Essential phrases for traveling in Spanish-speaking countries.",
                              "category": "Languages",
                              "colorHex": "#E11D48",
                              "cards": [
                                { "front": "¿Dónde está la estación?", "back": "Where is the station?" },
                                { "front": "La cuenta, por favor", "back": "The bill/check, please." },
                                { "front": "¿Cuánto cuesta esto?", "back": "How much does this cost?" },
                                { "front": "Mucho gusto", "back": "Nice to meet you." }
                              ]
                            }
                            """.trimIndent()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Load Sample")
                    }
                }

                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("Deck JSON Data") },
                    placeholder = { Text("{\n  \"format\": \"flashlet-deck-v1\",\n  \"title\": \"...\"\n}") },
                    minLines = 6,
                    maxLines = 10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_json_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onImport(jsonText)
                    }
                },
                enabled = jsonText.isNotBlank(),
                modifier = Modifier.testTag("confirm_import_button")
            ) {
                Text("Import Deck")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_import_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
