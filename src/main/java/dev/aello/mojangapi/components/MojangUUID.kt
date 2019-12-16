package dev.aello.mojangapi.components

import java.util.*

class MojangUUID(val uuid: String) {
    fun getUUID(): UUID {
        return UUID.fromString(uuid.replaceFirst
        ("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"))
    }
}