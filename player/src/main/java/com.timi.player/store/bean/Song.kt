package com.timi.player.store.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SongData")
data class Song(
	val songId: Long,
	val songName: String,
	val artistAndDescription: String,
	val picUrl: String
){
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0
}