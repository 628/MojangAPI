package dev.aello.mojangapi.exceptions

import java.lang.Exception

class ApiDownException(errorMessage: String) : Exception(errorMessage)