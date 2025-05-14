package com.stewardapostol.countriesapp.util

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable?, data: T? = null) : Resource<T>(data, throwable)

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)

        fun <T> loading(data: T? = null): Resource<T> = Loading(data)

        fun <T> error(throwable: Throwable?, data: T? = null): Resource<T> =
            Error(throwable, data)
    }
}