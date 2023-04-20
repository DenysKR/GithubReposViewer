package com.kravchenko.denys.githubviewer.network

import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.model.UserResponse
import okhttp3.Interceptor.*
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubAPI {
    @GET("users/{user}/repos")
    suspend fun getUserRepos(@Path("user") user: String): List<UserRepositoriesResponseItem>

    @GET("/user")
    suspend fun getUser(): UserResponse
}

