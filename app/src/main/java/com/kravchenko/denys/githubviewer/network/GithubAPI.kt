package com.kravchenko.denys.githubviewer.network

import com.kravchenko.denys.githubviewer.BuildConfig
import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponse
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubAPI {
    @GET("users/{user}/repos")
    suspend fun getUserRepos(@Path("user") user: String): Response<UserRepositoriesResponse>
}

object GithubHttpClient {
    private const val BASE_URL = "https://api.github.com/"

    private fun getRetrofit(): Retrofit {
        val headerAuthorizationInterceptor = Interceptor { chain ->
            var request = chain.request()
            val headers: Headers =
                request.headers.newBuilder().add(
                    "Authorization",
                    "Bearer ${BuildConfig.GITHUB_TOKEN}"
                ).build()
            request = request.newBuilder().headers(headers).build()
            chain.proceed(request)
        }
        var httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)

        var okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerAuthorizationInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: GithubAPI = getRetrofit().create(GithubAPI::class.java)
}