package dev.aello.mojangapi.exceptions

import java.lang.Exception

class RateLimitException (errorMessage: String) : Exception(errorMessage)