# 🌍 CountriesApp

A Kotlin-based app that helps users discover countries, regions, and capitals. It uses the **REST Countries API** to fetch country data and allows users to search and filter countries by name and region. The app is built with Jetpack Compose and Kotlin Coroutines, providing a clean, responsive UI and smooth user experience.
## 📸 Screenshot
<p align="center" style="background-color:#f0f0f0;">
    <img src="/screenshots/final_record.gif" alt="CountriesApp" width="200" style="margin-right: 20px;"/>
</p>
## 🔑 Getting Started

## 📦 Download APK

🔗 **[Download APK](https://github.com/steward25/CountriesApp/tree/main/apk)**  
📱 Simply install it on your Android device and start exploring countries around the world!

### 1. Clone the repository

```bash
git clone https://github.com/your-username/countriesapp.git
cd countriesapp
```

## 📦 Project Structure

```
- data/
  - Converters.kt             // Likely handles data type conversions for Room
  - Country.kt                // Data class representing a country
  - CountryDao.kt             // Data Access Object for the Country entity
  - CountryDB.kt              // Room database setup for country data
- domain/
  - CountryKlient.kt          // Interface for fetching country data (likely from network)
  - CountryRepository.kt      // Repository for managing country data access
  - CountryRepositoryHelper.kt // Helper class for the CountryRepository
  - DefaultCountryKlient.kt   // Default implementation of CountryKlient
- presentation/
  - ui.theme/               // Contains UI theme configurations
  - CountryViewModel.kt       // ViewModel for the country-related UI
  - MainActivity.kt           // Main activity of the application
- util/
  - networkBoundResource.kt   // Handles data fetching and caching logic
  - Resource.kt               // Wrapper for resource state (loading, success, error)
```

## 🛠️ Technologies Used

- **Kotlin**: Programming language.
- **Jetpack Compose**: For building the UI.
- **Room**: For local data storage.
- **Ktor**: For making HTTP requests to the get all countries information.
- **Coroutines**: For managing background tasks.
- **MVVM (Model-View-ViewModel)**: For separation of concerns.
