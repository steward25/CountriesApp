package com.stewardapostol.countriesapp.domain

import android.app.Application
import android.util.Log
import androidx.room.withTransaction
import com.stewardapostol.countriesapp.data.Country
import com.stewardapostol.countriesapp.data.CountryDB
import com.stewardapostol.countriesapp.domain.CountryRepositoryHelper.getAllCountriesFromDao
import com.stewardapostol.countriesapp.util.Resource
import com.stewardapostol.countriesapp.util.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object CountryRepository {

    private const val TAG = "CountryRepository"

    /**
     * Fetches country data from the local database or from a remote API based on certain conditions.
     *
     * @param app The [Application] context used to access the database.
     * @param shouldFetch A flag indicating whether the data should be fetched from the network (default is false).
     * @return A [Flow] emitting [Resource] objects, which contain the country data (or error).
     */
    fun getCountryData(
        app: Application,
        shouldFetch: Boolean = false,
    ): Flow<Resource<List<Country>>> {

        val db = CountryDB.db(app)

        return flow {
            // Call networkBoundResource or implement the network/db resource handling directly
            val resource = networkBoundResource(
                shouldFetch = { shouldFetch },
                query = {
                    getAllCountriesFromDao(db) // Fetch from DB
                },
                fetch = {
                    DefaultCountryKlient().getAllCountries()  // Fetch from API
                },
                saveFetchResult = { data ->
                    data?.let {
                        db.withTransaction {
                            db.countryDao().clearAll() // Clear previous data before inserting new data
                            db.countryDao().insertAll(it) // Insert new data into DB
                        }
                    }
                }
            )

            // Collect the emitted resource (data or error) and emit it downstream
            resource.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO) // Make sure this runs on background thread for db/network
    }

    /**
     * Fetches distinct regions for countries, to be used in region-based dropdown.
     */
    fun getRegions(app: Application): Flow<List<String>> {
        val db = CountryDB.db(app)
        return flow {
            val regions = db.countryDao().getAllRegions()  // Get distinct regions from DB
            emit(regions)
        }.flowOn(Dispatchers.IO) // Use IO dispatcher for DB queries
    }

    /**
     * Fetches countries based on selected region, sorted alphabetically, with search query.
     */
    fun getCountriesByRegion(app: Application, region: String, searchQuery: String = ""): Flow<List<Country>> {
        val db = CountryDB.db(app)
        return flow {
            val countries = if (searchQuery.isEmpty()) {
                db.countryDao().getCountriesByRegion(region)  // Get countries by region if no search
            } else {
                db.countryDao().searchCountriesByRegion(region, searchQuery)  // Search within the region
            }
            emit(countries)
        }.flowOn(Dispatchers.IO)  // Make sure DB queries run in background thread
    }
}
