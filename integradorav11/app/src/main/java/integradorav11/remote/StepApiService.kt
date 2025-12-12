package com.example.integradorav11.remote

import com.example.integradorav11.model.StepEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface StepApiService {
    @GET("/api/steps")
    suspend fun getHistorySteps(): List<StepEntry>

    @FormUrlEncoded
    @POST("/api/steps")
    suspend fun postSteps(
        @Field("date") date: String,
        @Field("steps") steps: Int
    ): Response<Unit>

    @PUT("/api/steps/{date}")
    suspend fun updateSteps(
        @Path("date") date: String,
        @Body entry: StepEntry
    ): Response<Unit>

    @DELETE("/api/steps/{date}")
    suspend fun deleteSteps(@Path("date") date: String): Response<Unit>
}