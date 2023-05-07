package com.example.nuevo_parchaosr.activities.map

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface retrofit2Api {
    @GET("/v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") key:String,
        @Query("start") start:String,
        @Query("end") end:String,
    ): Response<RouteResponse>
}