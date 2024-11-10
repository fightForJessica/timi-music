package com.timi.centre.search.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.timi.centre.search.bean.History

@Dao
interface HistoryDao {

    @Insert
    fun addHistory(vararg item: History)

    @Query("DELETE FROM History")
    fun clearHistory()

    @Query("SELECT * FROM History")
    fun getAllHistory(): List<History>

    @Query("SELECT * FROM History WHERE content = :content")
    fun searchHistory(content: String): History?

}