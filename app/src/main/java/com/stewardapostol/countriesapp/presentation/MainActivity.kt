package com.stewardapostol.countriesapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stewardapostol.countriesapp.presentation.ui.theme.CountriesAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    val countryViewModel: CountryViewModel by viewModels()


    override fun onStart() {
        super.onStart()
        countryViewModel.fetchAllCountries(true)
        countryViewModel.fetchRegions()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountriesAppTheme {
                CountryScreen(countryViewModel)
            }
        }
    }
}

@Composable
fun CountryScreen(viewModel: CountryViewModel) {

    LaunchedEffect(Unit) {
        // Fetch all countries with a delay
        viewModel.fetchAllCountries(true)

        // Delay before fetching regions
        delay(5000) // Delay in milliseconds (5 seconds in this case)

        // Fetch regions after the delay
        viewModel.fetchRegions()
    }

    // Collecting state from the ViewModel
    val name by viewModel.name.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()

    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val countrySelected by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val regionError by viewModel.regionError.collectAsStateWithLifecycle()
    val submitError by viewModel.submitError.collectAsStateWithLifecycle()
    val isSubmitSuccessful by viewModel.isSubmitSuccessful.collectAsStateWithLifecycle()

    val submittedName by viewModel.submittedName.collectAsStateWithLifecycle()
    val submittedRegion by viewModel.submittedRegion.collectAsStateWithLifecycle()

    val allCountries by viewModel.allCountries.collectAsStateWithLifecycle()
    val regions by viewModel.regions.collectAsStateWithLifecycle()
    val filteredCountries by viewModel.filteredCountries.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    var regionExpanded by rememberSaveable { mutableStateOf(false) }
    var countrySearchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var isDropdownVisible by rememberSaveable { mutableStateOf(true) }

    // Observe focus/tap interaction
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                regionExpanded = true
            }
        }
    }

    if (isSubmitSuccessful) {
        SuccessScreen(
            name = submittedName,
            region = submittedRegion,
            countryValue = countrySelected?.name?.official.toString(),
            capitalCity = viewModel.capitalCity.collectAsState().value
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Name") },
                isError = nameError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            nameError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Adds space between elements

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedRegion,
                    onValueChange = {},
                    label = { Text(text = "Region", color = Color.White) },
                    readOnly = true,
                    isError = regionError != null,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { regionExpanded = !regionExpanded }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Toggle dropdown"
                            )
                        }
                    },
                    interactionSource = interactionSource // Attach the interaction source
                )
                DropdownMenu(
                    expanded = regionExpanded,
                    onDismissRequest = { regionExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    regions.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(text = region, color = Color.White) },
                            onClick = {
                                viewModel.onRegionSelected(region)
                                regionExpanded = false
                            }
                        )
                    }
                }
            }

            regionError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Adds more space between sections

            // Country Search with Region Filtering
            if (selectedRegion.isNotBlank()) {
                OutlinedTextField(
                    value = countrySearchQuery,
                    onValueChange = {
                        countrySearchQuery = it
                        viewModel.onCountrySearchQueryChange(it)
                    },
                    label = { Text(text = "Search Country", color = Color.White) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Filtered Country List
                if (isDropdownVisible && countrySearchQuery.text.isNotBlank() && filteredCountries.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Log.e("Card", "Card clicked")
                            }
                    ) {
                        Column {
                            filteredCountries.forEach { country ->
                                TextButton(
                                    onClick = {
                                        viewModel.onCountrySelected(country)
                                        isDropdownVisible = false
                                        countrySearchQuery =
                                            TextFieldValue(country.name?.common ?: "")
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = country.name?.common ?: "Unknown Country",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Submit and Clear Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = viewModel::onSubmit, modifier = Modifier.weight(1f)) {
                    Text("SUBMIT")
                }
                Button(onClick = {
                    viewModel.onClear()
                    countrySearchQuery = TextFieldValue("")
                    isDropdownVisible = true
                }, modifier = Modifier.weight(1f)) {
                    Text("CLEAR")
                }
            }

            submitError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun SuccessScreen(name: String, region: String, countryValue: String, capitalCity: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp) // Ensures content is centered vertically
        ) {
            // Display greeting
            Text(
                text = "Hi $name",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp), // Large text size
                color = Color.White
            )

            // Display region and country value
            Text(
                text = "You are from $region, $countryValue",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp), // Slightly smaller text
                color = Color.White
            )

            // Display capital city
            Text(
                text = "Your Capital City is: $capitalCity",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp), // Large text size
                color = Color.White
            )
        }
    }
}
