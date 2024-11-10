package com.timi.centre.search.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.timi.centre.search.bean.History

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase(){

    companion object{

        private const val DATABASE_NAME = "HistoryDatabase"
        private var single: HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            if (single == null){
                single = Room
                    .databaseBuilder(context.applicationContext, HistoryDatabase::class.java, DATABASE_NAME)
                    .build()
            }
            return single!!
        }
    }

    abstract fun historyDao(): HistoryDao

}
