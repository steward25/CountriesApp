package com.stewardapostol.countriesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CountryDao {

    @Query("SELECT * FROM countries")
    fun getAllCountries(): List<Country>

    @Query("SELECT DISTINCT region FROM countries WHERE region IS NOT NULL ORDER BY region ASC")
    fun getAllRegions(): List<String>

    @Query("""
        SELECT * FROM countries
        WHERE region = :region
        ORDER BY name ASC
        LIMIT 10
    """)
    fun getCountriesByRegion(region: String): List<Country>

    @Query("""
        SELECT * FROM countries
        WHERE region = :region AND name LIKE '%' || :search || '%'
        ORDER BY name ASC
        LIMIT 10
    """)
    fun searchCountriesByRegion(region: String, search: String): List<Country>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<Country>)

    @Query("DELETE FROM countries")
    suspend fun clearAll()
}