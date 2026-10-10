package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General",
    val colorHex: String = "#4F46E5",
    val createdAt: Long = System.currentTimeMillis(),
    val isSample: Boolean = false
)
