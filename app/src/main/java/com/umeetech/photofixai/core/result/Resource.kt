package com.umeetech.photofixai.core.result

/**
 * Generic wrapper for the result of an operation that can succeed or fail.
 * Used across repositories, use cases and services.
 */
sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val error: AppError) : Resource<Nothing>
    data object Loading : Resource<Nothing>

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun error(error: AppError): Resource<Nothing> = Error(error)
    }
}

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(data))
    is Resource.Error -> this
    Resource.Loading -> Resource.Loading
}

inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T> Resource<T>.onError(action: (AppError) -> Unit): Resource<T> {
    if (this is Resource.Error) action(error)
    return this
}

/**
 * Runs [block] and wraps any thrown exception into a [Resource.Error] using the
 * supplied [errorMapper]. Keeps repositories/use cases free of scattered try/catch.
 */
inline fun <T> runCatchingResource(
    errorMapper: (Throwable) -> AppError = { AppError.Unknown(it.message ?: "Unknown error") },
    block: () -> T
): Resource<T> = try {
    Resource.Success(block())
} catch (t: Throwable) {
    Resource.Error(errorMapper(t))
}
