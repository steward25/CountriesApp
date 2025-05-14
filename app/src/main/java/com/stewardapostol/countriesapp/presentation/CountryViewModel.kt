package com.stewardapostol.countriesapp.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stewardapostol.countriesapp.data.Country
import com.stewardapostol.countriesapp.data.CountryDB
import com.stewardapostol.countriesapp.domain.CountryRepository
import com.stewardapostol.countriesapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountryViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Name Input and Validation
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _nameError = MutableStateFlow<String?>(null)
    val nameError: StateFlow<String?> = _nameError

    fun onNameChange(newValue: String) {
        _name.value = newValue
        _nameError.value = validateName(newValue)
    }

    private val _isSubmitSuccessful = MutableStateFlow(false)
    val isSubmitSuccessful: StateFlow<Boolean> = _isSubmitSuccessful

    private val _submittedName = MutableStateFlow("")
    val submittedName: StateFlow<String> = _submittedName

    private val _submittedRegion = MutableStateFlow("")
    val submittedRegion: StateFlow<String> = _submittedRegion

    private fun validateName(input: String): String? {
        return when {
            input.isBlank() -> "Name is required"
            !input.matches(Regex("^[a-zA-Z\\s]+$")) -> "Only letters and spaces allowed"
            else -> null
        }
    }

    // 2. Regions (dropdown/autocomplete)
    private val _regions = MutableStateFlow<List<String>>(emptyList())
    val regions: StateFlow<List<String>> = _regions

    private val _selectedRegion = MutableStateFlow("")
    val selectedRegion: StateFlow<String> = _selectedRegion

    private val _regionError = MutableStateFlow<String?>(null)
    val regionError: StateFlow<String?> = _regionError

    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    val capitalCity: StateFlow<String> = selectedCountry.map { country ->
        country?.capital?.firstOrNull() ?: "Unknown"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Unknown"
    )

    fun onRegionSelected(region: String) {
        _selectedRegion.value = region
        _regionError.value = null
        fetchCountriesByRegion(region)
    }

    // 3. Countries (filtered by region + search)
    private val _allCountries = MutableStateFlow<Resource<List<Country>>>(Resource.Loading())
    val allCountries: StateFlow<Resource<List<Country>>> = _allCountries

    private val _filteredCountries = MutableStateFlow<List<Country>>(emptyList())
    val filteredCountries: StateFlow<List<Country>> = _filteredCountries

    private var allCountriesList: List<Country> = emptyList()

    fun onCountrySearch(query: String) {
        val filtered = allCountriesList
            .filter {
                it.region == _selectedRegion.value &&
                        (it.name?.common?.contains(query, ignoreCase = true) == true)
            }
            .sortedBy { it.name?.common ?: "" }
            .take(10)

        _filteredCountries.value = filtered
    }

    fun onCountrySearchQueryChange(query: TextFieldValue) {
        onCountrySearch(query.text)
    }

    // 4. Submit Handling
    private val _submitError = MutableStateFlow<String?>(null)
    val submitError: StateFlow<String?> = _submitError

    fun onSubmit() {
        _nameError.value = validateName(_name.value)
        _regionError.value = if (_selectedRegion.value.isBlank()) "Region is required" else null

        if (_nameError.value == null && _regionError.value == null) {
            _submitError.value = null
            _isSubmitSuccessful.value = true
            _submittedName.value = _name.value
            _submittedRegion.value = _selectedRegion.value
        } else {
            _submitError.value = "Please fix the errors above"
        }
    }

    fun onClear() {
        _name.value = ""
        _nameError.value = null
        _selectedRegion.value = ""
        _regionError.value = null
        _submitError.value = null
        _isSubmitSuccessful.value = false
        _submittedName.value = ""
        _submittedRegion.value = ""
        _filteredCountries.value = emptyList()
    }

    // 5. Fetching Data
    fun fetchAllCountries(shouldFetch: Boolean = false) {
        viewModelScope.launch {
            CountryRepository.getCountryData(getApplication(), shouldFetch).collectLatest { resource ->
                _allCountries.value = resource
                if (resource is Resource.Success) {
                    allCountriesList = resource.data ?: emptyList()
                }
            }
        }
    }

    fun fetchRegions() {
        viewModelScope.launch {
            val db = CountryDB.db(getApplication())
            val regions = withContext(Dispatchers.IO) {
                db.countryDao().getAllRegions().ifEmpty {
                    listOf("Asia", "Europe", "Africa")
                }
            }
            _regions.value = regions
        }
    }

    fun fetchCountriesByRegion(region: String) {
        viewModelScope.launch {
            val db = CountryDB.db(getApplication())
            val countries = withContext(Dispatchers.IO) {
                db.countryDao().getCountriesByRegion(region)
            }
            _filteredCountries.value = countries
        }
    }

    fun onCountrySelected(country: Country) {
        _selectedCountry.value = country
    }
}

