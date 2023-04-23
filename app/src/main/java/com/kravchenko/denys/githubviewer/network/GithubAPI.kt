package com.kravchenko.denys.githubviewer.network

import com.kravchenko.denys.githubviewer.model.UserRepositoriesResponseItem
import com.kravchenko.denys.githubviewer.model.UserResponse
import okhttp3.Interceptor.*
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GithubAPI {
    @GET("users/{user}/repos")
    suspend fun getUserRepos(@Path("user") user: String): List<UserRepositoriesResponseItem>

    @GET("/user")
    suspend fun fetchCurrentUser(): UserResponse

    @PUT("/user/starred/{owner}/{repo}")
    suspend fun starRepo(
        @Path("owner") ownerName: String,
        @Path("repo") repoName: String,
    ): Response<Unit>

    @DELETE("/user/starred/{owner}/{repo}")
    suspend fun unStarRepo(
        @Path("owner") ownerName: String,
        @Path("repo") repoName: String,
    ): Response<Unit>

    @GET("/repos/{owner}/{repo}/stargazers")
    suspend fun fetchStargazers(
        @Path("owner") ownerName: String,
        @Path("repo") repoName: String,
    ): List<UserResponse>

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun fetchContributors(
        @Path("owner") ownerName: String,
        @Path("repo") repoName: String,
    ): List<UserResponse>

    @GET("/users/{user}/followers")
    suspend fun fetchFollowers(
        @Path("user") userName: String
    ): List<UserResponse>

    @GET("/users/{user}/following")
    suspend fun fetchFollowing(
        @Path("user") userName: String
    ): List<UserResponse>

    @GET("/users/{name}")
    suspend fun fetchUser(
        @Path("name") ownerName: String,
    ): UserResponse
}

