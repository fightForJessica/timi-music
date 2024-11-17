package com.timi.player.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.timi.player.store.bean.Song

@Dao
interface SongDataDao {

	@Insert
	fun addSongData(vararg item: Song)

	@Query("DELETE FROM SongData")
	fun clearSongData()

	@Query("SELECT * FROM SongData")
	fun getAllSongData(): List<Song>

	@Query("SELECT * FROM SongData WHERE songId = :songId")
	fun searchSongData(songId: Long): Song?

	@Query("DELETE FROM SongData WHERE songId = :songId")
	fun deleteSongData(songId: Long)
}