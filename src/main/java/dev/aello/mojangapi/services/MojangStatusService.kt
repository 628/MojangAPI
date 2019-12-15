package dev.aello.mojangapi.services

import retrofit2.Call
import retrofit2.http.GET

interface MojangStatusService {
    @GET("check")
    fun checkStatus(): Call<List<Map<String, String>>>
}