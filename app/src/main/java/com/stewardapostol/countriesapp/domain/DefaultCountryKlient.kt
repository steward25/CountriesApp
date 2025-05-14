package com.stewardapostol.countriesapp.domain

import android.util.Log
import com.stewardapostol.countriesapp.data.Country
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class DefaultCountryKlient : CountryKlient {

    override fun client(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    useAlternativeNames = true
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                })
            }

            install(ResponseObserver) {
                onResponse { response ->
                    if (response.status.value in 300..599) {
                        throw ResponseException(response, "HTTP ${response.status.value}")
                    }
                    Log.e("CountryKlient", "Response: ${response.status.value}")
                }
            }

            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

    override suspend fun getAllCountries(): List<Country> {
        return try {
            val response = client().get("https://restcountries.com/v3.1/all")
            response.body()
        } catch (e: Exception) {
            Log.e("CountryKlient", "Error in getAllCountries", e)
            return listOf()
        }
    }
}