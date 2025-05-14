package com.stewardapostol.countriesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Country::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CountryDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: CountryDB? = null

        fun db(context: Context): CountryDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, CountryDB::class.java, "country_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    abstract fun countryDao(): CountryDao
}