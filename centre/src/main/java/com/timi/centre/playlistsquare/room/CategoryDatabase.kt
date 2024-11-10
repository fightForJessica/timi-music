package com.timi.centre.playlistsquare.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CategoryTag::class], version = 1, exportSchema = true)
abstract class CategoryDatabase : RoomDatabase() {

	companion object{
		private const val DATABASE_NAME = "CategoryDatabase"
		private var single: CategoryDatabase? = null

		fun getInstance(context: Context): CategoryDatabase {
			if (single == null){
				single = Room.databaseBuilder(
					context.applicationContext,
					CategoryDatabase::class.java,
					DATABASE_NAME
				).build()
			}
			return single!!
		}
	}

	abstract fun categoryDao(): CategoryDao

}