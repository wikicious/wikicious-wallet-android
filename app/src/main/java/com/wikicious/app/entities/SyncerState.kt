package com.wikicious.app.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SyncerState(
    @PrimaryKey
    val key: String,
    val value: String
)
