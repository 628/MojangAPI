package dev.aello.mojangapi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.aello.mojangapi.adapters.MojangUUIDAdapter
import dev.aello.mojangapi.components.MojangStatistics
import dev.aello.mojangapi.components.NameHistory
import dev.aello.mojangapi.components.Player
import dev.aello.mojangapi.exceptions.ApiDownException
import dev.aello.mojangapi.exceptions.RateLimitException
import dev.aello.mojangapi.services.MojangService
import dev.aello.mojangapi.services.MojangStatusService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

/**
 * MojangAPI wrapper
 */
class MojangAPI {
    private val uuidAdapter = GsonBuilder().registerTypeAdapter(UUID::class.java, MojangUUIDAdapter()).create()
    private val mojangService = Retrofit.Builder()
            .baseUrl("https://api.mojang.com/")
            .addConverterFactory(GsonConverterFactory.create(uuidAdapter))
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

    /**
     * Get the [dev.aello.mojangapi.components.Player] object for the given username.
     *
     * @param username The username of the player
     * @return [dev.aello.mojangapi.components.Player] for that username at the current time or null if that user doesn't exist
     * @throws dev.aello.mojangapi.exceptions.ApiDownException If the API is down
     * @throws dev.aello.mojangapi.exceptions.RateLimitException If the rate limit is reached
     */
    fun getPlayer(username: String): Player? {
        val profileResponse: Response<Player>

        try {
            profileResponse = mojangService.getProfileAt(username).execute().verify() ?: return null
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        return profileResponse.body()
    }

    /**
     * Get the [dev.aello.mojangapi.components.Player] object for the given UUID
     *
     * @param uuid The UUID of the player
     * @return [dev.aello.mojangapi.components.Player] for that username at the current time or null if that user doesn't exist
     * @throws dev.aello.mojangapi.exceptions.ApiDownException If the API is down
     * @throws dev.aello.mojangapi.exceptions.RateLimitException If the rate limit is reached
     */
    fun getPlayer(uuid: UUID): Player? {
        val profileResponse: Response<Player>
        val nameHistory = getNameHistory(uuid)

        if (nameHistory.isEmpty()) {
            return null
        }

        val name = nameHistory[0]

        try {
            profileResponse = mojangService.getProfileAt(name.name)
                    .execute()
                    .verify() ?: return null
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        return profileResponse.body()
    }

    /**
     * Get the [dev.aello.mojangapi.components.Player] object for the given username at a given time
     *
     * @param username Username of the player
     * @param epochSeconds The time, in epoch seconds
     * @return [dev.aello.mojangapi.components.Player] for that username at the given time or null if that user doesn't exist
     * @throws dev.aello.mojangapi.exceptions.ApiDownException
     * @throws dev.aello.mojangapi.exceptions.RateLimitException
     */
    fun getPlayerAt(username: String, epochSeconds: Long): Player? {
        val profileAtResponse: Response<Player>

        try {
            profileAtResponse = mojangService.getProfileAt(username, epochSeconds)
                    .execute()
                    .verify() ?: return null
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        return profileAtResponse.body()
    }

    /**
     * Get the [dev.aello.mojangapi.components.Player] object for the given UUID at a given time
     *
     * @param uuid UUID of the player
     * @param epochSeconds The time, in epoch seconds
     * @return [dev.aello.mojangapi.components.Player] for that UUID at the given time or null if that user doesn't exist
     * @throws dev.aello.mojangapi.exceptions.ApiDownException
     * @throws dev.aello.mojangapi.exceptions.RateLimitException
     */
    fun getPlayerAt(uuid: UUID, epochSeconds: Long): Player? {
        val profileAtResponse: Response<Player>

        if (getPlayer(uuid)?.name == null) {
            return null
        }

        try {
            profileAtResponse = mojangService.getProfileAt(getPlayer(uuid)!!.name, epochSeconds)
                    .execute()
                    .verify() ?: return null
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        return profileAtResponse.body()
    }

    /**
     * Gets a list of names that the player has had
     *
     * @param player the [dev.aello.mojangapi.components.Player]
     * @return a list of past usernames or an empty list if the request failed (the player doesn't exist)
     * @throws dev.aello.mojangapi.exceptions.ApiDownException
     * @throws dev.aello.mojangapi.exceptions.RateLimitException
     */
    fun getNameHistory(uuid: UUID): List<NameHistory> {
        val nameHistoryResponse: Response<List<NameHistory>>

        try {
            nameHistoryResponse = mojangService.getNameHistory(uuid.toString().replace("-", ""))
                    .execute()
                    .verify() ?: return emptyList()
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        if (nameHistoryResponse.body() == null) {
            return emptyList()
        }

        return nameHistoryResponse.body() ?: emptyList()
    }

    /**
     * Get multiple players in one query
     *
     * @param playerNames A list of playernames
     * @return a map of the username to the Player object or an empty map if the request failed
     * @throws dev.aello.mojangapi.exceptions.ApiDownException
     * @throws dev.aello.mojangapi.exceptions.RateLimitException
     */
    fun getPlayers(playerNames: List<String>): Map<String, Player> {
        val multipleProfilesResponse: Response<List<Player>>

        try {
            multipleProfilesResponse = mojangService.getMultipleProfiles(playerNames)
                    .execute()
                    .verify() ?: return emptyMap()
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        if (multipleProfilesResponse.body() == null) {
            return emptyMap()
        }

        return multipleProfilesResponse.body()!!.map { it.name to it }.toMap()
    }

    /**
     * Get a [dev.aello.mojangapi.components.MojangStatistics]
     *
     * @return [dev.aello.mojangapi.components.MojangStatistics] or null if the request failed
     * @throws dev.aello.mojangapi.exceptions.ApiDownException
     * @throws dev.aello.mojangapi.exceptions.RateLimitException
     */
    fun getStatistics(): MojangStatistics? {
        val statisticsResponse: Response<MojangStatistics>

        try {
            statisticsResponse = mojangService.getStatistics().execute().verify() ?: return null
        } catch (e: IOException) {
            throw ApiDownException("Could not establish a connection with the api.")
        }

        return statisticsResponse.body()
    }

    private fun <R> Response<R>.verify(): Response<R>? {
        if (!this.isSuccessful) {
            return null
        }

        if (this.code() == 429) {
            throw RateLimitException("Please do not exceed 600 requests per 10 minutes; cache responses if needed.")
        }

        return this
    }
}