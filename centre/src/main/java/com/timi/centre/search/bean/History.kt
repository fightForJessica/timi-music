package com.timi.centre.search.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class History(
    @PrimaryKey
    val content: String
)