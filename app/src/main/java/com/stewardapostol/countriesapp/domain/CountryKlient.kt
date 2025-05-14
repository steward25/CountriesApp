package com.stewardapostol.countriesapp.domain

import com.stewardapostol.countriesapp.data.Country

interface CountryKlient {

    fun client(): io.ktor.client.HttpClient

    suspend fun getAllCountries(): List<Country>
}