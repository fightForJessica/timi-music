package com.timi.centre.playlistsquare.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.timi.centre.playlistsquare.room.CategoryTag

@Dao
interface CategoryDao {

	@Insert
	fun addCategoryData(vararg item: CategoryTag)

	@Insert
	fun addCategoryData(categoryTagList: List<CategoryTag>)

	@Query("DELETE FROM CategoryData")
	fun clearCategoryData()

	@Query("SELECT * FROM CategoryData")
	fun getAllCategoryData(): List<CategoryTag>

	@Query("SELECT * FROM CategoryData WHERE category = :category")
	fun getTagTypeData(category: String): List<CategoryTag>

	@Query("SELECT * FROM CategoryData WHERE hasSelect = :selected")
	fun getSelectedTypeData(selected: Boolean): List<CategoryTag>

	@Update
	fun updateCategoryData(vararg item: CategoryTag)
}