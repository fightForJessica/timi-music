package com.timi.centre.playlistsquare.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CategoryData")
data class CategoryTag(
	@PrimaryKey
	val name: String,
	val category: String,
	var hasSelect: Boolean,
	val hot: Boolean
)