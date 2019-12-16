package dev.aello.mojangapi.adapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.util.*

class MojangUUIDAdapter : TypeAdapter<UUID>() {
    override fun write(writer: JsonWriter?, uuid: UUID?) {
        val uuidString = uuid.toString().replace("-", "")
        writer?.value(uuidString)
    }

    override fun read(reader: JsonReader?): UUID {
        val rawMojangUUID = reader?.nextString()

        return UUID.fromString(rawMojangUUID
                        ?.replaceFirst(
                                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                                "$1-$2-$3-$4-$5"))
    }

}