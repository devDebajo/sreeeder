package ru.debajo.reader.rss.data.remote.service

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceFactory(
    private val gson: Gson,
    private val okHttpClient: OkHttpClient,
) {
    inline fun <reified T> createService(baseUrl: String): T {
        return createService(baseUrl, T::class.java)
    }

    fun <T> createService(baseUrl: String, slazz: Class<T>): T {
        val retrofit = buildRetrofit(baseUrl)
        return retrofit.create(slazz)
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }
}
