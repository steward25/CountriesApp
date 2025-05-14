package com.stewardapostol.countriesapp.domain

import android.util.Log
import com.stewardapostol.countriesapp.data.Country
import com.stewardapostol.countriesapp.data.CountryDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

object CountryRepositoryHelper {

    private const val TAG = "CountryRepositoryHelper"

    /**
     * Emits a list of all countries from Room DB.
     *
     * @param db The Room DB instance.
     * @return Flow emitting the list of all countries from the database.
     */
    fun getAllCountriesFromDao(db: CountryDB): Flow<List<Country>> = flow {
        emit(db.countryDao().getAllCountries())  // Ensure DAO returns Flow
    }.flowOn(Dispatchers.IO)

    /**
     * Inserts a list of countries into Room DB.
     *
     * @param db The Room DB instance.
     * @param countries List of Country objects to insert.
     */
    suspend fun insertCountriesToDao(
        db: CountryDB,
        countries: List<Country>
    ) {
        withContext(Dispatchers.IO) {
            try {
                db.countryDao().insertAll(countries)
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting countries: ${e.message}", e)
            }
        }
    }

    /**
     * Clears all countries from Room DB.
     *
     * @param db The Room DB instance.
     */
    suspend fun clearCountriesFromDao(db: CountryDB) {
        withContext(Dispatchers.IO) {
            try {
                db.countryDao().clearAll()
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing countries: ${e.message}", e)
            }
        }
    }

    /**
     * Retrieves the regions (unique) from Room DB.
     *
     * @param db The Room DB instance.
     * @return Flow emitting a list of regions.
     */
    fun getRegionsFromDao(db: CountryDB): Flow<List<String>> = flow {
        val regions = db.countryDao().getAllRegions()
        emit(regions)
    }.flowOn(Dispatchers.IO)

    /**
     * Retrieves countries for a specific region, based on region name.
     *
     * @param db The Room DB instance.
     * @param region The region name to filter countries by.
     * @return Flow emitting a list of countries from the given region.
     */
    fun getCountriesByRegionFromDao(db: CountryDB, region: String): Flow<List<Country>> = flow {
        val countries = db.countryDao().getCountriesByRegion(region)
        emit(countries)
    }.flowOn(Dispatchers.IO)

    /**
     * Retrieves countries based on region and search query from Room DB.
     *
     * @param db The Room DB instance.
     * @param region The region to filter countries by.
     * @param searchQuery The search query to filter country names.
     * @return Flow emitting a list of countries matching the search query and region.
     */
    fun searchCountriesByRegionFromDao(
        db: CountryDB,
        region: String,
        searchQuery: String
    ): Flow<List<Country>> = flow {
        val countries = db.countryDao().searchCountriesByRegion(region, searchQuery)
        emit(countries)
    }.flowOn(Dispatchers.IO)
}
