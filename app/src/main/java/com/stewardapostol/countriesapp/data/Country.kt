package com.stewardapostol.countriesapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "countries")
@TypeConverters(Converters::class)
data class Country(
    @PrimaryKey
    val cca3: String, // ISO 3166-1 alpha-3 code (e.g., "USA")

    @SerialName("name")
    val name: CountryName? = null,

    @SerialName("capital")
    val capital: List<String>? = null,

    @SerialName("region")
    val region: String? = null,

    @SerialName("subregion")
    val subregion: String? = null,

    @SerialName("population")
    val population: Long? = null,

    @SerialName("flags")
    val flags: FlagUrls? = null,

    @SerialName("currencies")
    val currencies: Map<String, CurrencyDetail>? = null
) : AppsData()

@Serializable
data class CountryName(
    @SerialName("common")
    val common: String? = null,

    @SerialName("official")
    val official: String? = null
)

@Serializable
data class FlagUrls(
    @SerialName("png")
    val png: String? = null,

    @SerialName("svg")
    val svg: String? = null
)

@Serializable
data class CurrencyDetail(
    @SerialName("name")
    val name: String? = null,

    @SerialName("symbol")
    val symbol: String? = null
)

// Base class for app-level data models
@Serializable
open class AppsData