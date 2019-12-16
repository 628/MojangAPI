package dev.aello.mojangapi.services

import dev.aello.mojangapi.components.MojangStatistics
import dev.aello.mojangapi.components.Player
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface MojangService {
    @GET("users/profiles/minecraft/{username}")
    fun getProfileAt(@Path("username") username: String,
                     @Query("at") at: Long = System.currentTimeMillis() / 1000): Call<Player>

    @GET("user/profiles/{uuid}/names")
    fun getNameHistory(@Path("uuid") uuid: String): Call<List<Map<String, String>>>

    @POST("profiles/minecraft")
    fun getMultipleProfiles(@Body player: List<String>): Call<List<Player>>

    @POST("orders/statistics")
    fun getStatistics(@Body metricKeys: List<String> = listOf("item_sold_minecraft")): Call<MojangStatistics>
}