package com.timi.player.store

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.timi.player.store.bean.Song

@Database(entities = [Song::class], version = 1, exportSchema = true)
abstract class SongDatabase : RoomDatabase(){

	companion object{
		private const val DATABASE_NAME = "SongDatabase"
		private var single: SongDatabase? = null

		fun getInstance(context: Context) : SongDatabase {
			if (single == null)
				single = Room
					.databaseBuilder(context.applicationContext, SongDatabase::class.java, DATABASE_NAME)
					.build()
			return single!!
		}
	}

	abstract fun songDataDao(): SongDataDao

}