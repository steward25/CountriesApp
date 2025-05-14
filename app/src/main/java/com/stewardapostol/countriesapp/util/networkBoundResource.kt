package com.stewardapostol.countriesapp.util
import kotlinx.coroutines.flow.*


fun <T> networkBoundResource(
    shouldFetch: () -> Boolean,
    query: () -> Flow<List<T>>,
    fetch: suspend () -> List<T>,
    saveFetchResult: suspend (List<T>?) -> Unit
): Flow<Resource<List<T>>> = flow {
    // Emit loading state
    emit(Resource.Loading())

    // Fetch from DB initially
    val dbData = query().first()

    if (shouldFetch() && dbData.isEmpty()) {
        // Fetch from network if conditions met
        try {
            val fetchedData = fetch()
            saveFetchResult(fetchedData)
            emit(Resource.Success(fetchedData))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    } else {
        // Emit data from DB if no network fetch
        emit(Resource.Success(dbData))
    }
}
