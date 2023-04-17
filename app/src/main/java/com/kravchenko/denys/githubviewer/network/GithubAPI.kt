package com.kravchenko.denys.githubviewer.network

import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import okhttp3.Interceptor.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubAPI {
    @GET("users/{user}/repos")
    suspend fun getUserRepos(@Path("user") user: String): Response<List<UserRepositoriesResponseItem>>
}

