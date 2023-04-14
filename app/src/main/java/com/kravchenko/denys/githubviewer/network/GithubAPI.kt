package com.kravchenko.denys.githubviewer.network

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubAPI {
    @GET("users/{user}/repos")
    fun getUserRepos(@Path("user")user: String)
}