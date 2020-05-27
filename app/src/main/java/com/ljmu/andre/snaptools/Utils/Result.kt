package com.ljmu.andre.snaptools.Utils


/**
 * This file was created by Jacques Hoffmann (jaqxues) in the Project SnapTools.<br>
 * Date: 13.05.20 - Time 14:20.
 */

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

sealed class Request<out T> {
    object Pending : Request<Nothing>()
    class Loaded<out T>(val result: Result<T>) : Request<T>()

    val success: Boolean
        get() = this@Request is Loaded && result is Result.Success
}