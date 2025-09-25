package com.example.vendprokiosk

import android.util.Log // Added for logging
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/machines/{machineId}")
    suspend fun getMachineDetails(
        @Path("machineId") machineId: String
    ): ApiMachineDetails
}

object RetrofitClient {
    private const val BASE_URL = "https://studio--studio-6864668644-fa8e7.us-central1.hosted.app/" // Updated BASE_URL
    // API_KEY constant has been removed as it's no longer required by the API for testing

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            // Logging the outgoing request details (URL and headers)
            Log.d("ApiServiceInterceptor", "Outgoing request URL: ${originalRequest.url}")
            Log.d("ApiServiceInterceptor", "Outgoing request headers: ${originalRequest.headers}")
            
            // Proceed with the original request, no API key header is added
            chain.proceed(originalRequest)
        }
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(ApiService::class.java)
    }
}
