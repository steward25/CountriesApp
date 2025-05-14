package com.stewardapostol.countriesapp.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromCountryName(value: CountryName?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toCountryName(value: String?): CountryName? =
        value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromFlagUrls(value: FlagUrls?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toFlagUrls(value: String?): FlagUrls? =
        value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromCurrencies(value: Map<String, CurrencyDetail>?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toCurrencies(value: String?): Map<String, CurrencyDetail>? =
        value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromCapitalList(value: List<String>?): String? =
        value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toCapitalList(value: String?): List<String>? =
        value?.let { json.decodeFromString(it) }
}