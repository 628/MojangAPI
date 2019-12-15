package dev.aello.mojangapi

import dev.aello.mojangapi.services.MojangService
import dev.aello.mojangapi.services.MojangStatusService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MojangAPI {
    private val mojangService = Retrofit.Builder()
            .baseUrl("https://api.mojang.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MojangService::class.java)

    /**
     * Checks to ensure a connection can be made to the Mojang API (it isn't down or inoperable)
     * @return false if a connection cannot be established or if the API is down
     */
    fun connect(): Boolean {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://status.mojang.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val statusService = retrofit.create(MojangStatusService::class.java)
        val statusResponse = statusService.checkStatus().execute()

        if (!statusResponse.isSuccessful) {
            return false
        }

        val response = statusResponse.body() ?: return false

        // Convert the list of maps into a single map
        val responseMap = response.flatMap { it.map { it.key to it.value } }.toMap()

        return (responseMap["api.mojang.com"] == "green")
    }
}