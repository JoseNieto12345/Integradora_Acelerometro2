package com.example.integradorav11.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitClient {
    
    private const val BASE_URL = "http://192.168.106.205:5000"

    val stepApiService: StepApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StepApiService::class.java)
    }
}